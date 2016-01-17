package me.ichun.mods.ichunutil.common.core.event;

import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.module.patron.PatronInfo;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.ArrayList;

public class EventHandlerServer
{
    public ArrayList<PatronInfo> patrons = new ArrayList<PatronInfo>();

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
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        patrons.remove(new PatronInfo(event.player.getGameProfile().getId().toString(), 1, false)); //Removes the player from the patron list if the player is one.
    }

    public void shuttingDownServer()
    {
        patrons.clear();
    }
}
