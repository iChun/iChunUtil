package ichun.common.core;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ichun.client.core.TickHandlerClient;
import ichun.client.keybind.KeyBind;
import ichun.common.core.util.EventCalendar;
import ichun.common.iChunUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.server.MinecraftServer;

import java.util.HashMap;

public class CommonProxy
{
    public static TickHandlerClient tickHandlerClient;
    public static HashMap<String, String> versionChecker = new HashMap<String, String>();
    public static HashMap<String, String> prevVerChecker = new HashMap<String, String>();

    public void init()
    {
        EventCalendar.checkDate();
    }

    public GameProfileRepository createProfileRepo()
    {
        return FMLCommonHandler.instance().getMinecraftServerInstance().func_152359_aw();
    }

    public MinecraftSessionService getSessionService()
    {
        return MinecraftServer.getServer().func_147130_as();
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
