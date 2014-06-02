package ichun.common.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ichun.client.core.TickHandlerClient;
import ichun.client.keybind.KeyBind;
import ichun.common.core.util.EventCalendar;
import ichun.common.iChunUtil;
import net.minecraft.client.settings.KeyBinding;

public class CommonProxy
{
    public static TickHandlerClient tickHandlerClient;

    public void init()
    {
        EventCalendar.checkDate();
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
