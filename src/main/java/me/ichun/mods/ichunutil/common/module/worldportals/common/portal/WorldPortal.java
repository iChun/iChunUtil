package me.ichun.mods.ichunutil.common.module.worldportals.common.portal;

import me.ichun.mods.ichunutil.client.render.RendererHelper;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.entity.EntityBlock;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.worldportals.common.WorldPortals;
import me.ichun.mods.ichunutil.common.module.worldportals.common.packet.PacketEntityLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRain;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

public abstract class WorldPortal
{
    private ArrayList<EnumFacing> facesOn;
    private ArrayList<EnumFacing> upDirs; //Upwards direction of the portal.

    private QuaternionFormula quaternionFormula; //used to calculate the rotation and the positional offset (as well as motion)

    private AxisAlignedBB plane;
    private AxisAlignedBB flatPlane; //AABB that defines the plane where the magic happens.
    public AxisAlignedBB scanRange;
    public AxisAlignedBB portalInsides;

    private float width;
    private float height;

    private ArrayList<Vec3d> positions;
    private ArrayList<BlockPos> posBlocks;

    private HashSet<AxisAlignedBB> collisions;

    private WorldPortal pair;

    public World world;
    public int time;
    public List<Entity> lastScanEntities = new ArrayList<>();
    public HashMap<Entity, Integer> teleportCooldown = new HashMap<>();

    private boolean firstUpdate;

    public WorldPortal(World world)
    {
        this.world = world;
        this.positions = new ArrayList<>();
        this.positions.add(new Vec3d(0,0,0));
        this.posBlocks = new ArrayList<>();
        this.posBlocks.add(new BlockPos(this.positions.get(0)));

        this.facesOn = new ArrayList<>();
        this.facesOn.add(EnumFacing.NORTH);
        this.upDirs = new ArrayList<>();
        this.upDirs.add(EnumFacing.UP);

        this.time = 0;
        this.firstUpdate = true;
    }

    public WorldPortal(World world, Vec3d position, EnumFacing faceOn, EnumFacing upDir, float width, float height)
    {
        this.world = world;
        this.positions = new ArrayList<>();
        this.positions.add(position);
        this.posBlocks = new ArrayList<>();
        this.posBlocks.add(new BlockPos(this.positions.get(0)));

        this.facesOn = new ArrayList<>();
        this.facesOn.add(faceOn);
        this.upDirs = new ArrayList<>();
        this.upDirs.add(upDir);

        this.width = width;
        this.height = height;

        this.setupAABBs();

        this.time = 0;
        this.firstUpdate = true;
    }

    public abstract float getPlaneOffset();

    public abstract boolean canCollideWithBorders();

    public abstract String owner(); //mod that owns this;

    @SideOnly(Side.CLIENT)
    public abstract void drawPlane(float partialTick);

    public void setFace(EnumFacing faceOut, EnumFacing upDir)
    {
        facesOn.clear();
        facesOn.add(faceOut);
        upDirs.clear();
        upDirs.add(upDir);
        setupAABBs();
    }

    public void addFace(EnumFacing faceOut, EnumFacing upDir, Vec3d position) //TODO positions?
    {
//        if(!facesOn.contains(faceOut))
        {
            facesOn.add(faceOut);
            upDirs.add(upDir);
            positions.add(position);
            posBlocks.add(new BlockPos(position));
            setupAABBs();
        }
    }

    public ArrayList<EnumFacing> getFacesOn()
    {
        return facesOn;
    }

    public EnumFacing getFaceOn()
    {
        return facesOn.get(0);
    }

    public EnumFacing getUpDir()
    {
        return upDirs.get(0);
    }

    public ArrayList<BlockPos> getPoses()
    {
        return posBlocks;
    }

    public BlockPos getPos()
    {
        return posBlocks.get(0);
    }

    public float getWidth()
    {
        return width;
    }

    public float getHeight()
    {
        return height;
    }

    public void setSize(float width, float height)
    {
        this.width = width;
        this.height = height;
        setupAABBs();
    }

    public float getScanDistance()
    {
        return 3F;
    }

    public void updateWorldPortal()
    {
        if(firstUpdate)
        {
            firstUpdate = false;
        }
        time++;
        if(!canTeleportEntities())
        {
            return;
        }
        Iterator<Map.Entry<Entity, Integer>> ite = teleportCooldown.entrySet().iterator();
        while(ite.hasNext())
        {
            Map.Entry<Entity, Integer> e = ite.next();
            e.setValue(e.getValue() - 1);
            if(e.getValue() < 0)
            {
                WorldPortals.eventHandler.removeMonitoredEntity(e.getKey(), this);
                ite.remove();
            }
        }
        if(!hasPair())
        {
            return;
        }

        EnumFacing faceOn = getFaceOn();
        List<Entity> entitiesInRange = world.getEntitiesWithinAABB(Entity.class, scanRange);
        for(int i = entitiesInRange.size() - 1; i >= 0; i--)
        {
            Entity ent = entitiesInRange.get(i);

            if(isAgainstWall())
            {
                WorldPortals.eventHandler.addMonitoredEntity(ent, this);
            }
            if(!canEntityTeleport(ent))
            {
                entitiesInRange.remove(i);
                continue;
            }
            if(teleportCooldown.containsKey(ent) || ent instanceof EntityPlayerMP && !ent.getEntityWorld().isRemote)
            {
                continue;
            }

            double[] motions = EntityHelper.simulateMoveEntity(ent, ent.motionX, ent.motionY, ent.motionZ);
            Vec3d newEntPos = new Vec3d(ent.posX + motions[0], ent.posY + ent.getEyeHeight() + motions[1], ent.posZ + motions[2]);
            boolean teleport = false;
            AxisAlignedBB teleportPlane = flatPlane;
            float offset = 0.0F; //should I test player width specifically?
            if(isAgainstWall() && ent instanceof EntityPlayer)
            {
                offset = Math.min(0.05F, (float)Math.abs((flatPlane.minX - ent.posX) * faceOn.getFrontOffsetX() + (flatPlane.minY - ent.posY) * faceOn.getFrontOffsetY() + (flatPlane.minZ - ent.posZ) * faceOn.getFrontOffsetZ()));
                if(!scanRange.offset(faceOn.getFrontOffsetX() * offset, faceOn.getFrontOffsetY() * offset, faceOn.getFrontOffsetZ() * offset).contains(newEntPos) &&
                        portalInsides.offset(faceOn.getFrontOffsetX() * offset, faceOn.getFrontOffsetY() * offset, faceOn.getFrontOffsetZ() * offset).contains(newEntPos) &&
                        (faceOn.getAxis().isHorizontal() && ent.getEntityBoundingBox().minY >= flatPlane.minY && ent.getEntityBoundingBox().maxY <= flatPlane.maxY || faceOn.getAxis().isVertical()  && ent.getEntityBoundingBox().minX >= flatPlane.minX && ent.getEntityBoundingBox().maxX <= flatPlane.maxX  && ent.getEntityBoundingBox().minZ >= flatPlane.minZ && ent.getEntityBoundingBox().maxZ <= flatPlane.maxZ) // special casing cause of pushOutOfBlocks for player
                        )
                {
                    teleportPlane = getTeleportPlane(offset);
                    teleport = true;
                }
            }
            else
            {
                if(!scanRange.contains(newEntPos) && portalInsides.contains(newEntPos))
                {
                    teleport = true;
                }
            }

            if(teleport)
            {
                double centerX = (teleportPlane.maxX + teleportPlane.minX) / 2D;
                double centerY = (teleportPlane.maxY + teleportPlane.minY) / 2D;
                double centerZ = (teleportPlane.maxZ + teleportPlane.minZ) / 2D;

                if(pair != null)
                {
                    float[] appliedOffset = getQuaternionFormula().applyPositionalRotation(new float[] { (float)(newEntPos.x - centerX), (float)(newEntPos.y - centerY), (float)(newEntPos.z - centerZ) });
                    float[] appliedMotion = getQuaternionFormula().applyPositionalRotation(new float[] { (float)motions[0], (float)motions[1], (float)motions[2] });
                    float[] appliedRotation = getQuaternionFormula().applyRotationalRotation(new float[] { ent.rotationYaw, ent.rotationPitch, ent.getEntityWorld().isRemote ? getRoll(ent) : 0F });

                    AxisAlignedBB pairTeleportPlane = pair.getTeleportPlane(offset);

                    double destX = (pairTeleportPlane.maxX + pairTeleportPlane.minX) / 2D;
                    double destY = (pairTeleportPlane.maxY + pairTeleportPlane.minY) / 2D;
                    double destZ = (pairTeleportPlane.maxZ + pairTeleportPlane.minZ) / 2D;

                    EntityTransformationStack ets = new EntityTransformationStack(ent);
                    ets.translate(destX - ent.posX + appliedOffset[0], destY - (ent.posY + ent.getEyeHeight()) + appliedOffset[1], destZ - ent.posZ + appliedOffset[2]); //go to the centre of the dest portal and offset with the fields
                    ets.rotate(appliedRotation[0], appliedRotation[1], appliedRotation[2]);

                    ent.setPosition(ent.posX, ent.posY, ent.posZ);
                    double maxWidthHeight = Math.max(ent.width, ent.height);
                    EntityHelper.putEntityWithinAABB(ent, pair.scanRange.expand(pair.getFaceOn().getFrontOffsetX() * -maxWidthHeight, pair.getFaceOn().getFrontOffsetY() * -maxWidthHeight, pair.getFaceOn().getFrontOffsetZ() * -maxWidthHeight));

                    ent.motionX = appliedMotion[0];
                    ent.motionY = appliedMotion[1];
                    ent.motionZ = appliedMotion[2];

                    //no going faster than 1 block a tick
                    if(Math.abs(ent.motionX) > 0.99D)
                    {
                        ent.motionX /= Math.abs(ent.motionX) + 0.001D;
                    }
                    if(Math.abs(ent.motionY) > 0.99D)
                    {
                        ent.motionY /= Math.abs(ent.motionY) + 0.001D;
                    }
                    if(Math.abs(ent.motionZ) > 0.99D)
                    {
                        ent.motionZ /= Math.abs(ent.motionZ) + 0.001D;
                    }
                    ent.fallDistance = 0.1F * ((float)ent.motionY / -0.1F * (float)ent.motionY / -0.1F);
                    ent.setPosition(ent.posX, ent.posY, ent.posZ);

                    //transfer over this entity to the other portal.
                    pair.teleportCooldown.put(ent, 3);
                    pair.lastScanEntities.add(ent);
                    if(pair.isAgainstWall())
                    {
                        WorldPortals.eventHandler.addMonitoredEntity(ent, pair);
                    }
                    teleportCooldown.put(ent, 3);
                    lastScanEntities.remove(ent);
//                    if(isAgainstWall()) //now removed by the teleport cooldown
//                    {
//                        WorldPortals.eventHandler.removeMonitoredEntity(ent, this);
//                    }

                    handleSpecialEntities(ent);

                    if(ent.getEntityWorld().isRemote)
                    {
                        handleClientEntityTeleport(ent, appliedRotation);
                    }
                    else
                    {
                        WorldPortals.channel.sendToAllAround(new PacketEntityLocation(ent), new NetworkRegistry.TargetPoint(ent.dimension, ent.posX, ent.posY, ent.posZ, 256D));
                    }
                }
            }
        }

        if(world.isRemote)
        {
            handleClient();
        }

        if(isAgainstWall())
        {
            lastScanEntities.removeAll(entitiesInRange); // now contains entities that are out of the range. Remove this from the tracking.
            for(Entity ent : lastScanEntities)
            {
                if(!teleportCooldown.containsKey(ent))
                {
                    WorldPortals.eventHandler.removeMonitoredEntity(ent, this);
                }
            }

            lastScanEntities = entitiesInRange;
        }
    }

    @SideOnly(Side.CLIENT)
    public float getRoll(Entity ent)
    {
        if(ent == Minecraft.getMinecraft().getRenderViewEntity())
        {
            return WorldPortals.eventHandlerClient.cameraRoll;
        }
        return 0F;
    }

    public boolean canEntityTeleport(Entity ent)
    {
        return true;
    }

    public void handleSpecialEntities(Entity ent)
    {
        if(ent instanceof EntityBlock)
        {
            ((EntityBlock)ent).timeExisting = 2;
        }
        else if(ent instanceof EntityFallingBlock)
        {
            ((EntityFallingBlock)ent).fallTime = 2;
        }
        else if(ent instanceof EntityFireball)
        {
            EntityFireball fireball = (EntityFireball)ent;
            float[] appliedAcceleration = getQuaternionFormula().applyPositionalRotation(new float[] { (float)fireball.accelerationX, (float)fireball.accelerationY, (float)fireball.accelerationZ });
            fireball.accelerationX = appliedAcceleration[0];
            fireball.accelerationY = appliedAcceleration[1];
            fireball.accelerationZ = appliedAcceleration[2];
        }
        else if(ent instanceof EntityArrow)
        {
            ((EntityArrow)ent).inGround = false;
        }
    }

    @SideOnly(Side.CLIENT)
    public void handleClient()
    {
        Minecraft mc = Minecraft.getMinecraft();

        //TODO a config for this?
        EnumFacing faceOn = getFaceOn();
        for (int i = 0; i < 4; ++i)
        {
            for (int j = 0; j < 2; ++j)
            {
                for(Particle particle : mc.effectRenderer.fxLayers[i][j])
                {
                    Vec3d particlePos = new Vec3d(particle.prevPosX, particle.prevPosY, particle.prevPosZ); //motion isn't accessible.
                    Vec3d newParticlePos = new Vec3d(particle.posX, particle.posY, particle.posZ);

                    float offset = (float)Math.abs((particle.prevPosX - particle.posX) * faceOn.getFrontOffsetX() * 1.5D + (particle.prevPosY - particle.posY) * faceOn.getFrontOffsetY() * 1.5D + (particle.prevPosZ - particle.posZ) * faceOn.getFrontOffsetZ() * 1.5D);
                    boolean isRain = particle instanceof ParticleRain && faceOn == EnumFacing.UP && scanRange.contains(particlePos);
                    if(isRain || !portalInsides.offset(faceOn.getFrontOffsetX() * offset, faceOn.getFrontOffsetY() * offset, faceOn.getFrontOffsetZ() * offset).intersects(particle.getBoundingBox()) && portalInsides.offset(faceOn.getFrontOffsetX() * offset, faceOn.getFrontOffsetY() * offset, faceOn.getFrontOffsetZ() * offset).intersects(particle.getBoundingBox().offset(particle.motionX, particle.motionY, particle.motionZ)))
                    {
                        AxisAlignedBB teleportPlane = getTeleportPlane(offset);

                        double centerX = (teleportPlane.maxX + teleportPlane.minX) / 2D;
                        double centerY = (teleportPlane.maxY + teleportPlane.minY) / 2D;
                        double centerZ = (teleportPlane.maxZ + teleportPlane.minZ) / 2D;

                        if(pair != null)
                        {
                            float[] appliedOffset = getQuaternionFormula().applyPositionalRotation(new float[] { (float)(newParticlePos.x - centerX), (float)(newParticlePos.y - centerY), (float)(newParticlePos.z - centerZ) });
                            float[] appliedMotion = getQuaternionFormula().applyPositionalRotation(new float[] { (float)(newParticlePos.x - particlePos.x), (float)(newParticlePos.y - particlePos.y), (float)(newParticlePos.z - particlePos.z) });

                            AxisAlignedBB pairTeleportPlane = pair.getTeleportPlane(offset);

                            double destX = (pairTeleportPlane.maxX + pairTeleportPlane.minX) / 2D;
                            double destY = (pairTeleportPlane.maxY + pairTeleportPlane.minY) / 2D;
                            double destZ = (pairTeleportPlane.maxZ + pairTeleportPlane.minZ) / 2D;

                            double x = destX - particle.posX + appliedOffset[0];
                            double y = destY - particle.posY + appliedOffset[1];
                            double z = destZ - particle.posZ + appliedOffset[2];
                            particle.posX += x;
                            particle.posY += y;
                            particle.posZ += z;
                            particle.prevPosX += x;
                            particle.prevPosY += y;
                            particle.prevPosZ += z;
                            particle.setPosition(particle.posX, particle.posY, particle.posZ);
                            particle.motionX = appliedMotion[0];
                            particle.motionY = appliedMotion[1];
                            particle.motionZ = appliedMotion[2];
                            if(isRain)
                            {
                                particle.motionX *= 5D;
                                particle.motionY *= 5D;
                                particle.motionZ *= 5D;
                            }
                        }
                    }
                }
            }
        }
    }

    public void terminate()
    {
        if(isAgainstWall())
        {
            for(Entity ent : lastScanEntities)
            {
                if(ent.getEntityBoundingBox().intersects(portalInsides))
                {
                    EnumFacing faceOn = getFaceOn();
                    EntityHelper.putEntityWithinAABB(ent, flatPlane.offset(faceOn.getFrontOffsetX() * 0.5D, faceOn.getFrontOffsetY() * 0.5D, faceOn.getFrontOffsetZ() * 0.5D));
                    ent.setPosition(ent.posX, ent.posY, ent.posZ);
                }
                WorldPortals.eventHandler.removeMonitoredEntity(ent, this);
            }
        }
        if(hasPair())
        {
            pair.setPair(null);
            setPair(null);
        }
    }

    public boolean isValid()
    {
        return !firstUpdate;
    }

    public boolean isFirstUpdate()
    {
        return firstUpdate;
    }

    public void forceFirstUpdate()
    {
        firstUpdate = true;
    }

    //Only for WorldPortals that can teleport
    public boolean isAgainstWall() //you have world, pos, faceOn, etc all to check. This is to remove the collision behind the portal.
    {
        return false;
    }

    private AxisAlignedBB createPlaneAround(double size)
    {
        return createPlaneAround(getPosition(), size);
    }

    private AxisAlignedBB createPlaneAround(Vec3d pos, double size)
    {
        double halfW = width / 2D;
        double halfH = height / 2D;

        AxisAlignedBB plane = new AxisAlignedBB(pos.x - halfW, pos.y - halfH, pos.z - size, pos.x + halfW, pos.y + halfH, pos.z + size);
        EnumFacing faceOn = getFaceOn();
        if(faceOn.getAxis() == EnumFacing.Axis.Y)
        {
            plane = EntityHelper.rotateAABB(EnumFacing.Axis.X, plane, faceOn == EnumFacing.UP ? -90F : 90F, pos.x, pos.y, pos.z);
        }
        plane = EntityHelper.rotateAABB(EnumFacing.Axis.Y, plane, faceOn.getAxis() == EnumFacing.Axis.X ? 90F : faceOn.getAxis() == EnumFacing.Axis.Y && getUpDir().getAxis() == EnumFacing.Axis.X ? 90F : 0F, pos.x, pos.y, pos.z).offset(faceOn.getFrontOffsetX() * getPlaneOffset(), faceOn.getFrontOffsetY() * getPlaneOffset(), faceOn.getFrontOffsetZ() * getPlaneOffset());
        return plane;
    }

    public AxisAlignedBB getCollisionRemovalAabbForEntity(Entity ent)
    {
        double max = Math.max(Math.max(ent.width, ent.height) + Math.sqrt(ent.motionX * ent.motionX + ent.motionY * ent.motionY + ent.motionZ * ent.motionZ), 1D);
        EnumFacing faceOn = getFaceOn();
        return flatPlane.expand(faceOn.getFrontOffsetX() * -max, faceOn.getFrontOffsetY() * -max, faceOn.getFrontOffsetZ() * -max);
    }

    public AxisAlignedBB getPortalInsides(Entity ent)
    {
        if(isAgainstWall() && ent instanceof EntityPlayer)
        {
            EnumFacing faceOn = getFaceOn();
            float offset = Math.min(0.05F, (float)Math.abs((flatPlane.minX - ent.posX) * faceOn.getFrontOffsetX() + (flatPlane.minY - ent.posY) * faceOn.getFrontOffsetY() + (flatPlane.minZ - ent.posZ) * faceOn.getFrontOffsetZ()));
            return portalInsides.offset(faceOn.getFrontOffsetX() * offset, faceOn.getFrontOffsetY() * offset, faceOn.getFrontOffsetZ() * offset);
        }
        return portalInsides;
    }

    public AxisAlignedBB getPlane()
    {
        return plane;
    }

    private void setupAABBs()
    {
        EnumFacing faceOn = getFaceOn();
        plane = createPlaneAround(0.0125D);
        flatPlane = createPlaneAround(0);
        scanRange = flatPlane.expand(faceOn.getFrontOffsetX() * getScanDistance(), faceOn.getFrontOffsetY() * getScanDistance(), faceOn.getFrontOffsetZ() * getScanDistance());
        portalInsides = flatPlane.expand(faceOn.getFrontOffsetX() * -100D, faceOn.getFrontOffsetY() * -100D, faceOn.getFrontOffsetZ() * -100D);
    }

    public AxisAlignedBB getFlatPlane()
    {
        return flatPlane;
    }

    public AxisAlignedBB getTeleportPlane(float offset)
    {
        if(offset != 0F)
        {
            EnumFacing faceOn = getFaceOn();
            return flatPlane.offset(faceOn.getFrontOffsetX() * offset, faceOn.getFrontOffsetY() * offset, faceOn.getFrontOffsetZ() * offset);
        }
        return flatPlane;
    }

    public boolean canTeleportEntities()
    {
        return facesOn.size() == 1;
    }

    public HashSet<AxisAlignedBB> getCollisionBoundaries()
    {
        if(collisions == null)
        {
            collisions = new HashSet<>(4);

            if(canCollideWithBorders())
            {
                double size = 0.0125D;
                AxisAlignedBB plane = flatPlane;

                if(plane.maxX - plane.minX > size * 3D)
                {
                    collisions.add(new AxisAlignedBB(plane.maxX, plane.minY, plane.minZ, plane.maxX + size, plane.maxY, plane.maxZ));
                    collisions.add(new AxisAlignedBB(plane.minX - size, plane.minY, plane.minZ, plane.minX, plane.maxY, plane.maxZ));
                }
                if(plane.maxY - plane.minY > size * 3D)
                {
                    collisions.add(new AxisAlignedBB(plane.minX, plane.maxY, plane.minZ, plane.maxX, plane.maxY + size, plane.maxZ));
                    collisions.add(new AxisAlignedBB(plane.minX, plane.minY - size, plane.minZ, plane.maxX, plane.minY, plane.maxZ));
                }
                if(plane.maxZ - plane.minZ > size * 3D)
                {
                    collisions.add(new AxisAlignedBB(plane.minX, plane.minY, plane.maxZ, plane.maxX, plane.maxY, plane.maxZ + size));
                    collisions.add(new AxisAlignedBB(plane.minX, plane.minY, plane.minZ - size, plane.maxX, plane.maxY, plane.minZ));
                }
            }
        }
        return collisions;
    }

    public boolean hasPair()
    {
        return pair != null && pair.positions.get(0).y > 0D;
    }

    public void setPair(WorldPortal portal)
    {
        if(pair != portal)
        {
            pair = portal;
            if(pair != null)
            {
                quaternionFormula = QuaternionFormula.createFromPlanes(getFaceOn(), getUpDir(), pair.getFaceOn(), pair.getUpDir());
            }
        }
    }

    public WorldPortal getPair()
    {
        return pair;
    }

    public void setPosition(Vec3d v)
    {
        this.positions.clear();
        this.positions.add(v);
        this.posBlocks.clear();
        this.posBlocks.add(new BlockPos(v));
        setupAABBs();
    }

    public Vec3d getPosition() //position of the world portal, pre-offset
    {
        return positions.get(0);
    }

    public ArrayList<Vec3d> getPositions()
    {
        return positions;
    }

    public QuaternionFormula getQuaternionFormula()
    {
        return pair != null ? quaternionFormula : QuaternionFormula.NO_ROTATION;
    }

    public NBTTagCompound write(NBTTagCompound tag)
    {
        return writePair(writeSelf(tag));
    }

    public NBTTagCompound writeSelf(NBTTagCompound tag)
    {
        tag.setFloat("width", width);
        tag.setFloat("height", height);

        tag.setInteger("facesOn", facesOn.size());
        for(int i = 0; i < facesOn.size(); i++)
        {
            tag.setInteger("faceOn_" + i, facesOn.get(i).getIndex());
            tag.setInteger("up_" + i, upDirs.get(i).getIndex());
        }

        tag.setInteger("positions", positions.size());
        for(int i = 0; i < positions.size(); i++)
        {
            Vec3d position = positions.get(i);
            tag.setDouble("posX_" + i, position.x);
            tag.setDouble("posY_" + i, position.y);
            tag.setDouble("posZ_" + i, position.z);
        }

        tag.setInteger("time", time);

        return tag;
    }

    public NBTTagCompound writePair(NBTTagCompound tag)
    {
        if(hasPair())
        {
            tag.setTag("pair", pair.writeSelf(new NBTTagCompound()));
        }
        return tag;
    }

    public void read(NBTTagCompound tag)
    {
        readSelf(tag);
        readPair(tag);
    }

    public void readSelf(NBTTagCompound tag)
    {
        setSize(tag.getFloat("width"), tag.getFloat("height"));
        if(tag.hasKey("faceOn"))//old stuff TODO deprecate/remove in 1.13
        {
            setFace(EnumFacing.getFront(tag.getInteger("faceOn")), EnumFacing.getFront(tag.getInteger("up")));
        }
        else
        {
            int facesOnCount = tag.getInteger("facesOn");
            for(int i = 0; i < facesOnCount; i++)
            {
                if(i == 0)
                {
                    setFace(EnumFacing.getFront(tag.getInteger("faceOn_" + i)), EnumFacing.getFront(tag.getInteger("up_" + i)));
                    setPosition(new Vec3d(tag.getDouble("posX_" + i), tag.getDouble("posY_" + i), tag.getDouble("posZ_" + i)));
                }
                else
                {
                    addFace(EnumFacing.getFront(tag.getInteger("faceOn_" + i)), EnumFacing.getFront(tag.getInteger("up_" + i)), new Vec3d(tag.getDouble("posX_" + i), tag.getDouble("posY_" + i), tag.getDouble("posZ_" + i)));
                }
            }
        }
        if(tag.hasKey("posX"))//old stuff TODO deprecate/remove in 1.13
        {
            setPosition(new Vec3d(tag.getDouble("posX"), tag.getDouble("posY"), tag.getDouble("posZ")));
        }

        time = tag.getInteger("time");

        firstUpdate = true;
    }

    public void readPair(NBTTagCompound tag)
    {
        if(tag.hasKey("pair"))
        {
            setPair(createFakeInstance(tag.getCompoundTag("pair")));
        }
    }

    public abstract <T extends WorldPortal> T createFakeInstance(NBTTagCompound tag);

    @SideOnly(Side.CLIENT)
    public void handleClientEntityTeleport(Entity ent, float[] rotations)
    {
        if(ent == Minecraft.getMinecraft().player)
        {
            WorldPortals.eventHandlerClient.prevCameraRoll = WorldPortals.eventHandlerClient.cameraRoll = rotations[2];
            WorldPortals.channel.sendToServer(new PacketEntityLocation(ent));
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldRenderFront(Entity viewer, float partialTicks) //TODO THIS
    {
        Vec3d position = RendererHelper.getCameraPosition(viewer, partialTicks);
        for(EnumFacing faceOn : facesOn)
        {
            if(faceOn.getFrontOffsetX() < 0 && position.x < flatPlane.minX || faceOn.getFrontOffsetX() > 0 && position.x > flatPlane.minX ||
                    faceOn.getFrontOffsetY() < 0 && position.y < flatPlane.minY || faceOn.getFrontOffsetY() > 0 && position.y > flatPlane.minY ||
                    faceOn.getFrontOffsetZ() < 0 && position.z < flatPlane.minZ || faceOn.getFrontOffsetZ() > 0 && position.z > flatPlane.minZ)
            {
                return true;
            }
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    public int getRenderDistanceChunks()
    {
        return iChunUtil.config.renderDistanceChunks == 0 ? Minecraft.getMinecraft().gameSettings.renderDistanceChunks : iChunUtil.config.renderDistanceChunks;
    }
}
