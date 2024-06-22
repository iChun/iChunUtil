package me.ichun.mods.ichunutil.loader.forge;

import me.ichun.mods.ichunutil.client.core.ConfigClient;
import me.ichun.mods.ichunutil.client.core.ResourceHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.loader.forge.client.EventHandlerClientForge;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(iChunUtil.MOD_ID)
public class LoaderForge extends iChunUtil
{
    public LoaderForge()
    {
        modProxy = this;

        if(FMLEnvironment.dist.isClient())
        {
            initClient();
        }

        //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, IExtensionPoint.DisplayTest.IGNORE_ALL_VERSION);
    }

    @OnlyIn(Dist.CLIENT)
    private void initClient()
    {
        ResourceHelper.init();

        configClient = d().registerConfig(new ConfigClient()); // configs cannot be initialised in setup stage.
    }
}
