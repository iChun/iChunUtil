package me.ichun.mods.ichunutil.common.core;

import me.ichun.mods.ichunutil.common.entity.EntityPersistentDataHandler;
import me.ichun.mods.ichunutil.loader.event.EventListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Locale;
import java.util.function.Consumer;

public abstract class EventHandlerServer
{
    public abstract EntityPersistentDataHandler getEntityPersistedDataHandler();

    /**
     * Convenience methods to listen to player tick - regularly used
     */
    private EventListener<Player> playerTickEndListener;
    public final void registerPlayerTickEndListener(Consumer<Player> consumer)
    {
        if(playerTickEndListener == null)
        {
            playerTickEndListener = new EventListener<>(this::registerAsPlayerTickEndListener);
        }
        playerTickEndListener.register(consumer);
    }

    public abstract void registerAsPlayerTickEndListener(EventListener<Player> eventListener);

    public void firePlayerTickEndEvent(Player player){}

    public boolean isFakePlayer(ServerPlayer player)
    {
        return player.connection == null || player.getClass().getSimpleName().toLowerCase(Locale.ROOT).contains("fakeplayer");
    }
}
