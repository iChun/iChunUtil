package ichun.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class ObfHelper
{
    public static boolean obfuscation;

    private static final String obfVersion = "1.6.0";

    public static final String[] equippedProgress 				= new String[] { "g", "field_78454_c", "equippedProgress" 				}; //ItemRenderer
    public static final String[] prevEquippedProgress 			= new String[] { "h", "field_78451_d", "prevEquippedProgress" 			}; //ItemRenderer
    public static final String[] itemToRender 					= new String[] { "f", "field_78453_b", "itemToRender" 					}; //ItemRenderer
    public static final String[] equippedItemSlot 				= new String[] { "j", "field_78450_g", "equippedItemSlot" 				}; //ItemRenderer

    public static final String[] lightningState					= new String[] { "b", "field_70262_b", "lightningState"					}; //EntityLightningBolt

    public static final String[] explosionRadius				= new String[] { "bs", "field_82226_g", "explosionRadius"				}; //EntityCreeper

    public static final String[] cameraZoom						= new String[] { "Y", "field_78503_V", "cameraZoom"						}; //EntityRenderer

    public static final String[] itemHealth						= new String[] { "d", "field_70291_e", "health"							}; //EntityItem

    public static final String[] arrowInGround					= new String[] { "i", "field_70254_i", "inGround"						}; //EntityArrow

    public static final String[] showNameTime					= new String[] { "q", "field_92017_k", "remainingHighlightTicks"		}; //GuiIngame
    
    public static final String[] defaultResourcePacks 			= new String[] { "aq", "field_110449_ao", "defaultResourcePacks"		}; //Minecraft
    public static final String[] refreshTexturePacksScheduled 	= new String[] { "ag", "field_71468_ad", "refreshTexturePacksScheduled"	}; //Minecraft
    
//    public static final String[] bipedHead 						= new String[] { "c", "field_78116_c", "bipedHead" 						}; //ModelBiped
//    public static final String[] bipedHeadwear 					= new String[] { "d", "field_78114_d", "bipedHeadwear" 					}; //ModelBiped
//    public static final String[] bipedRightArm 					= new String[] { "f", "field_78112_f", "bipedRightArm" 					}; //ModelBiped
//    public static final String[] bipedLeftArm 					= new String[] { "g", "field_78113_g", "bipedLeftArm" 					}; //ModelBiped
    
    public static final String[] mainModel 						= new String[] { "i", "field_77045_g", "mainModel" 						}; //RendererLivingEntity
    public static final String[] field_82423_g 					= new String[] { "g", "field_82423_g" 									}; //RenderBiped
    public static final String[] field_82425_h 					= new String[] { "h", "field_82425_h" 									}; //RenderBiped
    public static final String[] modelArmorChestplate 			= new String[] { "g", "field_77108_b", "modelArmorChestplate" 			}; //RenderPlayer
    
    public static final String[] timeSinceIgnited				= new String[] { "bq", "field_70833_d", "timeSinceIgnited"				}; //EntityCreeper
    public static final String[] fuseTime						= new String[] { "br", "field_82225_f", "fuseTime"						}; //EntityCreeper

    //EntityLivingBase
    public static final String jumpObf		= "func_70664_aZ";
    public static final String jumpDeobf	= "jump";

    //EntityLivingBase
    public static final String getDeathSoundObf = "func_70673_aS";
    public static final String getDeathSoundDeobf = "getDeathSound";
    
    //EntityRenderer
    public static final String renderHandObf = "func_78476_b";
    public static final String renderHandDeobf = "renderHand";
    
    //Entity
	public static final String setSizeObf = "func_70105_a";
	public static final String setSizeDeobf = "setSize";

	//EntityLivingbase
	public static final String getEntityTextureObf = "func_110775_a";

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
	
	public static ResourceLocation invokeGetEntityTexture(Render rend, Class clz, EntityLivingBase ent)
	{
		try
		{
			Method m = clz.getDeclaredMethod(ObfHelper.getEntityTextureObf, Entity.class);
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
