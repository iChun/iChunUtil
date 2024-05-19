package me.ichun.mods.ichunutil.common.config;

import java.util.HashSet;

public abstract class ConfigHandler
{
    private static final HashSet<ConfigHandler> SERVER_CONFIGS = new HashSet<>();

    public final ConfigBase config;

    protected ConfigHandler(ConfigBase config)
    {
        this.config = config;

        this.config.compile();

        init();

        if(this.config.getConfigType() == ConfigBase.Type.SERVER)
        {
            SERVER_CONFIGS.add(this);
        }
    }

    public abstract void init();

    protected void checkForChangesFromFile(boolean reload){} //Fabric doesn't do anything

    //Not called on Fabric as our loader manages the caching for us
    public static void onServerConnect()
    {
        for(ConfigHandler handler : SERVER_CONFIGS)
        {
            handler.config.cache();

            handler.checkForChangesFromFile(true);
        }
    }

    public static void onServerDisconnect()
    {
        for(ConfigHandler handler : SERVER_CONFIGS)
        {
            handler.config.restoreFromCache();
        }
    }
}
