package us.ichun.mods.ichunutil.common.core.config;

import net.minecraftforge.common.config.Configuration;

import java.util.ArrayList;
import java.util.Collections;

public class ConfigHandler
{
    public static ArrayList<ConfigBase> configs = new ArrayList<ConfigBase>();

    public static Configuration configKeybind;

    public static ConfigBase registerConfig(ConfigBase config)
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
