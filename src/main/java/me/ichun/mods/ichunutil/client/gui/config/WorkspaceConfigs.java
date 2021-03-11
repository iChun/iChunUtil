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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.lang.reflect.Field;
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

            if(config.config.getConfigType().equals(ModConfig.Type.SERVER) && Minecraft.getInstance().player != null && (ServerLifecycleHooks.getCurrentServer() == null || ServerLifecycleHooks.getCurrentServer().isSinglePlayer() && ServerLifecycleHooks.getCurrentServer().getPlayerList().getCurrentPlayerCount() > 1)) //is on dedicated server
            {
                WindowPopup.popup(this, 0.6D, 140, null, I18n.format("gui.ichunutil.configs.noEditingServerConfig"));
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
                ConfigBase.ValueWrapper<?> valueWrapper = ((ConfigInfo.ValueWrapperLocalised)item.getObject()).value;

                Field field = valueWrapper.field;
                field.setAccessible(true);
                Class clz = field.getType();
                Object o;
                try
                {
                    o = field.get(valueWrapper.parent);

                    if(clz == int.class && e instanceof ElementNumberInput)
                    {
                        field.set(valueWrapper.parent, ((ElementNumberInput)e).getInt());
                    }
                    else if(clz == double.class && e instanceof ElementNumberInput)
                    {
                        field.set(valueWrapper.parent, ((ElementNumberInput)e).getDouble());
                    }
                    else if(clz == boolean.class && e instanceof ElementToggleTextable)
                    {
                        field.set(valueWrapper.parent, ((ElementToggle<?>)e).toggleState);
                    }
                    else if(clz == String.class && e instanceof ElementTextField)
                    {
                        field.set(valueWrapper.parent, ((ElementTextField)e).getText());
                    }
                    else if(clz.isEnum() && e instanceof ElementDropdownContextMenu) //enum!
                    {
                        Object[] enums = clz.getEnumConstants();
                        for(Object en : enums)
                        {
                            if(en.toString().equals(((ElementDropdownContextMenu<?>)e).text))
                            {
                                field.set(valueWrapper.parent, en);
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
    public void closeScreen()
    {
        destroyWindowValues();
        super.closeScreen();
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
