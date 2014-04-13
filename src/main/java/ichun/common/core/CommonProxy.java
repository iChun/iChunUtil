package ichun.common.core;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ichun.client.core.TickHandlerClient;
import ichun.client.keybind.KeyBind;
import ichun.common.core.util.EventCalendar;

public class CommonProxy
{
    public static TickHandlerClient tickHandlerClient;

    public void init()
    {
        EventCalendar.checkDate();
    }

    @SideOnly(Side.CLIENT)
    public void registerKeyBind(KeyBind bind) {}
}
