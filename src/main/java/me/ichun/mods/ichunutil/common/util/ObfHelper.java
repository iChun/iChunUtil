package me.ichun.mods.ichunutil.common.util;

import net.minecraftforge.fml.loading.FMLLoader;

public class ObfHelper
{
    private static final String OBF_VERSION = "1.15.2";
    private static boolean devEnvironment;

    public static void detectDevEnvironment()
    {
        devEnvironment = FMLLoader.getNaming().equals("mcp");
    }
    public static boolean isDevEnvironment()
    {
        return devEnvironment;
    }

    public static final String getEntityTexture = "func_110775_a"; //IEntityRenderer
    public static final String preRenderCallback = "func_225620_a_"; //LivingRenderer
    public static final String getHurtSound = "func_184601_bQ"; //LivingEntity
    public static final String getDeathSound = "func_184615_bR"; //LivingEntity
    public static final String getSoundVolume = "func_70599_aP"; //LivingEntity
    public static final String getSoundPitch = "func_70647_i"; //LivingEntity
    public static final String onChangedPotionEffect = "func_70695_b"; //LivingEntity
}
