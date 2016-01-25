package me.ichun.mods.ichunutil.common.core.event;

import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.core.tracker.EntityTrackerRegistry;
import me.ichun.mods.ichunutil.common.core.util.EventCalendar;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.patron.PatronInfo;
import me.ichun.mods.ichunutil.common.packet.mod.PacketPatrons;
import me.ichun.mods.ichunutil.common.packet.mod.PacketUserShouldShowUpdates;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;

public class EventHandlerServer
{
    public int ticks;

    public ArrayList<PatronInfo> patrons = new ArrayList<PatronInfo>();
    public EntityTrackerRegistry entityTrackerRegistry = new EntityTrackerRegistry();

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            entityTrackerRegistry.tick();

            ticks++;
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.side.isServer() && event.phase == TickEvent.Phase.END)
        {
            if(EventCalendar.isAFDay())
            {
                if(event.player.isPlayerSleeping() && event.player.getRNG().nextFloat() < 0.025F || event.player.getRNG().nextFloat() < 0.005F)
                {
                    event.player.getEntityWorld().playSoundAtEntity(event.player, "mob.pig.say", event.player.isPlayerSleeping() ? 0.2F : 1.0F, (event.player.getRNG().nextFloat() - event.player.getRNG().nextFloat()) * 0.2F + 1.0F);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        for(ConfigBase conf : ConfigHandler.configs)
        {
            if(!conf.sessionProp.isEmpty())
            {
                conf.sendPlayerSession(event.player);
            }
        }
        iChunUtil.channel.sendTo(new PacketPatrons(null), event.player);
        iChunUtil.channel.sendTo(new PacketUserShouldShowUpdates(iChunUtil.config.versionNotificationTypes == 0 || (iChunUtil.config.versionNotificationTypes == 1 && MinecraftServer.getServer().getConfigurationManager().canSendCommands(event.player.getGameProfile()))), event.player);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        patrons.remove(new PatronInfo(event.player.getGameProfile().getId().toString().replaceAll("-", ""), 1, false)); //Removes the player from the patron list if the player is one.
    }

    public void shuttingDownServer()
    {
        patrons.clear();
        entityTrackerRegistry.trackerEntries.clear();
    }
}
