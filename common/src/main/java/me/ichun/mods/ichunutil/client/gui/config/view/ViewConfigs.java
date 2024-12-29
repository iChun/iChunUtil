package me.ichun.mods.ichunutil.client.gui.config.view;

import me.ichun.mods.ichunutil.client.gui.bns.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.WindowGeneric;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementButton;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementList;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementScrollBar;
import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.TreeSet;

public class ViewConfigs extends View<WindowGeneric<WorkspaceConfigs,ViewConfigs>>
{
    public boolean createdRestartAlert = false;

    public final ElementList<ViewConfigs, TreeSet<ConfigBase>> modsList;

    public ViewConfigs(@NotNull WindowGeneric<WorkspaceConfigs,ViewConfigs> parent, @NotNull String s)
    {
        super(parent, s);

        ElementButton btn = new ElementButton(this, "gui.done", (button, mouseX, mouseY) -> {
            parent.parent.onClose();
        });
        btn.setWidth(60);
        btn.setHeight(20);
        btn.setConstraint(new Constraint(btn).left(this, Constraint.Property.Type.LEFT, 5).right(this, Constraint.Property.Type.RIGHT, 30).bottom(this, Constraint.Property.Type.BOTTOM, 5));
        elements.add(btn);

        ElementScrollBar sv = new ElementScrollBar(this, ElementScrollBar.Orientation.VERTICAL, 0.6F);
        sv.setConstraint(new Constraint(sv).top(this, Constraint.Property.Type.TOP, 0).bottom(btn, Constraint.Property.Type.TOP, 5).right(this, Constraint.Property.Type.RIGHT, 0));
        elements.add(sv);

        modsList = new ElementList<>(this);
        modsList.setScrollVertical(sv).setConstraint(new Constraint(modsList).left(this, Constraint.Property.Type.LEFT, 0).bottom(btn, Constraint.Property.Type.TOP, 5).top(this, Constraint.Property.Type.TOP, 0).right(sv, Constraint.Property.Type.LEFT, 0));

        for(Map.Entry<String, TreeSet<ConfigBase>> e : parent.parent.modToConfig.entrySet())
        {
            modsList.addItem(e.getValue()).addTextWrapper(e.getKey()).setSelectionHandler(item -> {
                if(item.selected)
                {
                    //destroy window values if exists
                    if(parent.parent.viewValues != null)
                    {
                        parent.parent.viewValues.save();
                        parent.parent.removeFromDock(parent.parent.viewValues.parent, false);

                        parent.parent.viewValues = null;
                    }

                    WindowGeneric<WorkspaceConfigs, ViewValues> window = WindowGeneric.create(parent.parent, windowGeneric -> new ViewValues(windowGeneric, e.getValue().getFirst().getConfigName(), e.getValue()));
                    parent.parent.viewValues = window.getCurrentView();
                    parent.parent.addToDock(window, Constraint.Property.Type.LEFT);
                    window.constraint.right(parent.parent.getDock(), Constraint.Property.Type.RIGHT, -window.borderSize.get() + 1 + (Integer)parent.parent.getDock().borderSize.get());
                    window.constraint.apply();
                    if(parent.parent.hasInit())
                    {
                        window.init();
                        window.resize(getMinecraft(), this.width, this.height);
                    }
                }
            });
        }
        elements.add(modsList);
    }

    @Override
    public void setWindowGenericProperties(WindowGeneric<?, ?> window)
    {
        window.pos(20, 20);
        window.size(120, 300);
        window.disableUndocking();
        window.disableDrag();
        window.disableBringToFront();
    }

    public void createRestartAlertButton()
    {
        if(!createdRestartAlert)
        {
            createdRestartAlert = true;

            ElementButton btn = new ElementButton(this, "!", (button, mouseX, mouseY) -> {
            });
            btn.setWidth(20);
            btn.setHeight(20);
            btn.setConstraint(new Constraint(btn).right(this, Constraint.Property.Type.RIGHT, 5).bottom(this, Constraint.Property.Type.BOTTOM, 5));
            btn.setTooltip(I18n.get("gui.ichunutil.configs.needsRestart"));
            elements.add(btn);

            this.resize(getMinecraft(), this.width, this.height);
        }
    }
}
