package me.ichun.mods.ichunutil.client.tracker;

import me.ichun.mods.ichunutil.client.tracker.entity.EntityTracker;
import me.ichun.mods.ichunutil.client.tracker.render.RenderTracker;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@OnlyIn(Dist.CLIENT)
public final class ClientEntityTracker
{
    private static boolean hasInit;
    private static final AtomicInteger NEXT_ENTITY_ID = new AtomicInteger(-70000000);// -70 million. We reduce even further as we use this more, negative ent IDs prevent collision with real entities (with positive IDs starting with 0)
    private static final HashMap<EntityHolder, EntityTracker> TRACKERS = new HashMap<>();

    public static void init(IEventBus bus) //event bus from constructor
    {
        if(!hasInit)
        {
            hasInit = true;
            EntityTypes.REGISTRY.register(bus);
            bus.addListener(ClientEntityTracker::onClientSetup);
            MinecraftForge.EVENT_BUS.addListener(ClientEntityTracker::onWorldTick);
            MinecraftForge.EVENT_BUS.addListener(ClientEntityTracker::onWorldUnload);
        }
    }

    private static void onClientSetup(FMLClientSetupEvent event)
    {
        iChunUtil.LOGGER.info("That was an intended override. Nothing to worry about. No broken mod here. Not a Dangerous alternative prefix at all. Nope. Nossirree.");
        RenderingRegistry.registerEntityRenderingHandler(EntityTypes.TRACKER.get(), new RenderTracker.RenderFactory());
    }

    private static int getNextEntId()
    {
        return NEXT_ENTITY_ID.getAndDecrement();
    }

    public static EntityTracker getOrCreate(Entity ent)
    {
        EntityTracker tracker = null;
        for(Map.Entry<EntityHolder, EntityTracker> e : TRACKERS.entrySet())
        {
            if(e.getKey().entity == ent)
            {
                tracker = e.getValue();
                break;
            }
        }
        if(tracker == null)
        {
            tracker = new EntityTracker(EntityTypes.TRACKER.get(), ent.world);
            TRACKERS.put(new EntityHolder(ent), tracker);
        }

        if(!tracker.isAlive()) // our tracker is dead. create a new one
        {
            TRACKERS.put(new EntityHolder(ent), tracker = new EntityTracker(EntityTypes.TRACKER.get(), ent.world));
        }
        tracker.setParent(ent);
        if(!tracker.isAddedToWorld())
        {
            tracker.setEntityId(getNextEntId());
            tracker.setLocationAndAngles(ent.getPosX(), ent.getPosY(), ent.getPosZ(), ent.rotationYaw, ent.rotationPitch);
            ((ClientWorld)ent.world).addEntity(tracker.getEntityId(), tracker);
        }
        return tracker;
    }

    private static void onWorldTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END) //should be only on the client.
        {
            if(Minecraft.getInstance().player != null && !Minecraft.getInstance().isGamePaused()) //ingame, not paused.
            {
                EntityTracker playerTracker = null;
                Iterator<Map.Entry<EntityHolder, EntityTracker>> ite = TRACKERS.entrySet().iterator();
                while(ite.hasNext())
                {
                    Map.Entry<EntityHolder, EntityTracker> e = ite.next();
                    EntityTracker tracker = e.getValue();
                    if(!tracker.isAlive() || Minecraft.getInstance().player.ticksExisted - tracker.lastUpdate > 10)
                    {
                        if(e.getKey().entity == Minecraft.getInstance().player && tracker.lastUpdate == -1)
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
    }

    private static void onWorldUnload(WorldEvent.Unload event)
    {
        if(event.getWorld().isRemote())
        {
            Iterator<Map.Entry<EntityHolder, EntityTracker>> ite = TRACKERS.entrySet().iterator();
            while(ite.hasNext())
            {
                Map.Entry<EntityHolder, EntityTracker> e = ite.next();
                EntityTracker tracker = e.getValue();
                if(tracker.parent.getEntityWorld() == event.getWorld())
                {
                    ite.remove();
                }
            }
        }
    }

    private static class EntityHolder //to fix the issue of entities changing IDs and thus changing their HashCode
    {
        public final Entity entity;

        private EntityHolder(Entity entity) {this.entity = entity;}
    }

    private static class EntityTypes
    {
        private static final DeferredRegister<EntityType<?>> REGISTRY = new DeferredRegister<>(ForgeRegistries.ENTITIES, iChunUtil.MOD_ID);

        public static final RegistryObject<EntityType<EntityTracker>> TRACKER = REGISTRY.register("tracker", () -> EntityType.Builder.create(EntityTracker::new, EntityClassification.MISC)
                .size(0.1F, 0.1F)
                .disableSerialization()
                .disableSummoning()
                .immuneToFire()
                .build("from " + iChunUtil.MOD_NAME + ". Ignore this.")
        );
    }
}
