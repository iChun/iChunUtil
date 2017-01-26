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
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

@Mod(modid = iChunUtil.MOD_ID, name = iChunUtil.MOD_NAME,
        version = iChunUtil.VERSION,
        guiFactory = "me.ichun.mods.ichunutil.common.core.config.GenericModGuiFactory",
        dependencies = "required-after:Forge@[" + iChunUtil.REQ_FORGE_MAJOR + "." + iChunUtil.REQ_FORGE_MINOR + "." + iChunUtil.REQ_FORGE_REVISION + "." + iChunUtil.REQ_FORGE_BUILD + ",99999." + (iChunUtil.REQ_FORGE_MINOR + 1) + ".0.0)",
        acceptableRemoteVersions = "[" + iChunUtil.VERSION_MAJOR + "." + iChunUtil.VERSION_MINOR + ".0," + iChunUtil.VERSION_MAJOR + "." + (iChunUtil.VERSION_MINOR + 1) + ".0)",
        acceptedMinecraftVersions = "[1.9.4,1.10.2]"
)
//hashmap.put(Type.SKIN, new MinecraftProfileTexture(String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", new Object[] { StringUtils.stripControlCodes(p_152790_1_.getName()) }), null));
public class iChunUtil
{
    //Stuff to bump every update
    public static final String VERSION_OF_MC = "1.10.2";
    public static final int VERSION_MAJOR = 6;
    public static final int VERSION_MINOR = 2;
    public static final String VERSION = VERSION_MAJOR + "." + VERSION_MINOR + ".0";

    public static final String MOD_NAME = "iChunUtil";
    public static final String MOD_ID = "ichunutil";

    public static final int REQ_FORGE_MAJOR = 12;
    public static final int REQ_FORGE_MINOR = ForgeVersion.minorVersion;
    public static final int REQ_FORGE_REVISION = 2;
    public static final int REQ_FORGE_BUILD = 2151;

    public static final Logger LOGGER = Logger.createLogger(MOD_NAME);

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
    private static boolean isCompactPorkchopRecipeAdded;

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
        @ConfigProp(category = "block", useSession = true, module = "compactPorkchop")
        @IntBool
        public int enableCompactPorkchop = 1;

        //EULA module
        @ConfigProp(module = "eula")
        public String eulaAcknowledged = "";

        //Patreon module
        @ConfigProp(side = Side.CLIENT, module = "patreon", hidden = true)
        @IntBool
        public int showPatronReward = 1;

        @ConfigProp(side = Side.CLIENT, module = "patreon", hidden = true)
        @IntMinMax(min = 1, max = 6)
        public int patronRewardType = 1;

        //Update checker module
        @ConfigProp(module = "versionCheck")
        @IntMinMax(min = 0, max = 2)
        public int versionNotificationTypes = 0;

        @ConfigProp(side = Side.CLIENT, module = "versionCheck")
        @IntMinMax(min = 0, max = 2)
        public int versionNotificationFrequency = 2;

        @ConfigProp(side = Side.CLIENT, module = "versionCheck")
        @IntMinMax(min = 0, max = 35)
        public int versionSave = 0;

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
        public void onReceiveSession()
        {
            List<ItemStack> compactPorkchops = oreDictBlockCompactRawPorkchop;
            if(compactPorkchops.size() == 1 && compactPorkchops.get(0).getItem() != null && Block.getBlockFromItem(compactPorkchops.get(0).getItem()) == blockCompactPorkchop) //Only handle the recipe if it's the only oredict entry for the block.
            {
                Minecraft.getMinecraft().addScheduledTask(iChunUtil::setupCompactPorkchopRecipe);
            }
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
        hasMorphMod = Loader.isModLoaded("Morph");

        proxy.postInit();

        //        UpdateVersionGen.generate();
        //        System.out.println(EntityHelper.getGameProfile("pahimar").getId());
    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event)
    {
        UpdateChecker.serverStarted();
        setupCompactPorkchopRecipe();
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event)
    {
        eventHandlerServer.shuttingDownServer();
    }

    public static void setupCompactPorkchopRecipe()
    {
        if(isCompactPorkchopRecipeAdded != (config.enableCompactPorkchop == 1))
        {
            if(isCompactPorkchopRecipeAdded) //remove the recipe
            {
                List recipes = CraftingManager.getInstance().getRecipeList();
                for(int i = recipes.size() - 1; i >= 0; i--)
                {
                    if(recipes.get(i) instanceof ShapedRecipes)
                    {
                        ShapedRecipes recipe = (ShapedRecipes)recipes.get(i);
                        if(recipe.getRecipeOutput().isItemEqual(new ItemStack(blockCompactPorkchop)))
                        {
                            recipes.remove(i);
                        }
                    }
                }
            }
            else //add the recipe
            {
                GameRegistry.addRecipe(new ItemStack(blockCompactPorkchop), "PPP", "PPP", "PPP", 'P', Items.PORKCHOP);
                GameRegistry.addShapelessRecipe(new ItemStack(Items.PORKCHOP, 9), blockCompactPorkchop);
            }
            isCompactPorkchopRecipeAdded = (config.enableCompactPorkchop == 1);
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
