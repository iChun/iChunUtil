package us.ichun.mods.ichunutil.common;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import us.ichun.mods.ichunutil.common.core.CommonProxy;
import us.ichun.mods.ichunutil.common.core.config.Config;
import us.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import us.ichun.mods.ichunutil.common.core.config.IConfigUser;
import us.ichun.mods.ichunutil.common.core.network.ChannelHandler;
import us.ichun.mods.ichunutil.common.core.network.PacketHandler;
import us.ichun.mods.ichunutil.common.core.packet.PacketPatrons;
import us.ichun.mods.ichunutil.common.core.packet.PacketShowPatronReward;
import us.ichun.mods.ichunutil.common.core.updateChecker.ModVersionChecker;
import us.ichun.mods.ichunutil.common.core.updateChecker.ModVersionInfo;
import us.ichun.mods.ichunutil.common.core.updateChecker.PacketModsList;
import us.ichun.mods.ichunutil.common.core.util.ObfHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.EnumMap;

@Mod(modid = "iChunUtil", name = "iChunUtil",
        version = iChunUtil.version,
        dependencies = "required-after:Forge@[10.13.0.1186,)"
)
//hashmap.put(Type.SKIN, new MinecraftProfileTexture(String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", new Object[] { StringUtils.stripControlCodes(p_152790_1_.getName()) })));

//TODO check out the new KeyEvent with key releases in build 1211
public class iChunUtil
        implements IConfigUser
{
    //MC version, bumped up every MC update.
    public static final int versionMC = 5;
    public static final String versionOfMC = "1.8.0";

    public static final String version = versionMC + ".0.0";

    private static boolean hasPostLoad = false;

    private static Logger logger = LogManager.getLogger("iChunUtil");

    public static EnumMap<Side, FMLEmbeddedChannel> channels;

    public static Config config;

    public static boolean hasMorphMod;
    public static boolean isPatron;
    //Server's patron list. Client's is in TrailTicker
    public static ArrayList<String> patronList = new ArrayList<String>();

    @Instance("iChunUtil")
    public static iChunUtil instance;

    @SidedProxy(clientSide = "ClientProxy", serverSide = "CommonProxy")
    public static CommonProxy proxy;

    @Override
    public boolean onConfigChange(Config cfg, Property prop)
    {
        if(prop.getName().equalsIgnoreCase("showPatronReward"))
        {
            proxy.trailTicker.tellServerAsPatron = true;
        }
        return true;
    }

    @EventHandler
    public void preLoad(FMLPreInitializationEvent event)
    {
        ObfHelper.detectObfuscation();

        proxy.preInit();

        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);

        config = ConfigHandler.createConfig(event.getSuggestedConfigurationFile(), "ichunutil", "iChunUtil", logger, instance);

        config.setCurrentCategory("versionCheck", "ichun.config.versionCheck.name", "ichun.config.versionCheck.comment");
        config.createIntProperty("versionNotificationTypes", "ichun.config.versionNotificationTypes.name", "ichun.config.versionNotificationTypes.comment", true, false, 1, 0, 2);
        config.createIntProperty("versionNotificationFrequency", "ichun.config.versionNotificationFrequency.name", "ichun.config.versionNotificationFrequency.comment", true, false, 0, 0, 3);

        config.setCurrentCategory("versionSave", "ichun.config.versionSave.name", "ichun.config.versionSave.comment");
        String lastCheck = config.createStringProperty("lastCheck", "Last Check", "", false, false, "");
        config.createIntProperty("dayCheck", "Day Check", "", false, false, 0, 0, 35);

        String[] split = lastCheck.split(", ");

        for(String s : split)
        {
            String[] str = s.split(": ");
            if(str.length >= 2)
            {
                proxy.prevVerChecker.put(str[0], str[1]);
            }
        }

        ModVersionChecker.register_iChunMod(new ModVersionInfo("iChunUtil", versionOfMC, version, false));

        channels = ChannelHandler.getChannelHandlers("iChunUtil", PacketModsList.class, PacketPatrons.class, PacketShowPatronReward.class);
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
        for(Config cfg : ConfigHandler.configs)
        {
            cfg.setup();
        }
        if(FMLCommonHandler.instance().getEffectiveSide().isClient() && Config.configKeybind != null)
        {
            Config.configKeybind.save();
        }

        hasMorphMod = Loader.isModLoaded("Morph");

//        EntityHelperBase.getUUIDFromUsernames("pahimar");
//
//                ModVersionJsonGen.generate();
    }

    public static boolean getPostLoad()
    {
        return hasPostLoad;
    }

    //TODO iconRegister replacement for TextureStitchEvent.Pre ?

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientConnection(FMLNetworkEvent.ClientConnectedToServerEvent event)
    {
        if(isPatron)
        {
            proxy.trailTicker.tellServerAsPatron = true;
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        PacketHandler.sendToPlayer(channels, new PacketModsList(config.getInt("versionNotificationTypes"), FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().canSendCommands(event.player.getGameProfile())), event.player);
        PacketHandler.sendToPlayer(channels, new PacketPatrons(), event.player);
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
