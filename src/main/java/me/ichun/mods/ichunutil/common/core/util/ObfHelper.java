package me.ichun.mods.ichunutil.common.core.util;

import me.ichun.mods.ichunutil.common.block.BlockCompactPorkchop;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ObfHelper
{
    private static final String OBF_VERSION = "1.10.2";

    private static boolean isObfuscated;

    public static final String[] gameProfile = new String[] { "field_146106_i", "gameProfile" }; //EntityPlayer
    public static final String[] mainModel = new String[] { "field_77045_g", "mainModel" }; //RenderLivingBase
    public static final String[] resourceDomain = new String[] { "field_110626_a", "resourceDomain" }; //ResourceLocation
    public static final String[] resourcePath = new String[] { "field_110625_b", "resourcePath" }; //ResourceLocation

    //EntityLivingBase
    public static final String getHurtSoundObf = "func_184601_bQ";
    public static final String getHurtSoundDeobf = "getHurtSound";

    //EntityLivingBase
    public static final String getDeathSoundObf = "func_188593_c";
    public static final String getDeathSoundDeobf = "getDeathSound";

    //RenderLivingEntity
    public static final String preRenderCallbackObf = "func_77041_b";
    public static final String preRenderCallbackDeobf = "preRenderCallback";

    //Render
    public static final String getEntityTextureObf = "func_110775_a";
    public static final String getEntityTextureDeobf = "getEntityTexture";


    public static void obfWarning()
    {
        iChunUtil.LOGGER.warn("Error with some obfuscation!");
    }

    public static void detectObfuscation()
    {
        isObfuscated = true;
        try
        {
            Field[] fields = Class.forName("net.minecraft.item.ItemBlock").getDeclaredFields();
            for(Field f : fields)
            {
                f.setAccessible(true);
                if(f.getName().equalsIgnoreCase("block"))
                {
                    isObfuscated = false;
                    if(!iChunUtil.VERSION_OF_MC.equals(OBF_VERSION))
                    {
                        iChunUtil.LOGGER.warn("ObfHelper strings are not updated!");
                        throw new RuntimeException("Bad iChun! Update obfuscation strings!"); //this doesn't seem to crash the game o_O
                    }
                    return;
                }
            }

            BlockCompactPorkchop.class.getDeclaredMethod("func_149722_s"); // will only reach here if in dev env, setBlockUnbreakable
        }
        catch(NoSuchMethodException e)
        {
            throw new RuntimeException("You're running the deobf version of iChunUtil in an obfuscated environment! Don't do this!");
        }
        catch(Exception ignored)
        {
        }
    }

    public static boolean obfuscated()
    {
        return isObfuscated;
    }

    @SideOnly(Side.CLIENT)
    public static <T extends RenderLivingBase<V>, V extends EntityLivingBase> void invokePreRenderCallback(T rend, Class clz, V ent, float rendTick)
    {
        try
        {
            Method m = clz.getDeclaredMethod(ObfHelper.isObfuscated ? ObfHelper.preRenderCallbackObf : ObfHelper.preRenderCallbackDeobf, EntityLivingBase.class, float.class);
            m.setAccessible(true);
            m.invoke(rend, ent, rendTick);
        }
        catch(NoSuchMethodException e)
        {
            if(clz != RenderLivingBase.class)
            {
                invokePreRenderCallback(rend, clz.getSuperclass(), ent, rendTick);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @SideOnly(Side.CLIENT)
    public static <T extends Render<V>, V extends Entity> ResourceLocation getEntityTexture(T rend, Class clz, V ent)
    {
        try
        {
            Method m = clz.getDeclaredMethod(ObfHelper.isObfuscated ? ObfHelper.getEntityTextureObf : ObfHelper.getEntityTextureDeobf, Entity.class);
            m.setAccessible(true);
            return (ResourceLocation)m.invoke(rend, ent);
        }
        catch(NoSuchMethodException e)
        {
            if(clz != Render.class)
            {
                return getEntityTexture(rend, clz.getSuperclass(), ent);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return DefaultPlayerSkin.getDefaultSkinLegacy();
    }
}
