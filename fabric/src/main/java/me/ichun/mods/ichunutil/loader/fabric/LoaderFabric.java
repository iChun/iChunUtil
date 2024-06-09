package me.ichun.mods.ichunutil.loader.fabric;

import me.ichun.mods.ichunutil.common.iChunUtil;
import net.fabricmc.api.ModInitializer;

public class LoaderFabric extends iChunUtil
    implements ModInitializer
{
    @Override
    public void onInitialize()
    {
        modProxy = this;

        loaderDelegate = new LoaderDelegateFabric();

        ServerListenerFabric.init();
    }
}
