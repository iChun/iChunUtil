package me.ichun.mods.ichunutil.common;

import me.ichun.mods.ichunutil.client.core.event.EventHandlerClient;
import me.ichun.mods.ichunutil.common.core.Logger;
import me.ichun.mods.ichunutil.common.core.ProxyCommon;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntBool;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntMinMax;
import me.ichun.mods.ichunutil.common.core.event.EventHandlerServer;
import me.ichun.mods.ichunutil.common.core.network.PacketChannel;
import me.ichun.mods.ichunutil.common.core.util.ObfHelper;
import me.ichun.mods.ichunutil.common.module.update.UpdateChecker;
import me.ichun.mods.ichunutil.common.module.worldportals.common.WorldPortals;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

@Mod(modid = iChunUtil.MOD_ID, name = iChunUtil.MOD_NAME,
        version = iChunUtil.VERSION,
        guiFactory = iChunUtil.GUI_CONFIG_FACTORY,
        acceptedMinecraftVersions = iChunUtil.MC_VERSION_RANGE,
        dependencies = "required-after:forge@[" + iChunUtil.REQ_FORGE_MAJOR + "." + iChunUtil.REQ_FORGE_MINOR + "." + iChunUtil.REQ_FORGE_REVISION + "." + iChunUtil.REQ_FORGE_BUILD + "]; "
)
//hashmap.put(Type.SKIN, new MinecraftProfileTexture(String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", new Object[] { StringUtils.stripControlCodes(p_152790_1_.getName()) }), null));
public class iChunUtil
{
    //Stuff to bump every updateWorldPortal
    public static final String VERSION_OF_MC = "1.12.2";
    public static final String MC_VERSION_RANGE = "[1.12,1.13)";
    public static final int VERSION_MAJOR = 7;
    public static final int VERSION_MINOR = 2;
    public static final String VERSION = VERSION_MAJOR + "." + VERSION_MINOR + ".0";

    public static final String MOD_NAME = "iChunUtil";
    public static final String MOD_ID = "ichunutil";

    public static final int REQ_FORGE_MAJOR = 14;
    public static final int REQ_FORGE_MINOR = 23;
    public static final int REQ_FORGE_REVISION = 2;
    public static final int REQ_FORGE_BUILD = 2623;

    public static final Logger LOGGER = Logger.createLogger(MOD_NAME);

    public static final String GUI_CONFIG_FACTORY = "me.ichun.mods.ichunutil.common.core.config.GenericModGuiFactory";

    @Mod.Instance(MOD_ID)
    public static iChunUtil instance;

    @SidedProxy(clientSide = "me.ichun.mods.ichunutil.client.core.ProxyClient", serverSide = "me.ichun.mods.ichunutil.common.core.ProxyCommon")
    public static ProxyCommon proxy;

    //Mod stuffs
    public static Config config;

    public static PacketChannel channel;

    public static EventHandlerServer eventHandlerServer;
    public static EventHandlerClient eventHandlerClient;

    public static Block blockCompactPorkchop;
    public static List<ItemStack> oreDictBlockCompactRawPorkchop;

    private static boolean hasPostInit;
    private static boolean hasMorphMod;

    public static boolean userIsPatron;

    public class Config extends ConfigBase
    {
        @ConfigProp(category = "clientOnly", side = Side.CLIENT, changeable = false)
        @IntBool
        public int enableStencils = 1;

        //Modules
        //Compact Porkchop module
        @ConfigProp(module = "compactPorkchop", category = "block", useSession = true)
        @IntBool
        public int enableCompactPorkchop = 1;

        //EULA module
        @ConfigProp(module = "eula")
        public String eulaAcknowledged = "";

        //Patreon module
        @ConfigProp(module = "patreon", side = Side.CLIENT, hidden = true)
        @IntBool
        public int showPatronReward = 1;

        @ConfigProp(module = "patreon", side = Side.CLIENT, hidden = true)
        @IntMinMax(min = 1, max = 6)
        public int patronRewardType = 1;

        //Update checker module
        @ConfigProp(module = "versionCheck")
        @IntMinMax(min = 0, max = 2)
        public int versionNotificationTypes = 0;

        @ConfigProp(module = "versionCheck", side = Side.CLIENT)
        @IntMinMax(min = 0, max = 2)
        public int versionNotificationFrequency = 2;

        @ConfigProp(module = "versionCheck", side = Side.CLIENT)
        @IntMinMax(min = 0, max = 35)
        public int versionSave = 0;

        //Head model tracking module
        @ConfigProp(module = "headTracking", side = Side.CLIENT, hidden = true)
        @IntMinMax(min = 0, max = 2)
        public int aggressiveHeadTracking = 0;

        @ConfigProp(module = "headTracking", side = Side.CLIENT, hidden = true)
        @IntBool
        public int horseEasterEgg = 1;

        //World Portals module
        @ConfigProp(module = "worldPortals", side = Side.CLIENT, hidden = true)
        @IntMinMax(min = 0, max = 10)
        public int maxRecursion = 2;

        @ConfigProp(module = "worldPortals", side = Side.CLIENT, hidden = true)
        @IntMinMax(min = 1, max = 0xff)
        public int stencilValue = 0x2f;

        @ConfigProp(module = "worldPortals", side = Side.CLIENT, hidden = true)
        @IntMinMax(min = 0, max = 16)
        public int renderDistanceChunks = 0;

        @ConfigProp(module = "worldPortals", side = Side.CLIENT, hidden = true)
        @IntMinMax(min = 1, max = 100)
        public int maxRendersPerTick = 10;
        //End Modules

        public Config(File file)
        {
            super(file);
        }

        @Override
        public String getModId()
        {
            return iChunUtil.MOD_ID;
        }

        @Override
        public String getModName()
        {
            return iChunUtil.MOD_NAME;
        }

        @Override
        public void onConfigChange(Field field, Object original) //Nested int array and keybind original is the new var, no ori cause lazy
        {
            if(field.getName().equals("showPatronReward") || field.getName().equals("patronRewardType"))
            {
                iChunUtil.eventHandlerClient.patronUpdateServerAsPatron = true;
            }
        }
    }

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event)
    {
        ObfHelper.detectObfuscation();

        config = ConfigHandler.registerConfig(new Config(event.getSuggestedConfigurationFile()));

        proxy.preInit();

        UpdateChecker.registerMod(new UpdateChecker.ModVersionInfo(MOD_NAME, iChunUtil.VERSION_OF_MC, VERSION, false));
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event)
    {
        proxy.init();
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event)
    {
        hasPostInit = true;
        hasMorphMod = Loader.isModLoaded("morph");

        proxy.postInit();

        //                me.ichun.mods.ichunutil.common.module.update.UpdateVersionGen.generate();
        //        System.out.println(EntityHelper.getGameProfile("pahimar").getId());
    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event)
    {
        UpdateChecker.serverStarted();
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event)
    {
        eventHandlerServer.shuttingDownServer();
        WorldPortals.onServerStopping();
    }

    @Mod.EventHandler
    public void onIMCMessage(FMLInterModComms.IMCEvent event)
    {
        for(FMLInterModComms.IMCMessage message : event.getMessages())
        {
            if(message.key.equalsIgnoreCase("update") && message.isStringMessage())
            {
                String[] split = message.getStringValue().split(">");
                if(split.length != 4)
                {
                    LOGGER.info("Invalid update checker string " + message.getStringValue() + ". Invalid argument count!");
                }
                else //Mod name, MC version, mod version, clientSideOnly
                {
                    UpdateChecker.registerMod(new UpdateChecker.ModVersionInfo(split[0], split[1], split[2], split[3].equalsIgnoreCase("true")));
                }
            }
        }
    }

    public static boolean hasPostInit()
    {
        return hasPostInit;
    }

    public static boolean hasMorphMod()
    {
        return hasMorphMod;
    }
}
