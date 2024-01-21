package me.ichun.mods.ichunutil.loader.fabric;

import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.loader.fabric.event.FabricEvents;
import net.fabricmc.api.ModInitializer;

public class LoaderFabric extends iChunUtil
        implements ModInitializer
{
    private static boolean pushedLoadComplete = false;

    public LoaderFabric()
    {
        iChunUtil.INSTANCE = this;

        eventHandlerServer = new EventHandlerServerFabric();
    }

    @Override
    public void onInitialize()
    {

    }

    public static void pushLoadComplete()
    {
        if(!pushedLoadComplete)
        {
            pushedLoadComplete = true;
            FabricEvents.LOAD_COMPLETE.invoker().onLoadComplete();
        }
    }
}
