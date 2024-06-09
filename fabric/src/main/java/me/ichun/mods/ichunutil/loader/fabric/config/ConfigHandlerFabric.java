package me.ichun.mods.ichunutil.loader.fabric.config;

import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.config.ConfigHandler;
import me.ichun.mods.ichunutil.loader.fabric.config.loader.FabricConfigLoader;

public class ConfigHandlerFabric extends ConfigHandler
{
    public ConfigHandlerFabric(ConfigBase config)
    {
        super(config);
    }

    @Override
    public void init()
    {
        FabricConfigLoader.registerConfig(config);
    }
}
