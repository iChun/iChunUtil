package ichun.common.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ichun.client.core.TickHandlerClient;
import ichun.client.keybind.KeyBind;
import ichun.common.core.util.EventCalendar;
import net.minecraft.client.settings.KeyBinding;

public class CommonProxy
{
    public static TickHandlerClient tickHandlerClient;

    public void init()
    {
        EventCalendar.checkDate();
    }

    @SideOnly(Side.CLIENT)
    public KeyBind registerKeyBind(KeyBind bind, KeyBind replacing) { return bind; }

    /**
     * Please note that this keybind will trigger without checking for SHIFT/CTRL/ALT being held down. That checking has to be done on your end.
     * @param keyBind
     */
    @SideOnly(Side.CLIENT)
    public void registerMinecraftKeyBind(KeyBinding bind) {}
}
