package me.ichun.mods.ichunutil.loader.fabric;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public final class ServerListenerFabric
{
    private static MinecraftServer serverInstance;

    private static boolean hasInit;

    public static void init()
    {
        if(!hasInit)
        {
            hasInit = true;

            ServerLifecycleEvents.SERVER_STARTING.register(server -> serverInstance = server);
            ServerLifecycleEvents.SERVER_STOPPING.register(server -> serverInstance = null);
        }
    }

    public static MinecraftServer getServerInstance()
    {
        return serverInstance;
    }
}
