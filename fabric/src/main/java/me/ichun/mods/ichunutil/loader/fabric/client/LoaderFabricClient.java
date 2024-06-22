package me.ichun.mods.ichunutil.loader.fabric.client;

import me.ichun.mods.ichunutil.client.core.ConfigClient;
import me.ichun.mods.ichunutil.client.core.ResourceHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.fabricmc.api.ClientModInitializer;

public class LoaderFabricClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ResourceHelper.init();

        iChunUtil.eventHandlerClient = new EventHandlerClientFabric();

        iChunUtil.configClient = iChunUtil.d().registerConfig(new ConfigClient());
    }
}
