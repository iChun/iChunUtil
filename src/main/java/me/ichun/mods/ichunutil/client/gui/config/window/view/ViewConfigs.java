package me.ichun.mods.ichunutil.client.gui.config.window.view;

import me.ichun.mods.ichunutil.client.gui.bns.Theme;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementList;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementScrollBar;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementTextWrapper;
import me.ichun.mods.ichunutil.client.gui.config.window.WindowConfigs;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class ViewConfigs extends View<WindowConfigs>
{
    public ViewConfigs(@Nonnull WindowConfigs parent, @Nonnull String s)
    {
        super(parent, s);

        ElementScrollBar sv = new ElementScrollBar(this, ElementScrollBar.Orientation.VERTICAL, 0.6F);
        sv.setConstraint(new Constraint(sv).top(this, Constraint.Property.Type.TOP, 0)
                .bottom(this, Constraint.Property.Type.BOTTOM, 0)
                .right(this, Constraint.Property.Type.RIGHT, 0)
        );
        elements.add(sv);

        ElementList list = new ElementList(this).setScrollVertical(sv).setDragHandler((i, j) -> {})
                .setRearrangeHandler((i, j) -> {})
                ;
        list.setConstraint(new Constraint(list).left(this, Constraint.Property.Type.LEFT, 0)
//                .bottom(sh, Constraint.Property.Type.TOP, 0)
                .bottom(this, Constraint.Property.Type.BOTTOM, 0)
                .top(this, Constraint.Property.Type.TOP, 0)
                .right(sv, Constraint.Property.Type.LEFT, 0)
        );

        for(Map.Entry<String, ArrayList<ConfigBase>> e : parent.parent.configs.entrySet())
        {
            list.addItem(e.getKey()).addTextWrapper(e.getKey());
            for(ConfigBase configBase : e.getValue())
            {
                ElementList.Item item = list.addItem(configBase); //TODO tooltip provider?
                ElementTextWrapper wrapper = new ElementTextWrapper(item).setText(configBase.getConfigName()).setColor(getColorForType(configBase.getConfigType()));
                wrapper.setConstraint(Constraint.matchParent(wrapper, item, item.getBorderSize()).top(item, Constraint.Property.Type.TOP, item.getBorderSize()).bottom(null, Constraint.Property.Type.BOTTOM, 0));
                item.addElement(wrapper);
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
