package me.ichun.mods.ichunutil.loader.fabric;

import me.ichun.mods.ichunutil.common.iChunUtil;
import net.fabricmc.api.ModInitializer;

public class LoaderFabric extends iChunUtil
        implements ModInitializer
{
    public LoaderFabric()
    {
        iChunUtil.INSTANCE = this;

        eventHandlerServer = new EventHandlerServerFabric();
    }

    @Override
    public void onInitialize()
    {

    }
}
