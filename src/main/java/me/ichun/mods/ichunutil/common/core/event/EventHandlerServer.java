package me.ichun.mods.ichunutil.common.core.event;

import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.core.tracker.EntityTrackerRegistry;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.patron.PatronInfo;
import me.ichun.mods.ichunutil.common.packet.mod.PacketPatrons;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;

public class EventHandlerServer
{
    public ArrayList<PatronInfo> patrons = new ArrayList<PatronInfo>();
    public EntityTrackerRegistry entityTrackerRegistry = new EntityTrackerRegistry();

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            entityTrackerRegistry.tick();
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
