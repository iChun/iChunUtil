package me.ichun.mods.ichunutil.loader.fabric.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;

public class ModMenuIntegration implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return WorkspaceConfigs::new;
    }
}