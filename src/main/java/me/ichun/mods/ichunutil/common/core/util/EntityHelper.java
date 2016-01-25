package me.ichun.mods.ichunutil.common.core.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
