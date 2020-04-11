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

        ElementButton btn = new ElementButton(this, I18n.format("gui.done"));
        btn.setWidth(70);
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
            list.addItem(e.getKey()).addTextWrapper(e.getKey());
            for(WorkspaceConfigs.ConfigInfo info : e.getValue())
            {
                for(String key : info.categories.keySet())
                {
                    ElementList.Item item = list.addItem(info).setSelectionHandler(item1 -> parent.parent.selectItem((ElementList.Item)item1)); //TODO tooltip provider?
                    item.setTooltip(getLocalizedCategory(info, key, "description"));
                    ElementTextWrapper wrapper = new ElementTextWrapper(item).setText(" - " + getLocalizedCategory(info, key, "name")).setColor(getColorForType(info.config.getConfigType()));
                    wrapper.setConstraint(Constraint.matchParent(wrapper, item, item.getBorderSize()).top(item, Constraint.Property.Type.TOP, item.getBorderSize()).bottom(null, Constraint.Property.Type.BOTTOM, 0));
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

    public String getLocalizedCategory(WorkspaceConfigs.ConfigInfo info, String cat, String suffix)
    {
        if(cat.equals("general") || cat.equals("gameplay") || cat.equals("global") || cat.equals("serverOnly") || cat.equals("clientOnly") || cat.equals("block"))
        {
            return I18n.format("config.ichunutil.cat."+ cat + "." + suffix);
        }
        return I18n.format("config." + info.config.getModId() + ".cat."+ cat + "." + suffix);
    }
}
