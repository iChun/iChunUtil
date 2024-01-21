package me.ichun.mods.ichunutil.loader.fabric.client;

import me.ichun.mods.ichunutil.client.core.EventHandlerClient;
import me.ichun.mods.ichunutil.loader.LoaderHandler;
import me.ichun.mods.ichunutil.loader.fabric.LoaderFabric;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

public class EventHandlerClientFabric extends EventHandlerClient
{
    public EventHandlerClientFabric()
    {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> LoaderFabric.pushLoadComplete());

        LoaderHandler.d().registerClientTickEndListener(this::onClientTickEnd);
        WorldRenderEvents.START.register(context -> onRenderTickStart(context.tickDelta()));
        LoaderHandler.d().registerPostInitScreenListener(this::onPostInitScreen);
    }
}
