package me.ichun.mods.ichunutil.common.core.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Method;
import java.util.HashMap;
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
        double d0 = faced.posX - facer.posX;
        double d1 = faced.posZ - facer.posZ;
        double d2;

        if (faced instanceof EntityLivingBase)
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase)faced;
            d2 = entitylivingbase.posY + (double)entitylivingbase.getEyeHeight() - (facer.posY + (double)facer.getEyeHeight());
        }
        else
        {
            d2 = (faced.getEntityBoundingBox().minY + faced.getEntityBoundingBox().maxY) / 2.0D - (facer.posY + (double)facer.getEyeHeight());
        }

        double d3 = (double)MathHelper.sqrt_double(d0 * d0 + d1 * d1);
        float f2 = (float)(Math.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
        float f3 = (float)(-(Math.atan2(d2, d3) * 180.0D / Math.PI));
        facer.rotationPitch = updateRotation(facer.rotationPitch, f3, maxPitch);
        facer.rotationYaw = updateRotation(facer.rotationYaw, f2, maxYaw);
    }

    public static void faceLocation(Entity facer, double posX, double posY, double posZ, float maxYaw, float maxPitch)
    {
        double d0 = posX - facer.posX;
        double d1 = posY - facer.posZ;
        double d2 = posZ - (facer.posY + (double)facer.getEyeHeight());

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

}
