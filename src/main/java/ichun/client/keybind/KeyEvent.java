package ichun.client.keybind;

import cpw.mods.fml.common.eventhandler.Event;

public class KeyEvent extends Event
{
    public final KeyBind keyBind;
    public final boolean isPulse;

    public KeyEvent(KeyBind keyBind, boolean pulse)
    {
        this.keyBind = keyBind;
        this.isPulse = pulse;
    }
}
