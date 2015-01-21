package us.ichun.mods.ichunutil.client.core;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import us.ichun.mods.ichunutil.client.gui.GuiModUpdateNotification;
import us.ichun.mods.ichunutil.client.keybind.KeyBind;
import us.ichun.mods.ichunutil.client.thread.ThreadGetPatrons;
import us.ichun.mods.ichunutil.client.voxel.EntityTrail;
import us.ichun.mods.ichunutil.client.voxel.RenderVoxels;
import us.ichun.mods.ichunutil.client.voxel.TrailTicker;
import us.ichun.mods.ichunutil.common.core.CommonProxy;
import us.ichun.mods.ichunutil.common.core.config.Config;
import us.ichun.mods.ichunutil.common.core.util.ResourceHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.UUID;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit()
    {
        super.preInit();
        ResourceHelper.init();

        File file = new File(ResourceHelper.getConfigFolder(), "iChunUtil_KeyBinds.cfg");
        Config.configKeybind = new Configuration(file);
        Config.configKeybind.load();

        tickHandlerClient = new TickHandlerClient();
        FMLCommonHandler.instance().bus().register(tickHandlerClient);

        (new ThreadGetPatrons()).start();
    }

    @Override
    public void init()
    {
        trailTicker = new TrailTicker();
        FMLCommonHandler.instance().bus().register(trailTicker);
        RenderingRegistry.registerEntityRenderingHandler(EntityTrail.class, new RenderVoxels());
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
