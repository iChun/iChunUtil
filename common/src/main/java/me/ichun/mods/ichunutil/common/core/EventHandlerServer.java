package me.ichun.mods.ichunutil.common.core;

import com.mojang.brigadier.CommandDispatcher;
import me.ichun.mods.ichunutil.common.entity.EntityPersistentDataHandler;
import me.ichun.mods.ichunutil.loader.event.listener.EventListener;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Locale;
import java.util.function.Consumer;

public abstract class EventHandlerServer
{
    public abstract EntityPersistentDataHandler getEntityPersistedDataHandler();

    /**
     * Convenience methods to listen to specific regularly used events
     */
    private EventListener<Player> playerTickEndListener;
    protected abstract void registerAsPlayerTickEndListener(EventListener<Player> eventListener);
    public final void registerPlayerTickEndListener(Consumer<Player> consumer)
    {
        if(playerTickEndListener == null)
        {
            playerTickEndListener = new EventListener<>(this::registerAsPlayerTickEndListener);
        }
        playerTickEndListener.register(consumer);
    }

    private EventListener<ServerPlayer> playerLoggedInListener;
    protected abstract void registerAsPlayerLoggedInListener(EventListener<ServerPlayer> eventListener);
    public final void registerPlayerLoggedInListener(Consumer<ServerPlayer> consumer)
    {
        if(playerLoggedInListener == null)
        {
            playerLoggedInListener = new EventListener<>(this::registerAsPlayerLoggedInListener);
        }
        playerLoggedInListener.register(consumer);
    }

    private EventListener<ServerPlayer> playerLoggedOutListener;
    protected abstract void registerAsPlayerLoggedOutListener(EventListener<ServerPlayer> eventListener);
    public final void registerPlayerLoggedOutListener(Consumer<ServerPlayer> consumer)
    {
        if(playerLoggedOutListener == null)
        {
            playerLoggedOutListener = new EventListener<>(this::registerAsPlayerLoggedOutListener);
        }
        playerLoggedOutListener.register(consumer);
    }

    private EventListener<MinecraftServer> serverAboutToStartListener;
    protected abstract void registerAsServerAboutToStartListener(EventListener<MinecraftServer> eventListener);
    public final void registerServerAboutToStartListener(Consumer<MinecraftServer> consumer)
    {
        if(serverAboutToStartListener == null)
        {
            serverAboutToStartListener = new EventListener<>(this::registerAsServerAboutToStartListener);
        }
        serverAboutToStartListener.register(consumer);
    }

    private EventListener<MinecraftServer> serverStoppingListener;
    protected abstract void registerAsServerStoppingListener(EventListener<MinecraftServer> eventListener);
    public final void registerServerStoppingListener(Consumer<MinecraftServer> consumer)
    {
        if(serverStoppingListener == null)
        {
            serverStoppingListener = new EventListener<>(this::registerAsServerStoppingListener);
        }
        serverStoppingListener.register(consumer);
    }

    private EventListener<CommandDispatcher<CommandSourceStack>> commandRegistrationListener;
    protected abstract void registerAsCommandRegistrationListener(EventListener<CommandDispatcher<CommandSourceStack>> eventListener);
    public final void registerCommandRegistrationListener(Consumer<CommandDispatcher<CommandSourceStack>> consumer)
    {
        if(commandRegistrationListener == null)
        {
            commandRegistrationListener = new EventListener<>(this::registerAsCommandRegistrationListener);
        }
        commandRegistrationListener.register(consumer);
    }

    public void firePlayerTickEndEvent(Player player){}

    public boolean isFakePlayer(ServerPlayer player)
    {
        return player.connection == null || player.getClass().getSimpleName().toLowerCase(Locale.ROOT).contains("fakeplayer");
    }
}
