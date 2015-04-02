package us.ichun.mods.ichunutil.common.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import us.ichun.mods.ichunutil.common.core.packet.mod.PacketPatientData;
import us.ichun.mods.ichunutil.common.core.util.EventCalendar;
import us.ichun.mods.ichunutil.common.iChunUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class TickHandlerServer
{
    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent event)
    {
        if(event.phase.equals(TickEvent.Phase.END))
        {
            ticks++;
        }
    }

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
            if(infectionMap.containsKey(event.player.getGameProfile().getId().toString()))
            {
                int infectionLevel = infectionMap.get(event.player.getGameProfile().getId().toString());
                if(event.player.ticksExisted % 13 == 0)
                {
                    List list = event.player.getEntityWorld().getEntitiesWithinAABB(EntityPlayer.class, event.player.getEntityBoundingBox().expand(5D, 5D, 5D));
                    list.remove(event.player);
                    for(int i = 0; i < list.size(); i++)
                    {
                        EntityPlayer player = (EntityPlayer)list.get(i);
                        if(infectionMap.containsKey(player.getGameProfile().getId().toString()))
                        {
                            if(infectionMap.get(player.getGameProfile().getId().toString()) == infectionLevel - 1)
                            {
                                infectionMap.put(player.getGameProfile().getId().toString(), infectionLevel);
                                iChunUtil.channel.sendToPlayer(new PacketPatientData(infectionLevel, false, event.player.getGameProfile().getId().toString().replace("-", "")), player);
                                player.addPotionEffect(new PotionEffect(Potion.regeneration.id, infectionLevel * 20));
                                //cause infection increase
                            }
                        }
                        else
                        {
                            infectionMap.put(player.getGameProfile().getId().toString(), infectionLevel);
                            iChunUtil.channel.sendToPlayer(new PacketPatientData(0, false, event.player.getGameProfile().getId().toString().replace("-", "")), player);
                            player.addPotionEffect(new PotionEffect(Potion.poison.id, 100));
                            //cause infection
                        }
                    }
                }

                if((infectionLevel == getImmunityLevel(event.player.getGameProfile().getId().toString().replaceAll("-", "")) - 1/* || infectionLevel == 0 && event.player.getCommandSenderName().equalsIgnoreCase("Corosus")*/) && event.player.worldObj.rand.nextFloat() < 0.0005F)
                {
                    infectionMap.put(event.player.getGameProfile().getId().toString(), infectionLevel + 1);
                    iChunUtil.channel.sendToPlayer(new PacketPatientData(infectionLevel + 1, true, ""), event.player);
                    event.player.addPotionEffect(new PotionEffect(Potion.regeneration.id, infectionLevel * 20));
                    //mutation
                }
            }
        }
    }

    public int getImmunityLevel(String uuid)//no dashes please
    {
        for(String s : EntityHelperBase.volunteers)
        {
            if(s.replaceAll("-", "").equalsIgnoreCase(uuid))
            {
                return 0;
            }
        }

        rand.setSeed(Math.abs(uuid.hashCode()));

        float immunity = rand.nextFloat();

        int level = -1;
        int i = 0;

        while(immunity > 0F)
        {
            level++;
            immunity -= EntityHelperBase.RARITY[i];
            i++;
        }

        return level;
    }

    public int ticks;

    public Random rand = new Random();
    public HashMap<String, Integer> infectionMap = new HashMap<String, Integer>();
}
