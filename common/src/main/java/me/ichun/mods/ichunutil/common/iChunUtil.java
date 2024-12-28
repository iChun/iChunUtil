package me.ichun.mods.ichunutil.common;

import com.mojang.logging.LogUtils;
import me.ichun.mods.ichunutil.client.core.ConfigClient;
import me.ichun.mods.ichunutil.client.core.EventHandlerClient;
import me.ichun.mods.ichunutil.common.core.EventHandlerServer;
import me.ichun.mods.ichunutil.common.entity.EntityPersistentDataHandler;
import me.ichun.mods.ichunutil.loader.LoaderDelegate;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public abstract class iChunUtil
{
    static
    {
        LoaderDelegate.assignLoaderDelegate();
    }

    public static final String MOD_ID = "ichunutil";
    public static final String MOD_NAME = "iChunUtil";

    public static final Logger LOGGER = LogUtils.getLogger();

    protected static iChunUtil modProxy;

    public static LoaderDelegate loaderDelegate;

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public static EventHandlerClient eventHandlerClient;
    public static EventHandlerServer eventHandlerServer;

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public static ConfigClient configClient;

    public static EntityPersistentDataHandler entityPersistentDataHandler;

    @NotNull
    public static iChunUtil p()
    {
        return modProxy;
    }

    @NotNull
    public static LoaderDelegate d()
    {
        return loaderDelegate;
    }

    @NotNull
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public static EventHandlerClient eC()
    {
        return eventHandlerClient;
    }

    @NotNull
    public static EventHandlerServer eS()
    {
        return eventHandlerServer;
    }

    public iChunUtil(){}
}
