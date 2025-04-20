package me.ichun.mods.ichunutil.loader.neoforge;

import com.mojang.brigadier.CommandDispatcher;
import me.ichun.mods.ichunutil.common.core.EventHandlerServer;
import me.ichun.mods.ichunutil.common.entity.EntityPersistentDataHandler;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.loader.event.listener.EventListener;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;


public class EventHandlerServerNeoForge extends EventHandlerServer
{
    @Override
    public EntityPersistentDataHandler getEntityPersistedDataHandler()
    {
        if(iChunUtil.entityPersistentDataHandler == null)
        {
            iChunUtil.entityPersistentDataHandler = new EntityPersistentDataHandlerNeoForge();
        }
        return iChunUtil.entityPersistentDataHandler;
    }

    @Override
    public void registerAsPlayerTickEndListener(EventListener<Player> eventListener)
    {
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerTickEvent.Post.class, event -> eventListener.trigger(event.getEntity()));
    }


    @Override
    protected void registerAsPlayerLoggedInListener(EventListener<ServerPlayer> eventListener)
    {
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.PlayerLoggedInEvent.class, event -> eventListener.trigger((ServerPlayer)event.getEntity()));
    }

    @Override
    protected void registerAsPlayerLoggedOutListener(EventListener<ServerPlayer> eventListener)
    {
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.PlayerLoggedOutEvent.class, event -> eventListener.trigger((ServerPlayer)event.getEntity()));
    }

    @Override
    protected void registerAsServerAboutToStartListener(EventListener<MinecraftServer> eventListener)
    {
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ServerStartingEvent.class, event -> eventListener.trigger(event.getServer()));
    }

    @Override
    protected void registerAsServerStoppingListener(EventListener<MinecraftServer> eventListener)
    {
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ServerStoppingEvent.class, event -> eventListener.trigger(event.getServer()));
    }

    @Override
    protected void registerAsCommandRegistrationListener(EventListener<CommandDispatcher<CommandSourceStack>> eventListener)
    {
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, RegisterCommandsEvent.class, event -> eventListener.trigger(event.getDispatcher()));
    }

    @Override
    public boolean isFakePlayer(ServerPlayer player)
    {
        return player.isFakePlayer() || player instanceof FakePlayer || super.isFakePlayer(player);
    }
}
