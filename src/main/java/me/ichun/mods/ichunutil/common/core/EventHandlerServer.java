package me.ichun.mods.ichunutil.common.core;

public abstract class EventHandlerServer
{
    public int ticks;

    public void onServerTickEnd()
    {
        ticks++;
    }
}
