package me.ichun.mods.ichunutil.loader.fabric;

import com.mojang.brigadier.CommandDispatcher;
import me.ichun.mods.ichunutil.common.core.EventHandlerServer;
import me.ichun.mods.ichunutil.common.entity.EntityPersistentDataHandler;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.loader.event.listener.EventListener;
import me.ichun.mods.ichunutil.loader.fabric.event.FabricEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class EventHandlerServerFabric extends EventHandlerServer
{
    @Override
    public EntityPersistentDataHandler getEntityPersistedDataHandler()
    {
        if(iChunUtil.entityPersistentDataHandler == null)
        {
            iChunUtil.entityPersistentDataHandler = new EntityPersistentDataHandlerFabric();
        }
        return iChunUtil.entityPersistentDataHandler;
    }

    @Override
    public void registerAsPlayerTickEndListener(EventListener<Player> eventListener)
    {
        FabricEvents.PLAYER_TICK_END.register(eventListener::trigger);
    }

    @Override
    protected void registerAsPlayerLoggedInListener(EventListener<ServerPlayer> eventListener)
    {
        ServerPlayConnectionEvents.INIT.register((handler, server) -> eventListener.trigger(handler.getPlayer()));
    }

    @Override
    protected void registerAsPlayerLoggedOutListener(EventListener<ServerPlayer> eventListener)
    {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> eventListener.trigger(handler.getPlayer()));
    }

    @Override
    protected void registerAsServerAboutToStartListener(EventListener<MinecraftServer> eventListener)
    {
        ServerLifecycleEvents.SERVER_STARTING.register(eventListener::trigger);
    }

    @Override
    protected void registerAsServerStoppingListener(EventListener<MinecraftServer> eventListener)
    {
        ServerLifecycleEvents.SERVER_STOPPING.register(eventListener::trigger);
    }

    @Override
    protected void registerAsCommandRegistrationListener(EventListener<CommandDispatcher<CommandSourceStack>> eventListener)
    {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> eventListener.trigger(dispatcher));
    }

    @Override
    public void firePlayerTickEndEvent(Player player)
    {
        FabricEvents.PLAYER_TICK_END.invoker().onPlayerTickEnd(player);
    }

    @Override
    public boolean isFakePlayer(ServerPlayer player)
    {
        return player instanceof FakePlayer || super.isFakePlayer(player);
    }
}
