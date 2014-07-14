package ichun.common;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ichun.common.core.CommonProxy;
import ichun.common.core.config.Config;
import ichun.common.core.config.ConfigHandler;
import ichun.common.core.config.IConfigUser;
import ichun.common.core.network.ChannelHandler;
import ichun.common.core.network.PacketHandler;
import ichun.common.core.updateChecker.ModVersionChecker;
import ichun.common.core.updateChecker.ModVersionInfo;
import ichun.common.core.updateChecker.ModVersionJsonGen;
import ichun.common.core.updateChecker.PacketModsList;
import ichun.common.core.util.ObfHelper;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumMap;

@Mod(modid = "iChunUtil", name = "iChunUtil",
        version = iChunUtil.version,
        dependencies = "required-after:Forge@[10.12.2.1128,)"
)
public class iChunUtil
        implements IConfigUser
{
    //MC version, bumped up every MC update.
    public static final int versionMC = 3;
    public static final String version = versionMC + ".3.0";

    private static boolean hasPostLoad = false;

    private static Logger logger = LogManager.getLogger("iChunUtil");

    public static EnumMap<Side, FMLEmbeddedChannel> channels;

    public static Config config;

    @Instance("iChunUtil")
    public static iChunUtil instance;

    @SidedProxy(clientSide = "ichun.client.core.ClientProxy", serverSide = "ichun.common.core.CommonProxy")
    public static CommonProxy proxy;

    @Override
    public boolean onConfigChange(Config cfg, Property prop)
    {
        return true;
    }

    @EventHandler
    public void preLoad(FMLPreInitializationEvent event)
    {
        //        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        //        System.out.println("JSON!");
        //        System.out.println(gson.toJson(new TC1Json()));

        ObfHelper.detectObfuscation();

        proxy.init();

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

        ModVersionChecker.register_iChunMod(new ModVersionInfo("iChunUtil", "1.7", version, false));

        channels = ChannelHandler.getChannelHandlers("iChunUtil", PacketModsList.class);
    }

    @EventHandler
    public void load(FMLInitializationEvent event)
    {
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

//                ModVersionJsonGen.generate();
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
            proxy.tickHandlerClient.iconRegister = event.map;
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        PacketHandler.sendToPlayer(channels, new PacketModsList(config.getInt("versionNotificationTypes"), FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().func_152596_g(event.player.getGameProfile())), event.player);
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
