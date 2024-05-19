package me.ichun.mods.ichunutil.common;

import com.mojang.logging.LogUtils;
import me.ichun.mods.ichunutil.client.core.ConfigClient;
import me.ichun.mods.ichunutil.common.entity.EntityPersistentDataHandler;
import me.ichun.mods.ichunutil.loader.LoaderDelegate;
import me.ichun.mods.ichunutil.loader.client.LoaderDelegateClient;
import org.slf4j.Logger;

public abstract class iChunUtil //TODO double check the display test for the neoforge ports.
{
    public static final String MOD_ID = "ichunutil";
    public static final String MOD_NAME = "iChunUtil";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static iChunUtil modProxy;

    public static LoaderDelegate loaderDelegate;
    public static LoaderDelegateClient loaderDelegateClient;

    public static ConfigClient configClient;

    public static EntityPersistentDataHandler entityPersistentDataHandler;

    public static iChunUtil p()
    {
        return modProxy;
    }

    public static LoaderDelegate d()
    {
        return loaderDelegate;
    }

    public static LoaderDelegateClient dC()
    {
        return loaderDelegateClient;
    }

    public iChunUtil()
    {
    }
}
