package me.ichun.mods.ichunutil.client.gui.config;

import com.google.common.collect.Ordering;
import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.window.Window;
import me.ichun.mods.ichunutil.client.gui.bns.window.WindowDock;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementList;
import me.ichun.mods.ichunutil.client.gui.config.window.WindowConfigs;
import me.ichun.mods.ichunutil.client.gui.config.window.WindowValues;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.*;

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

        getDock().borderSize(() -> 0);
        addToDock(new WindowConfigs(this), Constraint.Property.Type.LEFT);
    }

    public void selectItem(ElementList.Item<?> item)
    {
        if(item.selected)
        {
            for(ElementList.Item<?> item1 : ((ElementList)item.parentFragment).items)
            {
                if(item1 != item)
                {
                    item1.selected = false; //workaround. Just make sure we don't got configs with no category
                }
            }

            destroyWindowValues();

            WindowValues window = new WindowValues(this, (ConfigInfo)item.getObject(), item.id);
//            addWindow(window);
//            window.setLeft(130);
            addToDock(window, Constraint.Property.Type.LEFT);
            window.constraint.right(getDock(), Constraint.Property.Type.RIGHT, -window.borderSize.get() + 1 + (Integer)getDock().borderSize.get());
            window.constraint.apply();
            if(hasInit())
            {
                window.init();
                window.resize(Minecraft.getInstance(), this.width, this.height);
            }
        }
    }

    public void destroyWindowValues()
    {
        WindowDock<?> dock = getDock();
        Iterator<Map.Entry<ArrayList<Window<?>>, Constraint.Property.Type>> ite = dock.docked.entrySet().iterator();
        while(ite.hasNext())
        {
            Map.Entry<ArrayList<Window<?>>, Constraint.Property.Type> e = ite.next();
            ArrayList<Window<?>> windows = e.getKey();
            for(int i = windows.size() - 1; i >= 0; i--)
            {
                Window<?> window = windows.get(i);
                if(window instanceof WindowValues)
                {
                    dock.dockedOriSize.remove(window);
                    if(windows.size() == 1)
                    {
                        ite.remove();
                    }
                    else
                    {
                        windows.remove(i);
                    }
                }
            }
        }
    }

    public static String getLocalizedCategory(WorkspaceConfigs.ConfigInfo info, String cat, String suffix)
    {
        if(cat.isEmpty())
        {
            return I18n.format("config.ichunutil.cat.general." + suffix);
        }
        else if(cat.equals("general") || cat.equals("gameplay") || cat.equals("global") || cat.equals("serverOnly") || cat.equals("clientOnly") || cat.equals("block"))
        {
            return I18n.format("config.ichunutil.cat."+ cat + "." + suffix);
        }
        return I18n.format("config." + info.config.getModId() + ".cat."+ cat + "." + suffix);
    }

    public static class ConfigInfo
            implements Comparable<ConfigInfo>
    {
        public final ConfigBase config;
        public final TreeMap<String, TreeSet<ValueWrapperLocalised>> categories = new TreeMap<>(Ordering.natural());

        public ConfigInfo(ConfigBase config)
        {
            this.config = config;
            for(Map.Entry<String, HashSet<ConfigBase.ValueWrapper<?>>> e : config.values.entrySet())
            {
                TreeSet<ValueWrapperLocalised> set = categories.computeIfAbsent(e.getKey(), v -> new TreeSet<>(Ordering.natural()));
                for(ConfigBase.ValueWrapper<?> valueWrapper : e.getValue())
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
            public final ConfigBase.ValueWrapper<?> value;
            public final String name;
            public final String desc;

            public ValueWrapperLocalised(ConfigBase.ValueWrapper<?> value, String name, String desc) {
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
