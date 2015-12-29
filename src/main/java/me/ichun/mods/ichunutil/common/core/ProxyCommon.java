package me.ichun.mods.ichunutil.common.core;

import me.ichun.mods.ichunutil.common.core.event.EventHandlerServer;
import net.minecraftforge.common.MinecraftForge;

public class ProxyCommon
{
    public void preInit()
    {
        MinecraftForge.EVENT_BUS.register(new EventHandlerServer());
    }

    public void init()
    {

    }

    public void postInit()
    {

    }
}
