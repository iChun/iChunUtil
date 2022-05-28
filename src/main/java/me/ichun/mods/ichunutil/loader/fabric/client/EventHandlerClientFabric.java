package me.ichun.mods.ichunutil.loader.fabric.client;

import me.ichun.mods.ichunutil.client.core.EventHandlerClient;
import me.ichun.mods.ichunutil.loader.LoaderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

public class EventHandlerClientFabric extends EventHandlerClient
{
    public EventHandlerClientFabric()
    {
        LoaderHandler.d().registerClientTickEndListener(this::onClientTickEnd);
        WorldRenderEvents.START.register(context -> onRenderTickStart(context.tickDelta()));
        LoaderHandler.d().registerPostInitScreenListener(this::onPostInitScreen);
    }
}
