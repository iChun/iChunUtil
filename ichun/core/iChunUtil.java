package ichun.core;

import ichun.client.core.TickHandlerClient;
import ichun.core.config.Config;
import ichun.core.config.ConfigHandler;

import java.util.logging.Level;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = "iChunUtil", name = "iChunUtil",
        version = iChunUtil.version,
        dependencies = "required-after:Forge@[9.10.0.810,)"
    )
public class iChunUtil
{
    public static final String version = "2.3.0";
    
    private static boolean hasPostLoad = false;

    @Instance("iChunUtil")
    public static iChunUtil instance;
    
	@SidedProxy(clientSide = "ichun.client.core.ClientProxy", serverSide = "ichun.core.CommonProxy")
	public static CommonProxy proxy;
    
    public static TickHandlerClient tickHandlerClient;
    
    @EventHandler
    public void preLoad(FMLPreInitializationEvent event)
    {
        LoggerHelper.init();
        ObfHelper.detectObfuscation();
        
        proxy.init();
        
        MinecraftForge.EVENT_BUS.register(this);
        
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
        	tickHandlerClient = new TickHandlerClient();
        	TickRegistry.registerTickHandler(tickHandlerClient, Side.CLIENT);
        }
    }
    
    @EventHandler
    public void postLoad(FMLPostInitializationEvent event)
    {
    	hasPostLoad = true;
    	for(Config cfg : ConfigHandler.configs)
    	{
    		cfg.setup();
    	}
    }
    
    public static boolean getPostLoad()
    {
    	return hasPostLoad;
    }
    
	@SideOnly(Side.CLIENT)
	@ForgeSubscribe
	public void onTextureStitched(TextureStitchEvent.Pre event)
	{
		if(event.map.textureType == 0)
		{
			tickHandlerClient.iconRegister = event.map;
		}
	}

    public static void console(String s, boolean warning)
    {
        StringBuilder sb = new StringBuilder();
        LoggerHelper.log(warning ? Level.WARNING : Level.INFO, sb.append("[").append(version).append("] ").append(s).toString());
    }

    public static void console(String s)
    {
        console(s, false);
    }

    public static void console(int i)
    {
        console((new Integer(i)).toString());
    }

    public static void console(boolean b)
    {
        console((new Boolean(b)).toString());
    }

    public static void console(float f)
    {
        console((new Float(f)).toString());
    }

    public static void console(double d)
    {
        console((new Double(d)).toString());
    }
}
