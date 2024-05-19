package me.ichun.mods.ichunutil.loader.fabric.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;

public class ModMenuIntegration implements ModMenuApi //TODO update teh fabric.mod.jsons for all the other mods, add URLS, add modmenu entrypoint.
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return WorkspaceConfigs::new;
    }
}