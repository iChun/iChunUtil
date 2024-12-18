package me.ichun.mods.ichunutil.common.config;

import me.ichun.mods.ichunutil.client.key.KeyBind;
import me.ichun.mods.ichunutil.common.iChunUtil;

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

    public abstract Object getEventBus();

    protected void registerKeybinds()
    {
        for(ConfigBase.Category cat : config.categories)
        {
            for(ConfigBase.Category.Entry e : cat.getEntries())
            {
                Class<?> clz = e.field.getType();
                String fieldName = e.field.getName();
                if(clz.equals(KeyBind.class))
                {
                    try
                    {
                        KeyBind keyBind = (KeyBind)e.field.get(config);
                        keyBind.register(getEventBus());
                    }
                    catch(IllegalAccessException | IllegalArgumentException ex)
                    {
                        iChunUtil.LOGGER.error("Error registering keybind {} for config {}", fieldName, config.getFileName(), ex);
                    }
                }
            }
        }
    }

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
