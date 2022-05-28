package me.ichun.mods.ichunutil.client.gui.config;

import com.google.common.collect.Ordering;
import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.window.Window;
import me.ichun.mods.ichunutil.client.gui.bns.window.WindowDock;
import me.ichun.mods.ichunutil.client.gui.bns.window.WindowPopup;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.*;
import me.ichun.mods.ichunutil.client.gui.config.window.WindowConfigs;
import me.ichun.mods.ichunutil.client.gui.config.window.WindowValues;
import me.ichun.mods.ichunutil.client.gui.config.window.view.ViewValues;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.loader.LoaderHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;

import java.lang.reflect.Field;
import java.util.*;

public class WorkspaceConfigs extends Workspace
{
    public TreeMap<String, TreeSet<ConfigInfo>> configs = new TreeMap<>(Ordering.natural());

    public WorkspaceConfigs(Screen lastScreen)
    {
        super(lastScreen, new TranslatableComponent("gui.ichunutil.configs.title"), iChunUtil.configClient.guiMinecraftStyle);

        ConfigBase.CONFIGS.forEach((configBase -> {
            TreeSet<ConfigInfo> confs = configs.computeIfAbsent(configBase.getConfigName(), v -> new TreeSet<>(Ordering.natural()));
            confs.add(new ConfigInfo(configBase));
        }));

        addToDock(new WindowConfigs(this), Constraint.Property.Type.LEFT);
    }

    public void selectItem(ElementList.Item<?> item)
    {
        if(item.selected)
        {
            for(ElementList.Item<?> item1 : item.parentFragment.items)
            {
                if(item1 != item)
                {
                    item1.selected = false; //workaround. Just make sure we don't got configs with no category
                }
            }

            destroyWindowValues();

            ConfigInfo config = (ConfigInfo)item.getObject();

            if(config.config.getConfigType().equals(ConfigBase.Type.SERVER) && !(Minecraft.getInstance().player != null && LoaderHandler.d().getMinecraftServer().isSingleplayer() && LoaderHandler.d().getMinecraftServer().getPlayerList().getPlayerCount() <= 1)) //Trying to edit a SERVER config in a non-singerplayer world environment.
            {
                WindowPopup.popup(this, 0.6D, 140, null, I18n.get("gui.ichunutil.configs.noEditingServerConfig"));
            }
            else
            {
                WindowValues window = new WindowValues(this, config, item.id);
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
    }

    public void destroyWindowValues()
    {
        WindowDock<?> dock = getDock();
        Iterator<Map.Entry<WindowDock.ArrayListHolder, Constraint.Property.Type>> ite = dock.docked.entrySet().iterator();
        while(ite.hasNext())
        {
            Map.Entry<WindowDock.ArrayListHolder, Constraint.Property.Type> e = ite.next();
            ArrayList<Window<?>> windows = e.getKey().windows;
            for(int i = windows.size() - 1; i >= 0; i--)
            {
                Window<?> window = windows.get(i);
                if(window instanceof WindowValues)
                {
                    saveConfig((ViewValues)((WindowValues)window).currentView);

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

    private void saveConfig(ViewValues view)
    {
        for(ElementList.Item<?> item : view.list.items)
        {
            Element<?> e = view.getControlElement(item);
            if(e != null) // we have the control element
            {
                ConfigInfo.EntryLocalised el = (ConfigInfo.EntryLocalised)item.getObject();

                Field field = el.entry.field;
                field.setAccessible(true);
                Class clz = field.getType();
                Object o;
                try
                {
                    o = field.get(el.config);

                    if(clz == int.class && e instanceof ElementNumberInput)
                    {
                        field.set(el.config, ((ElementNumberInput)e).getInt());
                    }
                    else if(clz == double.class && e instanceof ElementNumberInput)
                    {
                        field.set(el.config, ((ElementNumberInput)e).getDouble());
                    }
                    else if(clz == boolean.class && e instanceof ElementToggleTextable)
                    {
                        field.set(el.config, ((ElementToggle<?>)e).toggleState);
                    }
                    else if(clz == String.class && e instanceof ElementTextField)
                    {
                        field.set(el.config, ((ElementTextField)e).getText());
                    }
                    else if(clz.isEnum() && e instanceof ElementDropdownContextMenu) //enum!
                    {
                        Object[] enums = clz.getEnumConstants();
                        for(Object en : enums)
                        {
                            if(en.toString().equals(((ElementDropdownContextMenu<?>)e).text))
                            {
                                field.set(el.config, en);
                                break;
                            }
                        }
                    }
                    else if(o instanceof List) //lists
                    {
                        //List should have already been set by the editor.
                    }
                }
                catch(IllegalAccessException e1)
                {
                    continue;
                }
            }
        }
        view.info.config.save();
    }

    @Override
    public void onClose()
    {
        destroyWindowValues();
        super.onClose();
    }

    public static String getLocalizedCategory(WorkspaceConfigs.ConfigInfo info, String cat, String suffix)
    {
        if(cat.isEmpty())
        {
            return I18n.get("config.ichunutil.cat.general." + suffix);
        }
        else if(ConfigBase.DEFAULT_CATEGORY_COMMENTS.containsKey(cat))
        {
            return I18n.get("config.ichunutil.cat."+ cat + "." + suffix);
        }
        return I18n.get("config." + info.config.getModId() + ".cat."+ cat + "." + suffix);
    }

    public static class ConfigInfo
            implements Comparable<ConfigInfo>
    {
        public final ConfigBase config;
        public final TreeMap<String, TreeSet<EntryLocalised>> categories = new TreeMap<>(Ordering.natural());

        public ConfigInfo(ConfigBase config)
        {
            this.config = config;
            for(ConfigBase.Category category : config.categories)
            {
                TreeSet<EntryLocalised> entries = categories.computeIfAbsent(category.name, k -> new TreeSet<>(Comparator.naturalOrder()));

                for(ConfigBase.Category.Entry entry : category.getEntries())
                {
                    entries.add(new EntryLocalised(config, entry, I18n.get("config." + config.getModId() + ".prop." + entry.field.getName() + ".name"), I18n.get("config." + config.getModId() + ".prop." + entry.field.getName() + ".desc")));
                }
            }
        }

        @Override
        public int compareTo(ConfigInfo o)
        {
            return config.compareTo(o.config);
        }

        public static class EntryLocalised
                implements Comparable<EntryLocalised>
        {
            public final ConfigBase config;
            public final ConfigBase.Category.Entry entry;
            public final String name;
            public final String desc;

            public EntryLocalised(ConfigBase config, ConfigBase.Category.Entry entry, String name, String desc) {
                this.config = config;
                this.entry = entry;
                this.name = name;
                this.desc = desc;
            }

            @Override
            public int compareTo(EntryLocalised o)
            {
                return name.compareTo(o.name);
            }
        }
    }
}
