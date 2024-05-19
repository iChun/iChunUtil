package me.ichun.mods.ichunutil.api.fabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;

public final class FabricEvents
{
    private FabricEvents(){}//no init!

    public static final Event<AddReloadListener> ADD_RELOAD_LISTENER = EventFactory.createArrayBacked(AddReloadListener.class, callbacks -> list -> {
        for(AddReloadListener callback : callbacks)
        {
            callback.onAddReloadListener(list);
        }
    });

    public static final Event<PlayerTickEnd> PLAYER_TICK_END = EventFactory.createArrayBacked(PlayerTickEnd.class, callbacks -> player -> {
        for(PlayerTickEnd callback : callbacks)
        {
            callback.onPlayerTickEnd(player);
        }
    });

    @FunctionalInterface
    public interface AddReloadListener
    {
        void onAddReloadListener(ArrayList<PreparableReloadListener> list);
    }

    @FunctionalInterface
    public interface PlayerTickEnd
    {
        void onPlayerTickEnd(Player player);
    }
}
