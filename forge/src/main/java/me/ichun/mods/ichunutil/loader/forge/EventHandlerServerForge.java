package me.ichun.mods.ichunutil.loader.forge;

import com.mojang.brigadier.CommandDispatcher;
import me.ichun.mods.ichunutil.common.core.EventHandlerServer;
import me.ichun.mods.ichunutil.common.entity.EntityPersistentDataHandler;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.loader.event.EventListener;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.EventPriority;

public class EventHandlerServerForge extends EventHandlerServer
{
    @Override
    public EntityPersistentDataHandler getEntityPersistedDataHandler()
    {
        if(iChunUtil.entityPersistentDataHandler == null)
        {
            iChunUtil.entityPersistentDataHandler = new EntityPersistentDataHandlerForge();
        }
        return iChunUtil.entityPersistentDataHandler;
    }

    @Override
    public void registerAsPlayerTickEndListener(EventListener<Player> eventListener)
    {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TickEvent.PlayerTickEvent.Post.class, event -> eventListener.trigger(event.player));
    }

    @Override
    protected void registerAsPlayerLoggedInListener(EventListener<ServerPlayer> eventListener)
    {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.PlayerLoggedInEvent.class, event -> eventListener.trigger((ServerPlayer)event.getEntity()));
    }

    @Override
    protected void registerAsPlayerLoggedOutListener(EventListener<ServerPlayer> eventListener)
    {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.PlayerLoggedOutEvent.class, event -> eventListener.trigger((ServerPlayer)event.getEntity()));
    }

    @Override
    protected void registerAsServerAboutToStartListener(EventListener<MinecraftServer> eventListener)
    {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ServerStartingEvent.class, event -> eventListener.trigger(event.getServer()));
    }

    @Override
    protected void registerAsServerStoppingListener(EventListener<MinecraftServer> eventListener)
    {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ServerStoppingEvent.class, event -> eventListener.trigger(event.getServer()));
    }

    @Override
    protected void registerAsCommandRegistrationListener(EventListener<CommandDispatcher<CommandSourceStack>> eventListener)
    {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, RegisterCommandsEvent.class, event -> eventListener.trigger(event.getDispatcher()));
    }
}
