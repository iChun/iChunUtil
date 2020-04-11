package me.ichun.mods.ichunutil.client.gui.config;

import com.google.common.collect.Ordering;
import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementList;
import me.ichun.mods.ichunutil.client.gui.config.window.WindowConfigs;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class WorkspaceConfigs extends Workspace
{
    public TreeMap<String, TreeSet<ConfigInfo>> configs = new TreeMap<>(Ordering.natural());

    public WorkspaceConfigs(Screen lastScreen)
    {
        super(lastScreen, new TranslationTextComponent("gui.ichunutil.configs.title"), iChunUtil.configClient.guiStyleMinecraft);

        ConfigBase.CONFIGS.forEach((configBase -> {
            TreeSet<ConfigInfo> confs = configs.computeIfAbsent(configBase.getConfigName(), v -> new TreeSet<>(Ordering.natural()));
            confs.add(new ConfigInfo(configBase));
        }));

        addToDock(new WindowConfigs(this), Constraint.Property.Type.LEFT);
    }

    public void selectItem(ElementList.Item item)
    {

    }


    public static class ConfigInfo
            implements Comparable<ConfigInfo>
    {
        public final ConfigBase config;
        public final TreeMap<String, TreeSet<ValueWrapperLocalised>> categories = new TreeMap<>(Ordering.natural());

        public ConfigInfo(ConfigBase config)
        {
            this.config = config;
            for(Map.Entry<String, HashSet<ConfigBase.ValueWrapper>> e : config.values.entrySet())
            {
                TreeSet<ValueWrapperLocalised> set = categories.computeIfAbsent(e.getKey(), v -> new TreeSet<>(Ordering.natural()));
                for(ConfigBase.ValueWrapper valueWrapper : e.getValue())
                {
                    set.add(new ValueWrapperLocalised(valueWrapper, I18n.format("config." + config.getModId() + ".prop." + valueWrapper.field.getName() + ".name"), I18n.format("config." + config.getModId() + ".prop." + valueWrapper.field.getName() + ".desc")));
                }
            }
        }

        @Override
        public int compareTo(ConfigInfo o)
        {
            return config.compareTo(o.config);
        }

        public static class ValueWrapperLocalised
            implements Comparable<ValueWrapperLocalised>
        {
            public final ConfigBase.ValueWrapper value;
            public final String name;
            public final String desc;

            public ValueWrapperLocalised(ConfigBase.ValueWrapper value, String name, String desc) {
                this.value = value;
                this.name = name;
                this.desc = desc;
            }

            @Override
            public int compareTo(ValueWrapperLocalised o)
            {
                return name.compareTo(o.name);
            }
        }
    }
}
