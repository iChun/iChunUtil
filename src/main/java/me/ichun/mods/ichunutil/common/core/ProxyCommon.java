package me.ichun.mods.ichunutil.common.core;

import me.ichun.mods.ichunutil.common.core.event.EventHandlerServer;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraftforge.common.MinecraftForge;

public class ProxyCommon
{
    public void preInit()
    {
        iChunUtil.eventHandlerServer = new EventHandlerServer();
        MinecraftForge.EVENT_BUS.register(iChunUtil.eventHandlerServer);
    }

    public void init()
    {

    }

    public void postInit()
    {

    }

    public void nudgeHand(float mag)
    {
    }
}
