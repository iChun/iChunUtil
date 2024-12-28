package me.ichun.mods.ichunutil.loader.forge;

import me.ichun.mods.ichunutil.client.core.ConfigClient;
import me.ichun.mods.ichunutil.client.core.ResourceHelper;
import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(iChunUtil.MOD_ID)
public class LoaderForge extends iChunUtil
{
    public LoaderForge(FMLJavaModLoadingContext context)
    {
        modProxy = this;

        if(FMLEnvironment.dist.isClient())
        {
            initClient(context);
        }

        //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
        context.registerExtensionPoint(IExtensionPoint.DisplayTest.class, IExtensionPoint.DisplayTest.IGNORE_ALL_VERSION);
    }

    @OnlyIn(Dist.CLIENT)
    private void initClient(FMLJavaModLoadingContext context)
    {
        ResourceHelper.init();

        configClient = d().registerConfig(new ConfigClient(), context); // configs cannot be initialised in setup stage.

        context.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new WorkspaceConfigs(screen, MOD_ID)));
    }
}
