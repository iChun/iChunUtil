package me.ichun.mods.ichunutil.common.core.util;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class EntityHelper
{
    public static final UUID uuidExample = UUID.fromString("DEADBEEF-DEAD-BEEF-DEAD-DEADBEEFD00D");

    private static HashMap<String, GameProfile> nameToFullProfileMap = new HashMap<String, GameProfile>();

    @SideOnly(Side.CLIENT)
    public static void injectMinecraftPlayerGameProfile()
    {
        nameToFullProfileMap.put(Minecraft.getMinecraft().getSession().getUsername(), Minecraft.getMinecraft().getSession().getProfile());
    }

    public static float healthScale;
    public static int statusBarTime;
    public static String bossName;
    public static boolean hasColorModifier;

    @SideOnly(Side.CLIENT)
    public static void storeBossStatus()
    {
        healthScale = BossStatus.healthScale;
        statusBarTime = BossStatus.statusBarTime;
        bossName = BossStatus.bossName;
        hasColorModifier = BossStatus.hasColorModifier;
    }

    @SideOnly(Side.CLIENT)
    public static void restoreBossStatus()
    {
        BossStatus.healthScale = healthScale;
        BossStatus.statusBarTime = statusBarTime;
        BossStatus.bossName = bossName;
        BossStatus.hasColorModifier = hasColorModifier;
    }


    public static <T extends EntityLivingBase> String getHurtSound(T ent, Class clz)
    {
        try
        {
            Method m = clz.getDeclaredMethod(ObfHelper.obfuscated() ? ObfHelper.getHurtSoundObf : ObfHelper.getHurtSoundDeobf);
            m.setAccessible(true);
            return (String)m.invoke(ent);
        }
        catch(NoSuchMethodException e)
        {
            if(clz != EntityLivingBase.class)
            {
                return getHurtSound(ent, clz.getSuperclass());
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return "game.neutral.hurt";
    }

    public static <T extends EntityLivingBase> String getDeathSound(T ent, Class clz)
    {
        try
        {
            Method m = clz.getDeclaredMethod(ObfHelper.obfuscated() ? ObfHelper.getDeathSoundObf : ObfHelper.getDeathSoundDeobf);
            m.setAccessible(true);
            return (String)m.invoke(ent);
        }
        catch(NoSuchMethodException e)
        {
            if(clz != EntityLivingBase.class)
            {
                return getDeathSound(ent, clz.getSuperclass());
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return "game.neutral.die";
    }

    public static float updateRotation(float oriRot, float intendedRot, float maxChange)
    {
        float var4 = MathHelper.wrapAngleTo180_float(intendedRot - oriRot);

        if (var4 > maxChange)
        {
            var4 = maxChange;
        }

        if (var4 < -maxChange)
        {
            var4 = -maxChange;
        }

        return oriRot + var4;
    }

    public static void faceEntity(Entity facer, Entity faced, float maxYaw, float maxPitch)
    {
        faceLocation(facer, faced.posX, (faced instanceof EntityLivingBase ? (faced.posY + faced.getEyeHeight()) : (faced.getEntityBoundingBox().minY + faced.getEntityBoundingBox().maxY) / 2D), faced.posZ, maxYaw, maxPitch);
    }

    public static void faceLocation(Entity facer, double posX, double posY, double posZ, float maxYaw, float maxPitch)
    {
        double d0 = posX - facer.posX;
        double d1 = posZ - facer.posZ;
        double d2 = posY - (facer.posY + (double)facer.getEyeHeight());

        double d3 = (double)MathHelper.sqrt_double(d0 * d0 + d1 * d1);
        float f2 = (float)(Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
        float f3 = (float)(-(Math.atan2(d2, d3) * 180.0D / Math.PI));
        facer.rotationPitch = updateRotation(facer.rotationPitch, f3, maxPitch);
        facer.rotationYaw = updateRotation(facer.rotationYaw, f2, maxYaw);
    }

    public static Vec3 getEntityPositionEyes(Entity ent, float partialTicks)
    {
        if (partialTicks == 1.0F)
        {
            return new Vec3(ent.posX, ent.posY + (double)ent.getEyeHeight(), ent.posZ);
        }
        else
        {
            double d0 = ent.prevPosX + (ent.posX - ent.prevPosX) * (double)partialTicks;
            double d1 = ent.prevPosY + (ent.posY - ent.prevPosY) * (double)partialTicks + (double)ent.getEyeHeight();
            double d2 = ent.prevPosZ + (ent.posZ - ent.prevPosZ) * (double)partialTicks;
            return new Vec3(d0, d1, d2);
        }
    }

    public static float interpolateRotation(float prevRotation, float nextRotation, float partialTick)
    {
        float f3;

        for (f3 = nextRotation - prevRotation; f3 < -180.0F; f3 += 360.0F)
        {
            ;
        }

        while (f3 >= 180.0F)
        {
            f3 -= 360.0F;
        }

        return prevRotation + partialTick * f3;
    }

    public static float interpolateValues(float prevVal, float nextVal, float partialTick)
    {
        return prevVal + partialTick * (nextVal - prevVal);
    }

    public static void setVelocity(Entity entity, double d, double d1, double d2)
    {
        entity.motionX = d;
        entity.motionY = d1;
        entity.motionZ = d2;
    }

    public static MovingObjectPosition getEntityLook(Entity ent, double d)
    {
        return getEntityLook(ent, d, false);
    }

    public static MovingObjectPosition getEntityLook(Entity ent, double d, boolean ignoreEntities) //goes through liquid
    {
        return getEntityLook(ent, d, ignoreEntities, false, true, 1.0F);
    }

    public static MovingObjectPosition getEntityLook(Entity ent, double d, boolean ignoreEntities, boolean ignoreTransparentBlocks, boolean ignoreLiquid, float renderTick)
    {
        Vec3 vec3 = getEntityPositionEyes(ent, renderTick);
        Vec3 vec31 = ent.getLook(renderTick);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * d, vec31.yCoord * d, vec31.zCoord * d);

        MovingObjectPosition rayTrace = rayTraceBlocks(ent.worldObj, d, vec3, vec32, !ignoreLiquid, ignoreTransparentBlocks, false, true);

        if(!ignoreEntities)
        {
            double dist = d;
            if(rayTrace != null)
            {
                dist = rayTrace.hitVec.distanceTo(vec3);
            }

            Entity entityTrace = null;
            Vec3 vec33 = null;
            float f = 1.0F;
            List<Entity> list = ent.worldObj.getEntitiesInAABBexcluding(ent, ent.getEntityBoundingBox().addCoord(vec31.xCoord * dist, vec31.yCoord * dist, vec31.zCoord * dist).expand((double)f, (double)f, (double)f), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>()
            {
                public boolean apply(Entity p_apply_1_)
                {
                    return p_apply_1_.canBeCollidedWith();
                }
            }));
            double d2 = dist;

            for (int j = 0; j < list.size(); ++j)
            {
                Entity entity1 = (Entity)list.get(j);
                float f1 = entity1.getCollisionBorderSize();
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand((double)f1, (double)f1, (double)f1);
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                if (axisalignedbb.isVecInside(vec3))
                {
                    if (d2 >= 0.0D)
                    {
                        entityTrace = entity1;
                        vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                        d2 = 0.0D;
                    }
                }
                else if (movingobjectposition != null)
                {
                    double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                    if (d3 < d2 || d2 == 0.0D)
                    {
                        if (entity1 == ent.ridingEntity && !ent.canRiderInteract())
                        {
                            if (d2 == 0.0D)
                            {
                                entityTrace = entity1;
                                vec33 = movingobjectposition.hitVec;
                            }
                        }
                        else
                        {
                            entityTrace = entity1;
                            vec33 = movingobjectposition.hitVec;
                            d2 = d3;
                        }
                    }
                }
            }

            if (entityTrace != null && (d2 < dist || rayTrace == null))
            {
                rayTrace = new MovingObjectPosition(entityTrace, vec33);
            }

        }

        return rayTrace;
    }

    public static MovingObjectPosition rayTraceBlocks(World world, double dist, Vec3 vec31, Vec3 vec32, boolean stopOnLiquid, boolean ignoreTransparentBlocks, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock)
    {
        if (!Double.isNaN(vec31.xCoord) && !Double.isNaN(vec31.yCoord) && !Double.isNaN(vec31.zCoord))
        {
            if (!Double.isNaN(vec32.xCoord) && !Double.isNaN(vec32.yCoord) && !Double.isNaN(vec32.zCoord))
            {
                int i = MathHelper.floor_double(vec32.xCoord);
                int j = MathHelper.floor_double(vec32.yCoord);
                int k = MathHelper.floor_double(vec32.zCoord);
                int l = MathHelper.floor_double(vec31.xCoord);
                int i1 = MathHelper.floor_double(vec31.yCoord);
                int j1 = MathHelper.floor_double(vec31.zCoord);
                BlockPos blockpos = new BlockPos(l, i1, j1);
                IBlockState iblockstate = world.getBlockState(blockpos);
                Block block = iblockstate.getBlock();

                if ((!ignoreBlockWithoutBoundingBox || block.getCollisionBoundingBox(world, blockpos, iblockstate) != null) && block.canCollideCheck(iblockstate, stopOnLiquid) && !(ignoreTransparentBlocks && isTransparent(block)))
                {
                    MovingObjectPosition movingobjectposition = block.collisionRayTrace(world, blockpos, vec31, vec32);

                    if (movingobjectposition != null)
                    {
                        return movingobjectposition;
                    }
                }

                MovingObjectPosition movingobjectposition2 = null;
                int k1 = (int)Math.ceil(dist + 1);

                while (k1-- >= 0)
                {
                    if (Double.isNaN(vec31.xCoord) || Double.isNaN(vec31.yCoord) || Double.isNaN(vec31.zCoord))
                    {
                        return null;
                    }

                    if (l == i && i1 == j && j1 == k)
                    {
                        return returnLastUncollidableBlock ? movingobjectposition2 : null;
                    }

                    boolean flag2 = true;
                    boolean flag = true;
                    boolean flag1 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;

                    if (i > l)
                    {
                        d0 = (double)l + 1.0D;
                    }
                    else if (i < l)
                    {
                        d0 = (double)l + 0.0D;
                    }
                    else
                    {
                        flag2 = false;
                    }

                    if (j > i1)
                    {
                        d1 = (double)i1 + 1.0D;
                    }
                    else if (j < i1)
                    {
                        d1 = (double)i1 + 0.0D;
                    }
                    else
                    {
                        flag = false;
                    }

                    if (k > j1)
                    {
                        d2 = (double)j1 + 1.0D;
                    }
                    else if (k < j1)
                    {
                        d2 = (double)j1 + 0.0D;
                    }
                    else
                    {
                        flag1 = false;
                    }

                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = vec32.xCoord - vec31.xCoord;
                    double d7 = vec32.yCoord - vec31.yCoord;
                    double d8 = vec32.zCoord - vec31.zCoord;

                    if (flag2)
                    {
                        d3 = (d0 - vec31.xCoord) / d6;
                    }

                    if (flag)
                    {
                        d4 = (d1 - vec31.yCoord) / d7;
                    }

                    if (flag1)
                    {
                        d5 = (d2 - vec31.zCoord) / d8;
                    }

                    if (d3 == -0.0D)
                    {
                        d3 = -1.0E-4D;
                    }

                    if (d4 == -0.0D)
                    {
                        d4 = -1.0E-4D;
                    }

                    if (d5 == -0.0D)
                    {
                        d5 = -1.0E-4D;
                    }

                    EnumFacing enumfacing;

                    if (d3 < d4 && d3 < d5)
                    {
                        enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                        vec31 = new Vec3(d0, vec31.yCoord + d7 * d3, vec31.zCoord + d8 * d3);
                    }
                    else if (d4 < d5)
                    {
                        enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                        vec31 = new Vec3(vec31.xCoord + d6 * d4, d1, vec31.zCoord + d8 * d4);
                    }
                    else
                    {
                        enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        vec31 = new Vec3(vec31.xCoord + d6 * d5, vec31.yCoord + d7 * d5, d2);
                    }

                    l = MathHelper.floor_double(vec31.xCoord) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                    i1 = MathHelper.floor_double(vec31.yCoord) - (enumfacing == EnumFacing.UP ? 1 : 0);
                    j1 = MathHelper.floor_double(vec31.zCoord) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
                    blockpos = new BlockPos(l, i1, j1);
                    IBlockState iblockstate1 = world.getBlockState(blockpos);
                    Block block1 = iblockstate1.getBlock();

                    if ((!ignoreBlockWithoutBoundingBox || block1.getCollisionBoundingBox(world, blockpos, iblockstate1) != null) && !(ignoreTransparentBlocks && isTransparent(block)))
                    {
                        if (block1.canCollideCheck(iblockstate1, stopOnLiquid))
                        {
                            MovingObjectPosition movingobjectposition1 = block1.collisionRayTrace(world, blockpos, vec31, vec32);

                            if (movingobjectposition1 != null)
                            {
                                return movingobjectposition1;
                            }
                        }
                        else
                        {
                            movingobjectposition2 = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec31, enumfacing, blockpos);
                        }
                    }
                }

                return returnLastUncollidableBlock ? movingobjectposition2 : null;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    public boolean destroyBlocksInAABB(Entity ent, AxisAlignedBB aabb)
    {
        int i = MathHelper.floor_double(aabb.minX);
        int j = MathHelper.floor_double(aabb.minY);
        int k = MathHelper.floor_double(aabb.minZ);
        int l = MathHelper.floor_double(aabb.maxX);
        int i1 = MathHelper.floor_double(aabb.maxY);
        int j1 = MathHelper.floor_double(aabb.maxZ);
        boolean flag = false;
        boolean flag1 = false;

        for (int k1 = i; k1 <= l; ++k1)
        {
            for (int l1 = j; l1 <= i1; ++l1)
            {
                for (int i2 = k; i2 <= j1; ++i2)
                {
                    BlockPos blockpos = new BlockPos(k1, l1, i2);
                    Block block = ent.worldObj.getBlockState(blockpos).getBlock();

                    if (!block.isAir(ent.worldObj, new BlockPos(k1, l1, i2)))
                    {
                        if (block.canEntityDestroy(ent.worldObj, new BlockPos(k1, l1, i2), ent) && ent.worldObj.getGameRules().getBoolean("mobGriefing"))
                        {
                            flag1 = (ent.worldObj.isRemote || (ent.worldObj.setBlockToAir(new BlockPos(k1, l1, i2)) || flag1));
                        }
                        else
                        {
                            flag = true;
                        }
                    }
                }
            }
        }

        if (flag1)
        {
            double d0 = aabb.minX + (aabb.maxX - aabb.minX) * (double)ent.worldObj.rand.nextFloat();
            double d1 = aabb.minY + (aabb.maxY - aabb.minY) * (double)ent.worldObj.rand.nextFloat();
            double d2 = aabb.minZ + (aabb.maxZ - aabb.minZ) * (double)ent.worldObj.rand.nextFloat();
            ent.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }

        return flag;
    }

    public static NBTTagCompound getPlayerPersistentData(EntityPlayer player) //gets the persisted NBT.
    {
        NBTTagCompound persistentTag = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        player.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persistentTag);
        return persistentTag;
    }

    public static NBTTagCompound getPlayerPersistentData(EntityPlayer player, String name) //gets a tag within the persisted NBT
    {
        NBTTagCompound persistentTag = getPlayerPersistentData(player).getCompoundTag(name);
        getPlayerPersistentData(player).setTag(name, persistentTag);
        return persistentTag;
    }

    public static boolean isTransparent(Block block)
    {
        return block.getLightOpacity() != 0xff;
    }
}
