package me.ichun.mods.ichunutil.client.keybind;

import net.minecraftforge.fml.common.eventhandler.Event;

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
