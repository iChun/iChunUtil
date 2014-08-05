package ichun.client.voxel;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.EntityLivingBase;
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

                    updatePos(streak);
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
                if(e.getValue().worldObj.provider.dimensionId != world.provider.dimensionId || (world.getWorldTime() - e.getValue().lastUpdate) > 10L)
                {
                    e.getValue().setDead();
                    ite.remove();
                }
            }
        }
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.side == Side.CLIENT && event.phase == TickEvent.Phase.END)
        {
            AbstractClientPlayer player = (AbstractClientPlayer)event.player;
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

            ArrayList<LocationInfo> loc = getPlayerLocationInfo(player);
            LocationInfo oldest = loc.get(0);
            loc.remove(0);
            loc.add(oldest);
            oldest.update(player);
        }
    }

    public ArrayList<LocationInfo> getPlayerLocationInfo(EntityPlayer player)
    {
        ArrayList<LocationInfo> loc = playerLoc.get(player.getCommandSenderName());//0 = oldest
        if(loc == null)
        {
            loc = new ArrayList<LocationInfo>();
            playerLoc.put(player.getCommandSenderName(), loc);
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
}
