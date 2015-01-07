package us.ichun.mods.ichunutil.common.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import us.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

//TODO check usages of these fields. Some probably aren't used anymore.
public class ObfHelper
{
    public static boolean obfuscation;

    private static final String obfVersion = "1.8";

    public static final String[] mainModel = new String[] { "field_77045_g", "mainModel" };

    //EntityLivingBase
    public static final String getHurtSoundObf = "func_70621_aR";
    public static final String getHurtSoundDeobf = "getHurtSound";

    //EntityLivingBase
    public static final String getDeathSoundObf = "func_70673_aS";
    public static final String getDeathSoundDeobf = "getDeathSound";

    //RenderLivingEntity
	public static final String preRenderCallbackObf = "func_77041_b";
	public static final String preRenderCallbackDeobf = "preRenderCallback";
    
    //Render
    public static final String getEntityTextureObf = "func_110775_a";
    public static final String getEntityTextureDeobf = "getEntityTexture";

    public static void obfWarning()
    {
        iChunUtil.console("Forgot to update obfuscation!", true);
    }

    public static void detectObfuscation()
    {
        obfuscation = true;
        try
        {
            Field[] fields = Class.forName("net.minecraft.world.World").getDeclaredFields();
            for(Field f : fields)
            {
            	f.setAccessible(true);
            	if(f.getName().equalsIgnoreCase("loadedEntityList"))
            	{
            		obfuscation = false;
            		return;
            	}
            }
        }
        catch (Exception e)
        {
        }
    }
    
	public static void invokePreRenderCallback(Render rend, Class clz, Entity ent, float rendTick)
	{
		if(!(rend instanceof RendererLivingEntity) || !(ent instanceof EntityLivingBase))
		{
			return;
		}
		try
		{
			Method m = clz.getDeclaredMethod(ObfHelper.obfuscation ? ObfHelper.preRenderCallbackObf : ObfHelper.preRenderCallbackDeobf, EntityLivingBase.class, float.class);
			m.setAccessible(true);
			m.invoke(rend, ent, rendTick);
		}
		catch(NoSuchMethodException e)
		{
			if(clz != RendererLivingEntity.class)
			{
				invokePreRenderCallback(rend, clz.getSuperclass(), ent, rendTick);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

    public static ResourceLocation invokeGetEntityTexture(Render rend, Class clz, EntityLivingBase ent)
    {
        ResourceLocation loc = getEntTexture(rend, clz, ent);
        if(loc != null)
        {
            return loc;
        }
        return DefaultPlayerSkin.getDefaultSkinLegacy();
    }

    private static ResourceLocation getEntTexture(Render rend, Class clz, EntityLivingBase ent)
    {
        try
        {
            Method m = clz.getDeclaredMethod(ObfHelper.obfuscation ? ObfHelper.getEntityTextureObf : ObfHelper.getEntityTextureDeobf, Entity.class);
            m.setAccessible(true);
            return (ResourceLocation)m.invoke(rend, ent);
        }
        catch(NoSuchMethodException e)
        {
            if(clz != RendererLivingEntity.class)
            {
                return invokeGetEntityTexture(rend, clz.getSuperclass(), ent);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return DefaultPlayerSkin.getDefaultSkinLegacy();
    }
}
