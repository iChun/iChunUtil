package me.ichun.mods.ichunutil.client.tracker;

import me.ichun.mods.ichunutil.client.tracker.entity.EntityTracker;
import me.ichun.mods.ichunutil.client.tracker.render.RenderTracker;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.loader.LoaderHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class ClientEntityTracker
{
    private static boolean hasInit;
    private static final AtomicInteger NEXT_ENTITY_ID = new AtomicInteger(-70000000);// -70 million. We reduce even further as we use this more, negative ent IDs prevent collision with real entities (with positive IDs starting with 0)
    private static final IdentityHashMap<Entity, EntityTracker> TRACKERS = new IdentityHashMap<>();

    /**
     * Call this during mod constructor events
     */
    public static synchronized void init() //event bus from constructor
    {
        if(!hasInit)
        {
            hasInit = true;
            LoaderHandler.d().registerEntityTypeRegistryListener(ClientEntityTracker.EntityTypes::onEntityTypeRegistry);
            LoaderHandler.d().registerClientSetupListener(ClientEntityTracker::onClientSetup);
            LoaderHandler.d().registerClientTickEndListener(ClientEntityTracker::onClientTickEnd);
            LoaderHandler.d().registerClientLevelUnloadListener(ClientEntityTracker::onWorldUnload);
        }
    }

    private static void onClientSetup(Object event)
    {
        //Is this log still required?
        iChunUtil.LOGGER.info("That was an intended override. Nothing to worry about. No broken mod here. Not a Dangerous alternative prefix at all. Nope. Nossirree.");
        LoaderHandler.d().registerEntityRenderer(EntityTypes.TRACKER, new RenderTracker.RenderFactory());
    }

    public static int getNextEntId()
    {
        return NEXT_ENTITY_ID.getAndDecrement();
    }

    public static EntityTracker getOrCreate(Entity ent)
    {
        EntityTracker tracker = null;
        for(Map.Entry<Entity, EntityTracker> e : TRACKERS.entrySet())
        {
            if(e.getKey() == ent)
            {
                tracker = e.getValue();
                break;
            }
        }
        if(tracker == null)
        {
            tracker = new EntityTracker(EntityTypes.TRACKER, ent.level);
            TRACKERS.put(ent, tracker);
        }

        if(!tracker.isAlive()) // our tracker is dead. create a new one
        {
            TRACKERS.put(ent, tracker = new EntityTracker(EntityTypes.TRACKER, ent.level));
        }
        tracker.setParent(ent);
        if(!LoaderHandler.d().isEntityAddedToWorld(tracker))
        {
            tracker.setId(getNextEntId());
            tracker.moveTo(ent.getX(), ent.getY(), ent.getZ(), ent.getYRot(), ent.getXRot());
            ((ClientLevel)ent.level).putNonPlayerEntity(tracker.getId(), tracker);
        }
        return tracker;
    }

    private static void onClientTickEnd(Minecraft mc)
    {
        if(mc.player != null && !mc.isPaused()) //ingame, not paused.
        {
            EntityTracker playerTracker = null;
            Iterator<Map.Entry<Entity, EntityTracker>> ite = TRACKERS.entrySet().iterator();
            while(ite.hasNext())
            {
                Map.Entry<Entity, EntityTracker> e = ite.next();
                EntityTracker tracker = e.getValue();
                if(!tracker.isAlive() || mc.player.tickCount - tracker.lastUpdate > 10)
                {
                    if(e.getKey() == Minecraft.getInstance().player && tracker.lastUpdate == -1)
                    {
                        //we didn't even get to tick, the poor guy just got spawncamped
                        playerTracker = tracker;
                    }
                    ite.remove();
                }
            }
            if(playerTracker != null)
            {
                EntityTracker tracker = getOrCreate(playerTracker.parent);
                tracker.tags = playerTracker.tags; //AVENGE ME BROTHER
                tracker.updateBounds();
            }
        }
    }

    private static void onWorldUnload(Level level)
    {
        if(level.isClientSide())
        {
            Iterator<Map.Entry<Entity, EntityTracker>> ite = TRACKERS.entrySet().iterator();
            while(ite.hasNext())
            {
                Map.Entry<Entity, EntityTracker> e = ite.next();
                EntityTracker tracker = e.getValue();
                if(tracker.parent.getCommandSenderWorld() == level)
                {
                    ite.remove();
                }
            }
        }
    }

    private static class EntityTypes
    {
        public static EntityType<EntityTracker> TRACKER;
        public static void onEntityTypeRegistry(Object entityTypeRegistryEvent) //we're doing it this way because it's a client-side entity and we don't want to sync registry values
        {
            TRACKER = EntityType.Builder.of(EntityTracker::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .noSave()
                    .noSummon()
                    .fireImmune()
                    .build("an entity from " + iChunUtil.MOD_NAME + ". Ignore this.");
        }
    }
}
