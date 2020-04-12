package me.ichun.mods.ichunutil.client.gui.config.window.view;

import me.ichun.mods.ichunutil.client.gui.bns.Theme;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementButton;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementList;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementScrollBar;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementTextWrapper;
import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import me.ichun.mods.ichunutil.client.gui.config.window.WindowConfigs;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.TreeSet;

public class ViewConfigs extends View<WindowConfigs>
{
    public ViewConfigs(@Nonnull WindowConfigs parent, @Nonnull String s)
    {
        super(parent, s);

        ElementButton btn = new ElementButton(this, I18n.format("gui.done"), button -> {
            parent.parent.onClose();
        });
        btn.setWidth(60);
        btn.setHeight(20);
        btn.setConstraint(new Constraint(btn).left(this, Constraint.Property.Type.LEFT, 5)
                .bottom(this, Constraint.Property.Type.BOTTOM, 5)
        );
        elements.add(btn);

        ElementScrollBar sv = new ElementScrollBar(this, ElementScrollBar.Orientation.VERTICAL, 0.6F);
        sv.setConstraint(new Constraint(sv).top(this, Constraint.Property.Type.TOP, 0)
                .bottom(btn, Constraint.Property.Type.TOP, 5)
                .right(this, Constraint.Property.Type.RIGHT, 0)
        );
        elements.add(sv);

        ElementList list = new ElementList(this).setScrollVertical(sv)
                //                .setDragHandler((i, j) -> {})
                //                .setRearrangeHandler((i, j) -> {})
                ;
        list.setConstraint(new Constraint(list).left(this, Constraint.Property.Type.LEFT, 0)
                .bottom(btn, Constraint.Property.Type.TOP, 5)
                .top(this, Constraint.Property.Type.TOP, 0)
                .right(sv, Constraint.Property.Type.LEFT, 0)
        );

        for(Map.Entry<String, TreeSet<WorkspaceConfigs.ConfigInfo>> e : parent.parent.configs.entrySet())
        {
            list.addItem(e.getKey()).addTextWrapper(e.getKey()).setSelectionHandler(o -> {
                ElementList.Item item = ((ElementList.Item)o);
                if(item.selected)
                {
                    item.selected = false;
                    for(ElementList.Item item1 : ((ElementList)item.parentFragment).items)
                    {
                        item1.selected = false; //workaround. Just make sure we don't got configs with no category
                    }
                    for(ElementList.Item item1 : ((ElementList)item.parentFragment).items)
                    {
                        if(item1.getObject() == e.getValue().first())
                        {
                            item1.selected = true;
                            parent.parent.selectItem(item1);
                            break;
                        }
                    }
                }
            });
            for(WorkspaceConfigs.ConfigInfo info : e.getValue())
            {
                for(String key : info.categories.keySet())
                {
                    ElementList.Item item = list.addItem(info).setSelectionHandler(item1 -> parent.parent.selectItem((ElementList.Item)item1));
                    item.setId(key);
                    item.setTooltip(WorkspaceConfigs.getLocalizedCategory(info, key, "desc"));
                    ElementTextWrapper wrapper = new ElementTextWrapper(item).setText(" - " + WorkspaceConfigs.getLocalizedCategory(info, key, "name")).setColor(getColorForType(info.config.getConfigType()));
                    wrapper.setConstraint(Constraint.matchParent(wrapper, item, item.getBorderSize()).top(item, Constraint.Property.Type.TOP, item.getBorderSize()).bottom(null, Constraint.Property.Type.BOTTOM, 0));
                    wrapper.setTooltip(WorkspaceConfigs.getLocalizedCategory(info, key, "desc"));
                    item.addElement(wrapper);
                }
            }
        }
        elements.add(list);
    }

    public int getColorForType(ModConfig.Type type)
    {
        switch(type)
        {
            case CLIENT: return Theme.getAsHex(getTheme().font);
            case COMMON: return Theme.getAsHex(getTheme().fontChat);
            case SERVER: return Theme.getAsHex(getTheme().fontDim);
            default: return 0xff0000;
        }
    }
}
