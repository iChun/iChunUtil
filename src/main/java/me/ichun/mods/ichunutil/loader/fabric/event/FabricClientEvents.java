package me.ichun.mods.ichunutil.loader.fabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.level.Level;

public class FabricClientEvents
{
    private FabricClientEvents(){}

    public static final Event<ClientModInit> CLIENT_MOD_INIT = EventFactory.createArrayBacked(ClientModInit.class, callbacks -> eventObj -> {
        for(ClientModInit callback : callbacks)
        {
            callback.onClientModInit(eventObj);
        }
    });

    public static final Event<ClientLevelUnload> CLIENT_LEVEL_UNLOAD = EventFactory.createArrayBacked(ClientLevelUnload.class, callbacks -> level -> {
        for(ClientLevelUnload callback : callbacks)
        {
            callback.onClientLevelUnload(level);
        }
    });

    @FunctionalInterface
    public interface ClientModInit
    {
        void onClientModInit(Object eventObj);
    }

    @FunctionalInterface
    public interface ClientLevelUnload
    {
        void onClientLevelUnload(Level level);
    }
}
