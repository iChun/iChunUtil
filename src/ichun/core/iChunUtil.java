package ichun.core;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ichun.client.core.TickHandlerClient;
import ichun.core.config.Config;
import ichun.core.config.ConfigHandler;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "iChunUtil", name = "iChunUtil",
        version = iChunUtil.version,
        dependencies = "required-after:Forge@[10.12.0.1041,)"
    )
public class iChunUtil
{
	//MC version, bumped up every MC update.
	public static final int versionMC = 3;
    public static final String version = versionMC + ".5.0";
    
    private static boolean hasPostLoad = false;

    private static Logger logger = LogManager.getLogger("iChunUtil");

    @Instance("iChunUtil")
    public static iChunUtil instance;
    
	@SidedProxy(clientSide = "ichun.client.core.ClientProxy", serverSide = "ichun.core.CommonProxy")
	public static CommonProxy proxy;
    
    public static TickHandlerClient tickHandlerClient;
    
    @EventHandler
    public void preLoad(FMLPreInitializationEvent event)
    {
        ObfHelper.detectObfuscation();
        
        proxy.init();
        
        MinecraftForge.EVENT_BUS.register(this);
        
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
        	tickHandlerClient = new TickHandlerClient();
        	FMLCommonHandler.instance().bus().register(tickHandlerClient);
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
	@SubscribeEvent
	public void onTextureStitched(TextureStitchEvent.Pre event)
	{
		if(event.map.getTextureType() == 0)
		{
			tickHandlerClient.iconRegister = event.map;
		}
	}

    public static void console(String s, boolean warning)
    {
        StringBuilder sb = new StringBuilder();
        logger.log(warning ? Level.WARN : Level.INFO, sb.append("[").append(version).append("] ").append(s).toString());
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
