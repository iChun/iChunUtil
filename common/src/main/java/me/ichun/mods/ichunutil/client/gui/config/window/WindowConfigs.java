package me.ichun.mods.ichunutil.client.gui.config.window;

import me.ichun.mods.ichunutil.client.gui.bns.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.Window;
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

public class WindowConfigs extends Window<WorkspaceConfigs, WindowConfigs.ViewConfigs>
{
    public WindowConfigs(WorkspaceConfigs parent)
    {
        super(parent);
        setView(new ViewConfigs(this, "gui.ichunutil.configs.configs"));
        pos(20, 20);
        size(120, 300);
        disableUndocking();
        disableDrag();
        disableBringToFront();
    }

    public static class ViewConfigs extends View<WindowConfigs>
    {
        public boolean createdRestartAlert = false;

        public ViewConfigs(@NotNull WindowConfigs parent, @NotNull String s)
        {
            super(parent, s);

            ElementButton<?> btn = new ElementButton<>(this, "gui.done", button -> {
                parent.parent.onClose();
            });
            btn.setWidth(60);
            btn.setHeight(20);
            btn.setConstraint(new Constraint(btn).left(this, Constraint.Property.Type.LEFT, 5)
                .right(this, Constraint.Property.Type.RIGHT, 30)
                .bottom(this, Constraint.Property.Type.BOTTOM, 5)
            );
            elements.add(btn);

            ElementScrollBar<?> sv = new ElementScrollBar<>(this, ElementScrollBar.Orientation.VERTICAL, 0.6F);
            sv.setConstraint(new Constraint(sv).top(this, Constraint.Property.Type.TOP, 0)
                .bottom(btn, Constraint.Property.Type.TOP, 5)
                .right(this, Constraint.Property.Type.RIGHT, 0)
            );
            elements.add(sv);

            ElementList<?> list = new ElementList<>(this).setScrollVertical(sv);
            list.setConstraint(new Constraint(list).left(this, Constraint.Property.Type.LEFT, 0)
                .bottom(btn, Constraint.Property.Type.TOP, 5)
                .top(this, Constraint.Property.Type.TOP, 0)
                .right(sv, Constraint.Property.Type.LEFT, 0)
            );

            for(Map.Entry<String, TreeSet<ConfigBase>> e : parent.parent.modToConfig.entrySet())
            {
                list.addItem(e.getValue()).addTextWrapper(e.getKey()).setSelectionHandler(item -> {
                    if(item.selected)
                    {
                        //destroy window values if exists
                        if(parent.parent.windowValues != null)
                        {
                            parent.parent.windowValues.getCurrentView().save();
                            parent.parent.removeFromDock(parent.parent.windowValues, false);

                            parent.parent.windowValues = null;
                        }

                        WindowValues window = new WindowValues(parent.parent, e.getValue());
                        parent.parent.windowValues = window;
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
            elements.add(list);
        }

        public void createRestartAlertButton()
        {
            if(!createdRestartAlert)
            {
                createdRestartAlert = true;

                ElementButton<?> btn = new ElementButton<>(this, "!", button -> {});
                btn.setWidth(20);
                btn.setHeight(20);
                btn.setConstraint(new Constraint(btn).right(this, Constraint.Property.Type.RIGHT, 5)
                    .bottom(this, Constraint.Property.Type.BOTTOM, 5)
                );
                btn.setTooltip(I18n.get("gui.ichunutil.configs.needsRestart"));
                elements.add(btn);

                this.resize(getMinecraft(), this.width, this.height);
            }
        }
    }
}
