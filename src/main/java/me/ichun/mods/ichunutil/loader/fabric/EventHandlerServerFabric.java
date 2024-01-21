package me.ichun.mods.ichunutil.loader.fabric;

import me.ichun.mods.ichunutil.common.core.EventHandlerServer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class EventHandlerServerFabric extends EventHandlerServer
{
    public EventHandlerServerFabric()
    {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> LoaderFabric.pushLoadComplete());

        ServerTickEvents.END_SERVER_TICK.register(server -> onServerTickEnd());
    }
}
