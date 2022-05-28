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


    @FunctionalInterface
    public interface AddReloadListener
    {
        void onAddReloadListener(ArrayList<PreparableReloadListener> list);
    }
}
