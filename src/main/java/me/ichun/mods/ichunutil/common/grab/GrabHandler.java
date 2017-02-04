package me.ichun.mods.ichunutil.common.grab;

import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.grab.handlers.GrabbedEntityBlockHandler;
import me.ichun.mods.ichunutil.common.grab.handlers.GrabbedFallingBlockHandler;
import me.ichun.mods.ichunutil.common.grab.handlers.GrabbedFireballHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.EnumMap;

public class GrabHandler
{
    public String identifier;
    public EntityLivingBase grabber;
    public Entity grabbed;
    public int grabberId;
    public int grabbedId;
    public float grabDistance;
    public float yawTweak;
    public float pitchTweak;
    public boolean forceTerminate;
    public int time;

    //TODO handler entities travelling through dimensions if they're holding something in the new forge.
    public GrabHandler(EntityLivingBase grabber, Entity grabbed, float distance) //3.5F is a nice value for grab distance
    {
        this.identifier = RandomStringUtils.randomAscii(20);
        this.grabber = grabber;
        this.grabbed = grabbed;
        this.grabberId = grabber.getEntityId();
        this.grabbedId = grabbed.getEntityId();
        this.grabDistance = distance;
    }

    public GrabHandler(String identifier, int grabberId, int grabbedId, float dist)
    {
        this.identifier = identifier;
        this.grabberId = grabberId;
        this.grabbedId = grabbedId;
        this.grabDistance = dist;
    }

    public void update()
    {
        time++;

        Vec3d pos = EntityHelper.getEntityPositionEyes(grabber, 1.0F);
        grabber.rotationYawHead += yawTweak;
        grabber.rotationPitch += pitchTweak;
        Vec3d look = grabber.getLookVec(); //this is getLook(1.0F);
        grabber.rotationYawHead -= yawTweak;
        grabber.rotationPitch -= pitchTweak;
        Vec3d grabPos = new Vec3d(pos.xCoord + (look.xCoord * grabDistance), pos.yCoord + (look.yCoord * grabDistance), pos.zCoord + (look.zCoord * grabDistance));

        float distTolerance = grabToleranceTillTeleport();
        if(distTolerance > 0.0F && grabbed.getDistance(grabPos.xCoord, grabPos.yCoord, grabPos.zCoord) > distTolerance) //if grabbed is too far from the grabbed position, teleport to grabber and move to grabbed position?
        {
            grabbed.lastTickPosX = grabbed.prevPosX = grabbed.posX = grabber.posX;
            grabbed.lastTickPosY = grabbed.prevPosY = grabbed.posY = grabber.posY;
            grabbed.lastTickPosZ = grabbed.prevPosZ = grabbed.posZ = grabber.posZ;
            grabbed.setLocationAndAngles(grabbed.posX, grabbed.posY, grabbed.posZ, grabbed.rotationYaw, grabbed.rotationPitch);

            EntityHelper.setVelocity(grabbed, (grabPos.xCoord - grabbed.posX), (grabPos.yCoord - ((grabbed.getEntityBoundingBox().minY + grabbed.getEntityBoundingBox().maxY) / 2D)), (grabPos.zCoord - grabbed.posZ));
            grabbed.move(MoverType.SELF, grabbed.motionX, grabbed.motionY, grabbed.motionZ);
            grabbed.setLocationAndAngles(grabbed.posX, grabbed.posY, grabbed.posZ, grabbed.rotationYaw, grabbed.rotationPitch);
        }
        EntityHelper.setVelocity(grabbed, (grabPos.xCoord - grabbed.posX), (grabPos.yCoord - ((grabbed.getEntityBoundingBox().minY + grabbed.getEntityBoundingBox().maxY) / 2D)), (grabPos.zCoord - grabbed.posZ));
        grabbed.fallDistance = -(float)(grabbed.motionY * grabbed.motionY);
        grabbed.onGround = false;
        grabbed.isAirBorne = true;
        grabbed.timeUntilPortal = 5;
        for(GrabbedEntityHandler handler : entityHandlers)
        {
            if(handler.eligible(grabbed))
            {
                handler.handle(this);
            }
        }
    }

    public void transfer(GrabHandler transfer)
    {
        transfer.grabDistance = this.grabDistance;
    }

    public float grabToleranceTillTeleport() //0F to disable the teleport
    {
        return 5.0F;
    }

    public boolean shouldTerminate()
    {
        return forceTerminate || grabber != null && grabbed != null && (grabbed.isDead || !grabber.isEntityAlive() || grabbed == grabber.getRidingEntity() || grabbed.dimension != grabber.dimension || grabbed.getDistanceToEntity(grabber) > grabDistance + (grabToleranceTillTeleport() * 1.25D)); //if the enderman is >5D of grab distance, let go of it.
    }

    public boolean canSendAcrossDimensions()
    {
        return true;
    }

    public void terminate()
    {
    }

    @SideOnly(Side.CLIENT)
    public void getIDs()
    {
        WorldClient client = Minecraft.getMinecraft().world;
        if(grabber == null)
        {
            if(grabberId == -1)
            {
                grabber = Minecraft.getMinecraft().player;
            }
            else
            {
                Entity ent = client.getEntityByID(grabberId);
                if(ent instanceof EntityLivingBase)
                {
                    grabber = (EntityLivingBase)ent;
                }
            }
        }
        if(grabbed == null)
        {
            grabbed = client.getEntityByID(grabbedId);
        }
    }

    //grabbed entities and their handlers

    public static EnumMap<Side, ArrayList<GrabHandler>> grabbedEntities = new EnumMap<Side, ArrayList<GrabHandler>>(Side.class){{
        put(Side.SERVER, new ArrayList<GrabHandler>());
        put(Side.CLIENT, new ArrayList<GrabHandler>());
    }};

    public static ArrayList<Integer> dimensionalEntities = new ArrayList<Integer>();

    public static void tick(Side side)
    {
        ArrayList<GrabHandler> ents = grabbedEntities.get(side);
        for(int i = ents.size() - 1; i >= 0; i--)
        {
            GrabHandler handler = ents.get(i);
            if(handler.grabber != null && handler.grabbed != null)
            {
                if(handler.shouldTerminate())
                {
                    handler.terminate();
                    ents.remove(i);
                }
                else
                {
                    handler.update();
                }
            }
            else
            {
                handler.getIDs();
            }
        }
    }

    public static void grab(GrabHandler base, Side side)
    {
        ArrayList<GrabHandler> ents = grabbedEntities.get(side);
        ents.add(base);
    }

    public static GrabHandler release(EntityLivingBase grabber, Side side, Class<? extends GrabHandler> clz)
    {
        ArrayList<GrabHandler> ents = grabbedEntities.get(side);
        for(int i = ents.size() - 1; i >= 0; i--)
        {
            GrabHandler handler = ents.get(i);
            if(handler.grabber == grabber && (clz == null || clz.isInstance(handler)))
            {
                handler.terminate();
                ents.remove(i);
                return handler;
            }
        }
        return null;
    }

    public static ArrayList<GrabHandler> getHandlers(EntityLivingBase grabber, Side side)
    {
        ArrayList<GrabHandler> handlers = new ArrayList<GrabHandler>();

        ArrayList<GrabHandler> ents = grabbedEntities.get(side);
        for(int i = ents.size() - 1; i >= 0; i--)
        {
            GrabHandler handler = ents.get(i);
            if(handler.grabber == grabber)
            {
                handlers.add(handler);
            }
        }
        return handlers;
    }

    public static boolean hasHandlerType(EntityLivingBase grabber, Side side, Class<? extends GrabHandler> clz)
    {
        return getFirstHandler(grabber, side, clz) != null;
    }

    public static GrabHandler getFirstHandler(EntityLivingBase grabber, Side side, Class<? extends GrabHandler> clz)
    {
        for(GrabHandler handler : getHandlers(grabber, side))
        {
            if((clz == null || clz.isInstance(handler)))
            {
                return handler;
            }
        }
        return null;
    }

    public static boolean isGrabbed(Entity grabbed, Side side)
    {
        ArrayList<GrabHandler> ents = grabbedEntities.get(side);
        for(int i = ents.size() - 1; i >= 0; i--)
        {
            GrabHandler handler = ents.get(i);
            if(handler.grabbed == grabbed)
            {
                return true;
            }
        }
        return false;
    }

    //entity handlers section

    private static ArrayList<GrabbedEntityHandler> entityHandlers = new ArrayList<GrabbedEntityHandler>() {{
        add(new GrabbedFireballHandler());
        add(new GrabbedFallingBlockHandler());
        add(new GrabbedEntityBlockHandler());
    }};

    public interface GrabbedEntityHandler
    {
        boolean eligible(Entity grabbed);
        void handle(GrabHandler grabHandler);
    }

    public static void registerEntityHandler(GrabbedEntityHandler handler)
    {
        entityHandlers.add(handler);
    }
}
