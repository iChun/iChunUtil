package me.ichun.mods.ichunutil.common.core;

import me.ichun.mods.ichunutil.common.entity.EntityPersistentDataHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Consumer;

public abstract class EventHandlerServer
{
    public abstract EntityPersistentDataHandler getEntityPersistedDataHandler();

    /**
     * Convenience methods to listen to player tick - regularly used
     */
    protected final ArrayList<Consumer<Player>> playerTickEndListeners = new ArrayList<>();
    public final void registerPlayerTickEndListener(Consumer<Player> consumer)
    {
        if(playerTickEndListeners.isEmpty())
        {
            registerAsPlayerTickEndListener();
        }
        playerTickEndListeners.add(consumer);
    }

    public abstract void registerAsPlayerTickEndListener();

    protected void onPlayerTickEventEnd(Player player)
    {
        for(Consumer<Player> listener : playerTickEndListeners)
        {
            listener.accept(player);
        }
    }

    public void firePlayerTickEndEvent(Player player){}

    public boolean isFakePlayer(ServerPlayer player)
    {
        return player.connection == null || player.getClass().getSimpleName().toLowerCase(Locale.ROOT).contains("fakeplayer");
    }
}
