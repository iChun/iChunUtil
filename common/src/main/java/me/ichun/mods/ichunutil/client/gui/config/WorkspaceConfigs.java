package me.ichun.mods.ichunutil.client.gui.config;

import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.WindowGeneric;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementButton;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementList;
import me.ichun.mods.ichunutil.client.gui.config.view.ViewConfigs;
import me.ichun.mods.ichunutil.client.gui.config.view.ViewValues;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.TreeSet;

public class WorkspaceConfigs extends Workspace<WorkspaceConfigs>
{
    public final LinkedHashMap<String, TreeSet<ConfigBase>> modToConfig = new LinkedHashMap<>();

    public ViewConfigs viewConfigs;
    public ViewValues viewValues;

    @Nullable
    public String selectedModId;

    public WorkspaceConfigs(Screen lastScreen, @Nullable String selectedModId)
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

        this.selectedModId = selectedModId;
    }

    @Override
    protected void init()
    {
        boolean hasInit = hasInit();
        super.init();

        if(!hasInit && selectedModId != null)
        {
            for(ElementList.Item<TreeSet<ConfigBase>> item : viewConfigs.modsList.items)
            {
                TreeSet<ConfigBase> configs = item.getObject();
                if(configs.getFirst().getModId().equals(selectedModId))
                {
                    item.parent.setFocused(item);
                    item.selected = true;
                    item.triggerSelectionHandler();
                    break;
                }
            }
        }
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

    public static boolean createButtonToKeyBinds(ConfigBase.Category.Entry entry, ElementList.Item<?> item)
    {
        ElementButton button = new ElementButton(item, "controls.title", (btn, mouseX, mouseY) -> {
            item.getMinecraft().setScreen(new KeyBindsScreen(item.getWorkspace(), item.getMinecraft().options));
        });
        button.setTooltip(I18n.get("options.controls"));
        button.setSize(80, 14);
        button.setConstraint(new Constraint(button).top(item, Constraint.Property.Type.TOP, 3).bottom(item, Constraint.Property.Type.BOTTOM, 3).right(item, Constraint.Property.Type.RIGHT, 8));
        item.addElement(button);
        return true;
    }
}
