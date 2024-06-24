package me.ichun.mods.ichunutil.client.gui.config;

import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.WindowGeneric;
import me.ichun.mods.ichunutil.client.gui.config.view.ViewConfigs;
import me.ichun.mods.ichunutil.client.gui.config.view.ViewValues;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.TreeSet;

public class WorkspaceConfigs extends Workspace
{
    public final LinkedHashMap<String, TreeSet<ConfigBase>> modToConfig = new LinkedHashMap<>();

    public ViewConfigs viewConfigs;
    public ViewValues viewValues;

    public WorkspaceConfigs(Screen lastScreen)
    {
        super(lastScreen, Component.translatable("gui.ichunutil.configs.title"));

        for(ConfigBase config : ConfigBase.CONFIGS)
        {
            TreeSet<ConfigBase> modConfigs = modToConfig.computeIfAbsent(config.getConfigName(), key -> new TreeSet<>(Comparator.naturalOrder()));
            modConfigs.add(config);
        }

        WindowGeneric<WorkspaceConfigs, ViewConfigs> window = WindowGeneric.create(this, windowGeneric -> new ViewConfigs(windowGeneric, "gui.ichunutil.configs.configs"));
        viewConfigs = window.getCurrentView();
        addToDock(window, Constraint.Property.Type.LEFT);
    }

    @Override
    public void onClose()
    {
        if(viewValues != null)
        {
            viewValues.save();
        }
        super.onClose();
    }
}
