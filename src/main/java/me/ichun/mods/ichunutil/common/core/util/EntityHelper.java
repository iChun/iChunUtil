package me.ichun.mods.ichunutil.common.core.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
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
