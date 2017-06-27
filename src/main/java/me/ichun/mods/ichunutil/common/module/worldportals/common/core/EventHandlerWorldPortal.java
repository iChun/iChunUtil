package me.ichun.mods.ichunutil.common.module.worldportals.common.core;

import me.ichun.mods.ichunutil.common.module.worldportals.common.portal.WorldPortal;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.*;

public class EventHandlerWorldPortal
{
    public EnumMap<Side, HashMap<Entity, HashSet<WorldPortal>>> monitoredEntities = new EnumMap<Side, HashMap<Entity, HashSet<WorldPortal>>>(Side.class) // This is for portals that are against the wall.
    {{
        put(Side.SERVER, new HashMap<>());
        put(Side.CLIENT, new HashMap<>());
    }};

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event)
    {
        if(event.getSource() == DamageSource.IN_WALL) //check to see if entity is inside a portal.
        {
            if(isInPortal(event.getEntity()))
            {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event)
    {
        Iterator<HashMap.Entry<Entity, HashSet<WorldPortal>>> monitored = monitoredEntities.get(event.getWorld().isRemote ? Side.CLIENT : Side.SERVER).entrySet().iterator();
        while(monitored.hasNext())
        {
            Map.Entry<Entity, HashSet<WorldPortal>> e = monitored.next();
            if(e.getKey().getEntityWorld() == event.getWorld())
            {
                monitored.remove();
            }
        }
    }


    @SubscribeEvent
    public void onGetCollisionBoxesEvent(GetCollisionBoxesEvent event)
    {
        if(event.getEntity() == null) //assume particles? too much work to figure out, ignore.
        {
            return;
        }
        HashMap<Entity, HashSet<WorldPortal>> sideMap = monitoredEntities.get(event.getEntity().world.isRemote ? Side.CLIENT : Side.SERVER);
        if(sideMap.containsKey(event.getEntity()))
        {
            HashSet<WorldPortal> portals = sideMap.get(event.getEntity());
            HashSet<WorldPortal> invalid = new HashSet<>();
            for(WorldPortal portal : portals)
            {
                if(!portal.isValid())
                {
                    invalid.add(portal);
                    continue;
                }
                AxisAlignedBB check = portal.getCollisionRemovalAabbForEntity(event.getEntity()); //should I do it this way? WHAT ABOUT PARTICLES?
                if(check.intersectsWith(event.getAabb()))
                {
                    if(event.getAabb().equals(event.getEntity().getEntityBoundingBox())) //entity being pushed out of blocks
                    {
                        event.getCollisionBoxesList().clear();
                    }
                    else
                    {
                        //REMOVE ALL THOSE THAT INTERSECT
                        for(int i = event.getCollisionBoxesList().size() - 1; i >= 0; i--)
                        {
                            AxisAlignedBB aabb = event.getCollisionBoxesList().get(i);
                            boolean flag = false;
                            for(AxisAlignedBB portalBorder : portal.getCollisionBoundaries())
                            {
                                if(portalBorder.equals(aabb))
                                {
                                    flag = true;
                                    break;
                                }
                            }
                            if(!flag && aabb.intersectsWith(check))
                            {
                                event.getCollisionBoxesList().remove(i);
                            }
                        }
                    }
                }
            }
            portals.removeAll(invalid);
        }
    }

    public void addMonitoredEntity(Entity ent, WorldPortal portal)
    {
        HashMap<Entity, HashSet<WorldPortal>> sideMap = monitoredEntities.get(ent.world.isRemote ? Side.CLIENT : Side.SERVER);
        HashSet<WorldPortal> portals = sideMap.get(ent);
        if(portals == null)
        {
            portals = new HashSet<>();
            sideMap.put(ent, portals);
        }
        portals.add(portal);
    }

    public void removeMonitoredEntity(Entity ent, WorldPortal portal)
    {
        HashMap<Entity, HashSet<WorldPortal>> sideMap = monitoredEntities.get(ent.world.isRemote ? Side.CLIENT : Side.SERVER);
        HashSet<WorldPortal> portals = sideMap.get(ent);
        if(portals != null)
        {
            portals.remove(portal);
            if(portals.isEmpty())
            {
                sideMap.remove(ent);
            }
        }
    }

    public boolean isInPortal(Entity ent)
    {
        HashMap<Entity, HashSet<WorldPortal>> sideMap = monitoredEntities.get(ent.world.isRemote ? Side.CLIENT : Side.SERVER);
        if(sideMap.containsKey(ent))
        {
            HashSet<WorldPortal> portals = sideMap.get(ent);
            for(WorldPortal portal : portals)
            {
                if(!portal.isValid())
                {
                    continue;
                }
                AxisAlignedBB check = portal.getCollisionRemovalAabbForEntity(ent);
                if(check.intersectsWith(ent.getEntityBoundingBox()))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
