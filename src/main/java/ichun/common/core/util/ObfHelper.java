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
    public static boolean obfuscation;

    private static final String obfVersion = "1.7.0";

    public static final String[] equippedProgress 				= new String[] { "field_78454_c", "equippedProgress" 				, "f"}; //ItemRenderer
    public static final String[] prevEquippedProgress 			= new String[] { "field_78451_d", "prevEquippedProgress" 			, "g"}; //ItemRenderer
    public static final String[] itemToRender 					= new String[] { "field_78453_b", "itemToRender" 					, "e"}; //ItemRenderer
    public static final String[] equippedItemSlot 				= new String[] { "field_78450_g", "equippedItemSlot" 			    , "i"}; //ItemRenderer

    public static final String[] lightningState					= new String[] { "field_70262_b", "lightningState"					, "b"}; //EntityLightningBolt

    public static final String[] explosionRadius				= new String[] { "field_82226_g", "explosionRadius"				    ,"bs"}; //EntityCreeper

    public static final String[] cameraZoom						= new String[] { "field_78503_V", "cameraZoom"						,"af"}; //EntityRenderer
    public static final String[] fogColorRed					= new String[] { "field_78518_n", "fogColorRed"					    , "m"}; //EntityRenderer
    public static final String[] fogColorGreen					= new String[] { "field_78519_o", "fogColorGreen"					, "n"}; //EntityRenderer
    public static final String[] fogColorBlue					= new String[] { "field_78533_p", "fogColorBlue"					, "o"}; //EntityRenderer

    public static final String[] itemHealth						= new String[] { "field_70291_e", "health"							, "e"}; //EntityItem

    public static final String[] arrowInGround					= new String[] { "field_70254_i", "inGround"						, "i"}; //EntityArrow

    public static final String[] showNameTime					= new String[] { "field_92017_k", "remainingHighlightTicks"		    , "q"}; //GuiIngame
    
    public static final String[] defaultResourcePacks 			= new String[] { "field_110449_ao", "defaultResourcePacks"		    ,"ap"}; //Minecraft
    public static final String[] refreshTexturePacksScheduled 	= new String[] { "field_71468_ad", "refreshTexturePacksScheduled"	,"ae"}; //Minecraft
    
	public static final String[] textureOffsetX 				= new String[] { "field_78803_o", "textureOffsetX" 				    , "r"}; //ModelRenderer
	public static final String[] textureOffsetY 				= new String[] { "field_78813_p", "textureOffsetY" 				    , "s"}; //ModelRenderer
	public static final String[] compiled 						= new String[] { "field_78812_q", "compiled"	 					, "t"}; //ModelRenderer

	public static final String[] quadList 						= new String[] { "field_78254_i", "quadList"	 					, "i"}; //ModelBox
    
    public static final String[] mainModel 						= new String[] { "field_77045_g", "mainModel" 						, "i"}; //RendererLivingEntity

    public static final String[] field_82423_g 					= new String[] { "field_82423_g" 									, "g"}; //RenderBiped
    public static final String[] field_82425_h 					= new String[] { "field_82425_h" 									, "h"}; //RenderBiped

    public static final String[] modelBipedMain			        = new String[] { "field_77109_a", "modelBipedMain"			        , "f"}; //RenderPlayer
    public static final String[] modelArmorChestplate 			= new String[] { "field_77108_b", "modelArmorChestplate" 			, "g"}; //RenderPlayer

    public static final String[] resourceDomain			        = new String[] { "field_110626_a", "resourceDomain"		            , "a"}; //ResourceLocation
    public static final String[] resourcePath 			        = new String[] { "field_110625_b", "resourcePath" 			        , "b"}; //ResourceLocation

    public static final String[] timeSinceIgnited				= new String[] { "field_70833_d", "timeSinceIgnited"				,"bq"}; //EntityCreeper
    public static final String[] fuseTime						= new String[] { "field_82225_f", "fuseTime"						,"br"}; //EntityCreeper

    public static final String[] isImmuneToFire			        = new String[] { "field_70178_ae", "isImmuneToFire"	        	    ,"af"}; //Entity

    public static final String[] isJumping 		        		= new String[] { "field_70703_bu", "isJumping" 		        	    ,"bd"}; //EntityLivingBase

    public static final String[] shadowSize		        		= new String[] { "field_76989_e", "shadowSize"			        	, "d"}; //Render

    public static final String[] tagMap			        		= new String[] { "field_74784_a", "tagMap"				        	, "c"}; //NBTTagCompound

    //EntityLivingBase
    public static final String jumpObf		= "func_70664_aZ";
    public static final String jumpDeobf	= "jump";

    //EntityLivingBase
    public static final String getHurtSoundObf = "func_70621_aR";
    public static final String getHurtSoundDeobf = "getHurtSound";

    //EntityLivingBase
    public static final String getDeathSoundObf = "func_70673_aS";
    public static final String getDeathSoundDeobf = "getDeathSound";

    //RenderLivingEntity
	public static final String preRenderCallbackObf = "func_77041_b";
	public static final String preRenderCallbackDeobf = "preRenderCallback";
    
    //EntityRenderer
    public static final String renderHandObf = "func_78476_b";
    public static final String renderHandDeobf = "renderHand";
    
    //Entity
	public static final String setSizeObf = "func_70105_a";
	public static final String setSizeDeobf = "setSize";

	//RenderPlayer
	public static final String renderLivingLabelObf = "func_96449_a";

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
    
	public static void forceSetSize(Class clz, Entity ent, float width, float height)
	{
		try
		{
			Method m = clz.getDeclaredMethod(ObfHelper.obfuscation ? ObfHelper.setSizeObf : ObfHelper.setSizeDeobf, float.class, float.class);
			m.setAccessible(true);
			m.invoke(ent, width, height);
		}
		catch(NoSuchMethodException e)
		{
			if(clz != Entity.class)
			{
				forceSetSize(clz.getSuperclass(), ent, width, height);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
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

    public static void invokeRenderLivingLabel(RenderPlayer rend, EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, String par8Str, float par9, double par10)
    {
        try
        {
            Method m = RenderPlayer.class.getDeclaredMethod(ObfHelper.renderLivingLabelObf, EntityLivingBase.class, double.class, double.class, double.class, String.class, float.class, double.class);
            m.setAccessible(true);
            m.invoke(rend, par1EntityLivingBase, par2, par4, par6, par8Str, par9, par10);
        }
        catch(NoSuchMethodException e)
        {
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
