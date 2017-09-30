package me.ichun.mods.ichunutil.client.core;

import com.google.common.base.Splitter;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import me.ichun.mods.ichunutil.client.core.event.EventHandlerClient;
import me.ichun.mods.ichunutil.client.entity.EntityLatchedRenderer;
import me.ichun.mods.ichunutil.client.keybind.KeyBind;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import me.ichun.mods.ichunutil.client.render.entity.RenderBlock;
import me.ichun.mods.ichunutil.client.render.entity.RenderLatchedRenderer;
import me.ichun.mods.ichunutil.common.core.ProxyCommon;
import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.core.util.ResourceHelper;
import me.ichun.mods.ichunutil.common.entity.EntityBlock;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class ProxyClient extends ProxyCommon
{
    @Override
    public void preInit()
    {
        super.preInit();
        ResourceHelper.init();
        RendererHelper.init();

        File file = new File(ResourceHelper.getConfigFolder(), "ichunutil_keybinds.cfg");
        ConfigHandler.configKeybind = new Configuration(file);
        ConfigHandler.configKeybind.load();

        List cms = Splitter.on("\\n").splitToList(I18n.translateToLocal("ichunutil.config.cat.keybind.comment"));
        String cm = "";
        for(int ll = 0; ll < cms.size(); ll++)
        {
            cm = cm + cms.get(ll);
            if(ll != cms.size() - 1)
            {
                cm = cm + "\n";
            }
        }
        ConfigHandler.configKeybind.addCustomCategoryComment("keybinds", cm);

        EntityHelper.injectMinecraftPlayerGameProfile();

        iChunUtil.eventHandlerClient = new EventHandlerClient();
        MinecraftForge.EVENT_BUS.register(iChunUtil.eventHandlerClient);

        RenderingRegistry.registerEntityRenderingHandler(EntityLatchedRenderer.class, new RenderLatchedRenderer.RenderFactory());
        RenderingRegistry.registerEntityRenderingHandler(EntityBlock.class, new RenderBlock.RenderFactory());
    }

    @Override
    public void postInit()
    {
        super.postInit();
        if(ConfigHandler.configKeybind.hasChanged())
        {
            ConfigHandler.configKeybind.save();
        }
    }

    @Override
    public String getPlayerId()
    {
        return Minecraft.getMinecraft().getSession().getPlayerID().replaceAll("-", "");
    }

    @Override
    public void setGameProfileLookupService()
    {
        YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(Minecraft.getMinecraft().getProxy(), UUID.randomUUID().toString());
        EntityHelper.sessionService = yggdrasilauthenticationservice.createMinecraftSessionService();
        GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();
        EntityHelper.profileCache = new PlayerProfileCache(gameprofilerepository, new File(Minecraft.getMinecraft().mcDataDir, MinecraftServer.USER_CACHE_FILE.getName()));
    }

    @Override
    public void adjustRotation(Entity ent, float yawChange, float pitchChange)
    {
        float prevYaw = 0.0F;
        float yaw = 0.0F;
        float prevPitch = 0.0F;
        float pitch = 0.0F;

        if(ent instanceof EntityPlayerSP)
        {
            EntityPlayerSP player = (EntityPlayerSP)ent;
            prevYaw = player.prevRotationYaw - player.prevRenderArmYaw;
            yaw = player.rotationYaw - player.renderArmYaw;
            prevPitch = player.prevRotationPitch - player.prevRenderArmPitch;
            pitch = player.rotationPitch - player.renderArmPitch;
        }

        super.adjustRotation(ent, yawChange, pitchChange);

        if(ent instanceof EntityPlayerSP)
        {
            EntityPlayerSP player = (EntityPlayerSP)ent;
            player.prevRenderArmYaw = player.prevRotationYaw;
            player.renderArmYaw = player.rotationYaw;
            player.prevRenderArmPitch = player.prevRotationPitch;
            player.renderArmPitch = player.rotationPitch;
            player.prevRenderArmYaw -= prevYaw;
            player.renderArmYaw -= yaw;
            player.prevRenderArmPitch -= prevPitch;
            player.renderArmPitch -= pitch;
        }
    }

    @Override
    public void nudgeHand(float mag)
    {
        Minecraft.getMinecraft().player.renderArmPitch += mag;
    }

    @Override
    public EntityPlayer getMcPlayer()
    {
        return Minecraft.getMinecraft().player;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public KeyBind registerKeyBind(KeyBind bind, KeyBind replacing)
    {
        if(replacing != null)
        {
            if(bind.equals(replacing))
            {
                return replacing;
            }
            for(int i = iChunUtil.eventHandlerClient.keyBindList.size() - 1; i >= 0; i--)
            {
                KeyBind keybind = iChunUtil.eventHandlerClient.keyBindList.get(i);
                if(keybind.equals(replacing))
                {
                    keybind.usages--;
                    if(keybind.usages <= 0)
                    {
                        iChunUtil.eventHandlerClient.keyBindList.remove(i);
                    }
                    bind.ignoreHold = keybind.ignoreHold;
                }
            }
        }

        for(KeyBind keybind : iChunUtil.eventHandlerClient.keyBindList)//Check to see if the keybind is already registered. If it is, increase usages count. If not, add it.
        {
            if(keybind.equals(bind))
            {
                keybind.usages++;
                return keybind;
            }
        }
        bind.usages++;
        iChunUtil.eventHandlerClient.keyBindList.add(bind);
        return bind;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerMinecraftKeyBind(KeyBinding bind)
    {
        iChunUtil.eventHandlerClient.mcKeyBindList.put(bind, (new KeyBind(bind.getKeyCode(), false, false, false, true)).setIsMinecraftBind());
    }
}
