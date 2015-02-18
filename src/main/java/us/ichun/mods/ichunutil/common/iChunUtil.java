package us.ichun.mods.ichunutil.common;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.ichun.mods.ichunutil.common.core.CommonProxy;
import us.ichun.mods.ichunutil.common.core.config.ConfigBase;
import us.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import us.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import us.ichun.mods.ichunutil.common.core.config.annotations.IntBool;
import us.ichun.mods.ichunutil.common.core.config.annotations.IntMinMax;
import us.ichun.mods.ichunutil.common.core.network.PacketChannel;
import us.ichun.mods.ichunutil.common.core.network.PacketExecuter;
import us.ichun.mods.ichunutil.common.core.updateChecker.ModVersionChecker;
import us.ichun.mods.ichunutil.common.core.updateChecker.ModVersionInfo;
import us.ichun.mods.ichunutil.common.core.util.ObfHelper;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

@Mod(modid = "iChunUtil", name = "iChunUtil",
        version = iChunUtil.version,
        dependencies = "required-after:Forge@[1.8-11.14.0.1281-1.8,)"
)
//hashmap.put(Type.SKIN, new MinecraftProfileTexture(String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", new Object[] { StringUtils.stripControlCodes(p_152790_1_.getName()) }), null));

//TODO easter egg for AFday?
public class iChunUtil
{
    //MC version, bumped up every MC update.
    public static final int versionMC = 5;
    public static final String versionOfMC = "1.8.0";

    public static final String version = versionMC + ".0.0";

    private static boolean hasPostLoad = false;

    private static Logger logger = LogManager.getLogger("iChunUtil");

    public static PacketChannel channel;

    public static Config config;

    public static boolean hasMorphMod;
    public static boolean isPatron;
    //Server's patron list. Client's is in TrailTicker
    public static ArrayList<String> patronList = new ArrayList<String>();

    @Instance("iChunUtil")
    public static iChunUtil instance;

    @SidedProxy(clientSide = "us.ichun.mods.ichunutil.client.core.ClientProxy", serverSide = "us.ichun.mods.ichunutil.common.core.CommonProxy")
    public static CommonProxy proxy;

    public class Config extends ConfigBase
    {
        @ConfigProp(category = "versionCheck")
        @IntMinMax(min = 0, max = 2)
        public int versionNotificationTypes = 1;

        @ConfigProp(category = "versionCheck")
        @IntMinMax(min = 0, max = 3)
        public int versionNotificationFrequency = 0;

        @ConfigProp(category = "versionSave", comment = "", nameOverride = "Last Check")
        public String lastCheck = "";

        @ConfigProp(category = "versionSave", comment = "", nameOverride = "Day Check")
        @IntMinMax(min = 0, max = 35)
        public int dayCheck = 0;

        @ConfigProp(category = "patreon", hidden = true)
        @IntBool
        public int showPatronReward = 1;

        public Config(File file)
        {
            super(file);
        }

        @Override
        public String getModId()
        {
            return "ichunutil";
        }

        @Override
        public String getModName()
        {
            return "iChunUtil";
        }

        @Override
        public void onConfigChange(Field field, Object original)
        {
            if(field.getName().equals("showPatronReward"))
            {
                iChunUtil.proxy.trailTicker.tellServerAsPatron = true;
            }
        }
    }

    @EventHandler
    public void preLoad(FMLPreInitializationEvent event)
    {
        ObfHelper.detectObfuscation();

        proxy.preInit();

        FMLCommonHandler.instance().bus().register(new PacketExecuter());

        us.ichun.mods.ichunutil.common.core.EventHandler eventHandler = new us.ichun.mods.ichunutil.common.core.EventHandler();
        FMLCommonHandler.instance().bus().register(eventHandler);
        MinecraftForge.EVENT_BUS.register(eventHandler);

        config = (Config)ConfigHandler.registerConfig(new Config(event.getSuggestedConfigurationFile()));

        String[] split = config.lastCheck.split(", ");

        for(String s : split)
        {
            String[] str = s.split(": ");
            if(str.length >= 2)
            {
                proxy.prevVerChecker.put(str[0], str[1]);
            }
        }

        ModVersionChecker.register_iChunMod(new ModVersionInfo("iChunUtil", versionOfMC, version, false));
    }

    @EventHandler
    public void load(FMLInitializationEvent event)
    {
        proxy.init();

        ModVersionChecker.init();
    }

    @EventHandler
    public void postLoad(FMLPostInitializationEvent event)
    {
        hasPostLoad = true;
        for(ConfigBase cfg : ConfigHandler.configs)
        {
            cfg.setup();
        }
        if(FMLCommonHandler.instance().getEffectiveSide().isClient() && ConfigHandler.configKeybind != null)
        {
            ConfigHandler.configKeybind.save();
        }

        hasMorphMod = Loader.isModLoaded("Morph");

//                us.ichun.mods.ichunutil.common.core.EntityHelperBase.getUUIDFromUsernames("pahimar");
        //
        //                ModVersionJsonGen.generate();
    }

    public static boolean getPostLoad()
    {
        return hasPostLoad;
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
}
