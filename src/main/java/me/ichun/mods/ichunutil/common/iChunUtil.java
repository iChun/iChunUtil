package me.ichun.mods.ichunutil.common;


import me.ichun.mods.ichunutil.client.core.event.EventHandlerClient;
import me.ichun.mods.ichunutil.common.core.Logger;
import me.ichun.mods.ichunutil.common.core.ProxyCommon;
import me.ichun.mods.ichunutil.common.core.event.EventHandlerServer;
import me.ichun.mods.ichunutil.common.core.util.ObfHelper;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = iChunUtil.MOD_NAME, name = iChunUtil.MOD_NAME,
        version = iChunUtil.VERSION,
//        guiFactory = "us.ichun.mods.ichunutil.common.core.config.GenericModGuiFactory",
        dependencies = "required-after:Forge@[" + iChunUtil.REQ_FORGE_MAJOR + "." + iChunUtil.REQ_FORGE_MINOR + "." + iChunUtil.REQ_FORGE_REVISION + "." + iChunUtil.REQ_FORGE_BUILD + ",99999." + (iChunUtil.REQ_FORGE_MINOR + 1) + ".0.0)"
)
//hashmap.put(Type.SKIN, new MinecraftProfileTexture(String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", new Object[] { StringUtils.stripControlCodes(p_152790_1_.getName()) }), null));
public class iChunUtil
{
    //Stuff to bump every update
    public static final String VERSION_OF_MC = "1.8.8";
    public static final int VERSION_MAJOR = 6;
    public static final String VERSION = VERSION_MAJOR + ".0.0";

    public static final String MOD_NAME = "iChunUtil";

    public static final int REQ_FORGE_MAJOR = 11;
    public static final int REQ_FORGE_MINOR = ForgeVersion.minorVersion;
    public static final int REQ_FORGE_REVISION = 0;
    public static final int REQ_FORGE_BUILD = 1654;

    public static final Logger LOGGER = Logger.createLogger(MOD_NAME);

    @Mod.Instance(MOD_NAME)
    public static iChunUtil instance;

    @SidedProxy(clientSide = "me.ichun.mods.ichunutil.client.core.ProxyClient", serverSide = "me.ichun.mods.ichunutil.common.core.ProxyCommon")
    public static ProxyCommon proxy;

    //Mod stuffs

    public static EventHandlerServer eventHandlerServer;
    public static EventHandlerClient eventHandlerClient;

    private static boolean hasPostInit;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event)
    {
        ObfHelper.detectObfuscation();

        proxy.preInit();
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

        proxy.postInit();
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event)
    {
    }

    public boolean hasPostInit()
    {
        return hasPostInit;
    }
}
