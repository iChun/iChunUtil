package me.ichun.mods.ichunutil.client.gui.config;

import com.google.common.collect.Ordering;
import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.config.window.WindowConfigs;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

public class WorkspaceConfigs extends Workspace
{
    public TreeMap<String, ArrayList<ConfigBase>> configs = new TreeMap<>(Ordering.natural());

    public WorkspaceConfigs(Screen lastScreen)
    {
        super(lastScreen, new TranslationTextComponent("gui.ichunutil.configs.title"), iChunUtil.configClient.guiStyleMinecraft);

        ConfigBase.CONFIGS.forEach((configBase -> {
            ArrayList<ConfigBase> confs = configs.computeIfAbsent(configBase.getConfigName(), v -> new ArrayList<>());
            confs.add(configBase);
            Collections.sort(confs);
        }));

        addToDock(new WindowConfigs(this), Constraint.Property.Type.LEFT);
    }
}
