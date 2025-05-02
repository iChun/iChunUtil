package me.ichun.mods.ichunutil.loader.fabric.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.iChunUtil;

import java.util.HashMap;
import java.util.Map;

public class ModMenuIntegration implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return screen -> new WorkspaceConfigs(screen, iChunUtil.MOD_ID);
    }

    @Override
    public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories()
    {
        HashMap<String, ConfigScreenFactory<?>> mods = new HashMap<>();
        for(ConfigBase config : ConfigBase.CONFIGS)
        {
            mods.put(config.getModId(), screen -> new WorkspaceConfigs(screen, config.getModId()));
        }
        return mods;
    }
}