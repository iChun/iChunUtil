package me.ichun.mods.ichunutil.common.core.config;

import com.google.common.collect.Ordering;
import net.minecraftforge.common.config.Configuration;

import java.util.TreeSet;

public class ConfigHandler
{
    public static TreeSet<ConfigBase> configs = new TreeSet<>(Ordering.natural());

    public static Configuration configKeybind;

    public static <T extends ConfigBase> T registerConfig(T config)
    {
        configs.add(config);
        config.read();
        config.storeSession();
        return config;
    }
}
