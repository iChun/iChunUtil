package us.ichun.mods.ichunutil.common.core;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import us.ichun.mods.ichunutil.common.core.util.EventCalendar;

public class TickHandlerServer
{
    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.side.isServer() && event.phase.equals(TickEvent.Phase.END))
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
}
