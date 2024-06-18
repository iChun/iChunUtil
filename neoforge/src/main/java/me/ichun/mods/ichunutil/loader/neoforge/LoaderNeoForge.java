package me.ichun.mods.ichunutil.loader.neoforge;

import me.ichun.mods.ichunutil.client.core.ConfigClient;
import me.ichun.mods.ichunutil.client.core.ResourceHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(iChunUtil.MOD_ID)
public class LoaderNeoForge extends iChunUtil
{
    public LoaderNeoForge(IEventBus modEventBus)
    {
        modProxy = this;

        if(FMLEnvironment.dist.isClient())
        {
            initClient(modEventBus);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void initClient(IEventBus modEventBus)
    {
        ResourceHelper.init();

        configClient = d().registerConfig(new ConfigClient(), modEventBus); // configs cannot be initialised in setup stage.
    }
}
