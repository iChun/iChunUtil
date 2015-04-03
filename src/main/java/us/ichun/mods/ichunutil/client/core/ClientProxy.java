package us.ichun.mods.ichunutil.client.core;

import com.google.common.base.Splitter;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import us.ichun.mods.ichunutil.client.gui.GuiModUpdateNotification;
import us.ichun.mods.ichunutil.client.keybind.KeyBind;
import us.ichun.mods.ichunutil.client.layer.LayerSnout;
import us.ichun.mods.ichunutil.client.thread.ThreadGetPatrons;
import us.ichun.mods.ichunutil.client.thread.ThreadStatistics;
import us.ichun.mods.ichunutil.client.voxel.EntityTrail;
import us.ichun.mods.ichunutil.client.voxel.RenderVoxels;
import us.ichun.mods.ichunutil.client.voxel.TrailTicker;
import us.ichun.mods.ichunutil.common.core.CommonProxy;
import us.ichun.mods.ichunutil.common.core.EntityHelperBase;
import us.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import us.ichun.mods.ichunutil.common.core.util.ResourceHelper;
import us.ichun.mods.ichunutil.common.iChunUtil;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit()
    {
        super.preInit();
        ResourceHelper.init();

        File file = new File(ResourceHelper.getConfigFolder(), "iChunUtil_KeyBinds.cfg");
        ConfigHandler.configKeybind = new Configuration(file);
        ConfigHandler.configKeybind.load();

        List cms = Splitter.on("\\n").splitToList(StatCollector.translateToLocal("ichunutil.config.cat.keybind.comment"));
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

        tickHandlerClient = new TickHandlerClient();
        FMLCommonHandler.instance().bus().register(tickHandlerClient);

        (new ThreadGetPatrons()).start();

        EntityHelperBase.injectMinecraftPlayerGameProfile();
    }

    @Override
    public void init()
    {
        super.init();

        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(iChunUtil.blockCompactPorkchop), 0, new ModelResourceLocation("ichunutil:compactPorkchop", "inventory"));

        trailTicker = new TrailTicker();
        FMLCommonHandler.instance().bus().register(trailTicker);
        RenderingRegistry.registerEntityRenderingHandler(EntityTrail.class, new RenderVoxels());

        RenderPlayer renderPlayer = ((RenderPlayer)Minecraft.getMinecraft().getRenderManager().skinMap.get("default"));
        renderPlayer.addLayer(new LayerSnout(renderPlayer));
        renderPlayer = ((RenderPlayer)Minecraft.getMinecraft().getRenderManager().skinMap.get("slim"));
        renderPlayer.addLayer(new LayerSnout(renderPlayer));
    }

    @Override
    public void postInit()
    {
        super.postInit();
        ThreadStatistics.checkFirstLaunch();
    }

    @Override
    public GameProfileRepository createProfileRepo()
    {
        return ((new YggdrasilAuthenticationService(Minecraft.getMinecraft().getProxy(), UUID.randomUUID().toString()))).createProfileRepository();
    }

    @Override
    public MinecraftSessionService getSessionService()
    {
        return Minecraft.getMinecraft().getSessionService();
    }

    @Override
    public void notifyNewUpdate(String modName, String version)
    {
        versionChecker.put(modName, version);
        if(tickHandlerClient.modUpdateNotification == null)
        {
            tickHandlerClient.modUpdateNotification = new GuiModUpdateNotification(Minecraft.getMinecraft());
        }
        tickHandlerClient.modUpdateNotification.addModUpdate(modName, version);
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
            for(int i = tickHandlerClient.keyBindList.size() - 1; i >= 0; i--)
            {
                KeyBind keybind = tickHandlerClient.keyBindList.get(i);
                if(keybind.equals(replacing))
                {
                    keybind.usages--;
                    if(keybind.usages <= 0)
                    {
                        tickHandlerClient.keyBindList.remove(i);
                    }
                    bind.setPulse(keybind.canPulse, keybind.pulseTime);
                    bind.ignoreHold = keybind.ignoreHold;
                }
            }
        }

        for(KeyBind keybind : tickHandlerClient.keyBindList)//Check to see if the keybind is already registered. If it is, increase usages count. If not, add it.
        {
            if(keybind.equals(bind))
            {
                keybind.usages++;
                return keybind;
            }
        }
        bind.usages++;
        tickHandlerClient.keyBindList.add(bind);
        return bind;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerMinecraftKeyBind(KeyBinding bind)
    {
        tickHandlerClient.mcKeyBindList.put(bind, (new KeyBind(bind.getKeyCode(), false, false, false, true)).setIsMinecraftBind());
    }

}
