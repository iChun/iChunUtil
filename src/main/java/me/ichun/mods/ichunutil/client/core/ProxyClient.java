package me.ichun.mods.ichunutil.client.core;

import me.ichun.mods.ichunutil.client.core.event.EventHandlerClient;
import me.ichun.mods.ichunutil.common.core.ProxyCommon;
import net.minecraftforge.common.MinecraftForge;

public class ProxyClient extends ProxyCommon
{
    @Override
    public void preInit()
    {
        super.preInit();

        MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
    }
}
