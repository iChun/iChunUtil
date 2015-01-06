package us.ichun.mods.ichunutil.client.voxel;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import us.ichun.mods.ichunutil.common.core.network.PacketHandler;
import us.ichun.mods.ichunutil.common.core.packet.PacketShowPatronReward;
import us.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;

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
                PacketHandler.sendToServer(iChunUtil.channels, new PacketShowPatronReward(iChunUtil.config.getInt("showPatronReward") == 1));
            }
        }
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.side == Side.CLIENT && event.phase == TickEvent.Phase.END)
        {
            AbstractClientPlayer player = (AbstractClientPlayer)event.player;
            if(player.worldObj.getPlayerEntityByName(player.getName()) != player)
            {
                return;
            }

            WorldClient world = Minecraft.getMinecraft().theWorld;

            EntityTrail hat = streaks.get(player.getName());
            if(hat == null || hat.isDead)
            {
                if(player.getName().equalsIgnoreCase(Minecraft.getMinecraft().thePlayer.getName()))
                {
                    //Assume respawn
                    for(Entry<String, EntityTrail> e : streaks.entrySet())
                    {
                        e.getValue().setDead();
                    }
                }

                hat = new EntityTrail(world, player);
                streaks.put(player.getName(), hat);
                world.spawnEntityInWorld(hat);
            }

            ArrayList<LocationInfo> loc = getPlayerLocationInfo(player);
            LocationInfo oldest = loc.get(0);
            loc.remove(0);
            loc.add(oldest);
            oldest.update(player);
        }
    }

    public ArrayList<LocationInfo> getPlayerLocationInfo(EntityPlayer player)
    {
        ArrayList<LocationInfo> loc = playerLoc.get(player.getName());//0 = oldest
        if(loc == null)
        {
            loc = new ArrayList<LocationInfo>();
            playerLoc.put(player.getName(), loc);
        }
        int time = 100;
        if(loc.size() < time)
        {
            for(int i = 0; i < (time - loc.size()); i++)
            {
                loc.add(0, new LocationInfo(player));
            }
        }
        else if(loc.size() > time)
        {
            loc.remove(0);
        }
        return loc;
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

    public HashMap<String, ArrayList<LocationInfo>> playerLoc = new HashMap<String, ArrayList<LocationInfo>>();

    public HashMap<String, EntityTrail> streaks = new HashMap<String, EntityTrail>();

    public ArrayList<String> patronList = new ArrayList<String>();

    public boolean tellServerAsPatron;
}
