package me.ichun.mods.ichunutil.loader.fabric.client;

import me.ichun.mods.ichunutil.client.core.ConfigClient;
import me.ichun.mods.ichunutil.client.core.ResourceHelper;
import me.ichun.mods.ichunutil.common.entity.util.EntityHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.loader.LoaderHandler;
import me.ichun.mods.ichunutil.loader.fabric.event.FabricClientEvents;
import net.fabricmc.api.ClientModInitializer;

public class LoaderFabricClient
        implements ClientModInitializer
{
    public LoaderFabricClient()
    {
        ResourceHelper.init();

        iChunUtil.configClient = LoaderHandler.d().registerConfig(new ConfigClient());

        EntityHelper.injectMinecraftPlayerGameProfile();
    }

    @Override
    public void onInitializeClient()
    {
        iChunUtil.eventHandlerClient = new EventHandlerClientFabric();

        FabricClientEvents.CLIENT_MOD_INIT.invoker().onClientModInit(null);
    }
}
