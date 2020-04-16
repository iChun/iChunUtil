package me.ichun.mods.ichunutil.common.util;

import cpw.mods.modlauncher.api.INameMappingService;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

public class ObfHelper
{
    private static final String OBF_VERSION = "1.15.2";

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
}
