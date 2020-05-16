package me.ichun.mods.ichunutil.common.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import cpw.mods.modlauncher.api.INameMappingService;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.loading.FMLLoader;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

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

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public static <T extends EntityRenderer<?>, V extends Entity> ResourceLocation getEntityTexture(T rend, Class clz, V ent)
    {
        try
        {
            Method m = clz.getDeclaredMethod(ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, "func_110775_a"), Entity.class);
            m.setAccessible(true);
            return (ResourceLocation)m.invoke(rend, ent);
        }
        catch(NoSuchMethodException e)
        {
            if(clz != EntityRenderer.class)
            {
                return getEntityTexture(rend, clz.getSuperclass(), ent);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public static <T extends LivingRenderer<V, ?>, V extends LivingEntity> void invokePreRenderCallback(T rend, Class clz, V ent, MatrixStack stack, float rendTick)
    {
        try
        {
            Method m = clz.getDeclaredMethod(ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, "func_225620_a_"), LivingEntity.class, MatrixStack.class, float.class);
            m.setAccessible(true);
            m.invoke(rend, ent, stack, rendTick);
        }
        catch(NoSuchMethodException e)
        {
            if(clz != LivingRenderer.class)
            {
                invokePreRenderCallback(rend, clz.getSuperclass(), ent, stack, rendTick);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
