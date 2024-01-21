package me.ichun.mods.ichunutil.loader.fabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.ArrayList;

public class FabricEvents
{
    public FabricEvents(){}

    public static final Event<AddReloadListener> ADD_RELOAD_LISTENER = EventFactory.createArrayBacked(AddReloadListener.class, callbacks -> list -> {
        for(AddReloadListener callback : callbacks)
        {
            callback.onAddReloadListener(list);
        }
    });

    public static final Event<LoadComplete> LOAD_COMPLETE = EventFactory.createArrayBacked(LoadComplete.class, callbacks -> () -> {
        for(LoadComplete callback : callbacks)
        {
            callback.onLoadComplete();
        }
    });

    @FunctionalInterface
    public interface AddReloadListener
    {
        void onAddReloadListener(ArrayList<PreparableReloadListener> list);
    }

    @FunctionalInterface
    public interface LoadComplete
    {
        void onLoadComplete();
    }
}
