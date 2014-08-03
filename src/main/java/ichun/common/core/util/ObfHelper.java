package ichun.common.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import ichun.common.iChunUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

//TODO check usages of these fields. Some probably aren't used anymore.
public class ObfHelper
{
    //hashmap.put(Type.SKIN, new MinecraftProfileTexture(String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", new Object[] { StringUtils.stripControlCodes(p_152790_1_.getName()) })));
    public static boolean obfuscation;

    private static final String obfVersion = "1.7.10";

    public static final String[] resourceDomain			        = new String[] { "field_110626_a", "resourceDomain"		            , "a"}; //ResourceLocation
    public static final String[] resourcePath 			        = new String[] { "field_110625_b", "resourcePath" 			        , "b"}; //ResourceLocation
    public static final String[] gameProfile                    = new String[] { "field_146106_i", "gameProfile"                    , "i"}; //EntityPlayer

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
        return AbstractClientPlayer.locationStevePng;
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
        return AbstractClientPlayer.locationStevePng;
    }
}
