package us.ichun.mods.ichunutil.common.core;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import us.ichun.mods.ichunutil.client.core.TickHandlerClient;
import us.ichun.mods.ichunutil.client.keybind.KeyBind;
import us.ichun.mods.ichunutil.client.voxel.TrailTicker;
import us.ichun.mods.ichunutil.common.core.util.EventCalendar;
import us.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.server.MinecraftServer;

import java.util.HashMap;

public class CommonProxy
{
    public static TickHandlerClient tickHandlerClient;
    public static TrailTicker trailTicker;
    public static HashMap<String, String> versionChecker = new HashMap<String, String>();
    public static HashMap<String, String> prevVerChecker = new HashMap<String, String>();

    public void preInit()
    {
        EventCalendar.checkDate();
    }

    public void init()
    {
    }

    public GameProfileRepository createProfileRepo()
    {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getGameProfileRepository();
    }

    public MinecraftSessionService getSessionService()
    {
        return MinecraftServer.getServer().getMinecraftSessionService();
    }

    /**
     * This is just a proxy method to add a new update notification.
     * Actual update checking should be done by the mod itself.
     * The one supplied with iChunUtil is made to check for updates for my (iChun) mods.
     * Feel free to reference that and write your own.
     * @param modName
     * @param version
     */
    public void notifyNewUpdate(String modName, String version)
    {
        versionChecker.put(modName, version);
        iChunUtil.console("[NEW UPDATE AVAILABLE] " + modName + " - " + version);
    }

    @SideOnly(Side.CLIENT)
    public KeyBind registerKeyBind(KeyBind bind, KeyBind replacing) { return bind; }

    /**
     * Please note that this keybind will trigger without checking for SHIFT/CTRL/ALT being held down. That checking has to be done on your end.
     * @param bind Minecraft Keybind
     */
    @SideOnly(Side.CLIENT)
    public void registerMinecraftKeyBind(KeyBinding bind) {}
}
