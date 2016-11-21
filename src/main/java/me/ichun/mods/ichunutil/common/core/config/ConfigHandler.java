package me.ichun.mods.ichunutil.common.core.config;

import net.minecraftforge.common.config.Configuration;

import java.util.ArrayList;
import java.util.Collections;

public class ConfigHandler
{
    public static ArrayList<ConfigBase> configs = new ArrayList<>();

    public static Configuration configKeybind;

    public static <T extends ConfigBase> T registerConfig(T config)
    {
        if(!configs.contains(config))
        {
            configs.add(config);
            Collections.sort(configs);
        }
        config.read();
        config.storeSession();
        return config;
    }
}
