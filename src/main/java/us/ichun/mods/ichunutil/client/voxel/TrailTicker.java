package us.ichun.mods.ichunutil.client.voxel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import us.ichun.mods.ichunutil.common.core.packet.mod.PacketShowPatronReward;
import us.ichun.mods.ichunutil.common.iChunUtil;
import us.ichun.mods.ichunutil.common.tracker.EntityInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class TrailTicker
{
    @SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event)
    {
        if(event.phase == TickEvent.Phase.START)
        {
            this.renderTick = event.renderTickTime;

            Iterator<Entry<String, EntityTrail>> iterator = streaks.entrySet().iterator();

            while(iterator.hasNext())
            {
                Entry<String, EntityTrail> e = iterator.next();
                if(e.getValue().parent != null)
                {
                    EntityTrail streak = e.getValue();
                    if(e.getValue().parent.isDead)
                    {
                        streak.setDead();
                        iterator.remove();
                    }
                    else
                    {
                        updatePos(streak);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void worldTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().theWorld != null)
        {
            WorldClient world = Minecraft.getMinecraft().theWorld;
            if(worldInstance != world)
            {
                worldInstance = world;
                streaks.clear();
            }

            Iterator<Entry<String, EntityTrail>> ite = streaks.entrySet().iterator();

            while(ite.hasNext())
            {
                Entry<String, EntityTrail> e = ite.next();
                if(e.getValue().worldObj.provider.getDimensionId() != world.provider.getDimensionId() || (world.getWorldTime() - e.getValue().lastUpdate) > 10L)
                {
                    e.getValue().setDead();
                    ite.remove();
                }
            }

            if(tellServerAsPatron)
            {
                tellServerAsPatron = false;
                iChunUtil.channel.sendToServer(new PacketShowPatronReward(iChunUtil.config.showPatronReward == 1));
            }
        }
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.side == Side.CLIENT && event.phase == TickEvent.Phase.END)
        {
            AbstractClientPlayer player = (AbstractClientPlayer)event.player;
            if(player.worldObj.getPlayerEntityByName(player.getCommandSenderName()) != player)
            {
                return;
            }

            WorldClient world = Minecraft.getMinecraft().theWorld;

            EntityTrail hat = streaks.get(player.getCommandSenderName());
            if(hat == null || hat.isDead)
            {
                if(player.getCommandSenderName().equalsIgnoreCase(Minecraft.getMinecraft().thePlayer.getCommandSenderName()))
                {
                    //Assume respawn
                    for(Entry<String, EntityTrail> e : streaks.entrySet())
                    {
                        e.getValue().setDead();
                    }
                }

                hat = new EntityTrail(world, player);
                streaks.put(player.getCommandSenderName(), hat);
                world.spawnEntityInWorld(hat);
            }
        }
    }

    public void updatePos(EntityTrail streak)
    {
        streak.lastTickPosX = streak.parent.lastTickPosX;
        streak.lastTickPosY = streak.parent.lastTickPosY;
        streak.lastTickPosZ = streak.parent.lastTickPosZ;

        streak.prevPosX = streak.parent.prevPosX;
        streak.prevPosY = streak.parent.prevPosY;
        streak.prevPosZ = streak.parent.prevPosZ;

        streak.posX = streak.parent.posX;
        streak.posY = streak.parent.posY;
        streak.posZ = streak.parent.posZ;
    }

    public float renderTick;

    public WorldClient worldInstance;


    public HashMap<String, EntityTrail> streaks = new HashMap<String, EntityTrail>();

    public ArrayList<String> patronList = new ArrayList<String>();

    public boolean tellServerAsPatron;
}
