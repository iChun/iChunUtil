package me.ichun.mods.ichunutil.client.keybind;

import net.minecraftforge.fml.common.eventhandler.Event;

public class KeyEvent extends Event
{
    public final KeyBind keyBind;

    public KeyEvent(KeyBind keyBind)
    {
        this.keyBind = keyBind;
    }
}
