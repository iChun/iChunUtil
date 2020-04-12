package me.ichun.mods.ichunutil.client.gui.config.window.view;

import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.*;
import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import me.ichun.mods.ichunutil.client.gui.config.window.WindowValues;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.config.annotations.Prop;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class ViewValues extends View<WindowValues>
{
    public final WorkspaceConfigs.ConfigInfo info;
    public final String category;
    public final TreeSet<WorkspaceConfigs.ConfigInfo.ValueWrapperLocalised> values;

    public ViewValues(@Nonnull WindowValues parent, @Nonnull String s, WorkspaceConfigs.ConfigInfo info, String category, TreeSet<WorkspaceConfigs.ConfigInfo.ValueWrapperLocalised> values)
    {
        super(parent, s);
        this.info = info;
        this.category = category;
        this.values = values;

        ElementScrollBar sv = new ElementScrollBar(this, ElementScrollBar.Orientation.VERTICAL, 0.6F);
        sv.setConstraint(new Constraint(sv).top(this, Constraint.Property.Type.TOP, 0)
                .bottom(this, Constraint.Property.Type.BOTTOM, 0)
                .right(this, Constraint.Property.Type.RIGHT, 0)
        );
        elements.add(sv);

        ElementList list = new ElementList(this).setScrollVertical(sv)
                //                .setDragHandler((i, j) -> {})
                //                .setRearrangeHandler((i, j) -> {})
                ;
        list.setConstraint(new Constraint(list).left(this, Constraint.Property.Type.LEFT, 0)
                .bottom(this, Constraint.Property.Type.BOTTOM, 0)
                .top(this, Constraint.Property.Type.TOP, 0)
                .right(sv, Constraint.Property.Type.LEFT, 0)
        );

        for(WorkspaceConfigs.ConfigInfo.ValueWrapperLocalised value : values)
        {
            ElementList.Item item = list.addItem(value).setBorderSize(0);
            ElementTextWrapper wrapper = new ElementTextWrapper(item).setText(value.name);
            wrapper.setConstraint(Constraint.matchParent(wrapper, item, item.getBorderSize()).right(item, Constraint.Property.Type.RIGHT, 90).top(item, Constraint.Property.Type.TOP, item.getBorderSize()).bottom(item, Constraint.Property.Type.BOTTOM, item.getBorderSize()));
            wrapper.setTooltip(value.desc);
            item.addElement(wrapper);
            ElementPadding padding = new ElementPadding(item, 0, 20);
            padding.setConstraint(new Constraint(padding).right(item, Constraint.Property.Type.RIGHT, 0));
            item.addElement(padding);
            addControlFor(value, item);
        }

        elements.add(list);
    }

    public void addControlFor(WorkspaceConfigs.ConfigInfo.ValueWrapperLocalised value, ElementList.Item item)
    {
        Field field = value.value.field;
        field.setAccessible(true);
        String fieldName = field.getName();
        Class clz = field.getType();
        Prop props; // should always exist
        if(field.isAnnotationPresent(Prop.class))
        {
            props = field.getAnnotation(Prop.class);
        }
        else
        {
            props = ConfigBase.class.getDeclaredFields()[0].getAnnotation(Prop.class);
        }

        //TODO hidden properties???
        Object o;
        try
        {
            o = field.get(value.value.parent);
        }
        catch(IllegalAccessException e)
        {
            return;
        }

        if(clz == int.class)
        {
            ElementNumberInput input = new ElementNumberInput(item, false);
            input.setMin(props.min() == Double.MIN_VALUE ? Integer.MIN_VALUE : (int)props.min());
            input.setMax(props.max() == Double.MAX_VALUE ? Integer.MAX_VALUE : (int)props.max());
            input.setDefaultText(o.toString());
            input.setSize(80, 14);
            input.setConstraint(new Constraint(input).top(item, Constraint.Property.Type.TOP, 3).bottom(item, Constraint.Property.Type.BOTTOM, 3).right(item, Constraint.Property.Type.RIGHT, 8));
            item.addElement(input);
        }
        else if(clz == double.class)
        {
            ElementNumberInput input = new ElementNumberInput(item, true);
            input.setMin(props.min());
            input.setMax(props.max());
            input.setMaxDec(2);
            input.setDefaultText(o.toString());
            input.setSize(80, 14);
            input.setConstraint(new Constraint(input).top(item, Constraint.Property.Type.TOP, 3).bottom(item, Constraint.Property.Type.BOTTOM, 3).right(item, Constraint.Property.Type.RIGHT, 8));
            item.addElement(input);
        }
        else if(clz == boolean.class)
        {
            ElementToggle toggle = new ElementToggleTextable(item, value.name).setToggled((boolean)o);
            toggle.setSize(80, 14);
            toggle.setConstraint(new Constraint(toggle).top(item, Constraint.Property.Type.TOP, 3).bottom(item, Constraint.Property.Type.BOTTOM, 3).right(item, Constraint.Property.Type.RIGHT, 8));
            item.addElement(toggle);
        }
        else if(clz == String.class)
        {
            ElementTextField input = new ElementTextField(item);
            input.setDefaultText(o.toString());
            input.setSize(80, 14);
            input.setConstraint(new Constraint(input).top(item, Constraint.Property.Type.TOP, 3).bottom(item, Constraint.Property.Type.BOTTOM, 3).right(item, Constraint.Property.Type.RIGHT, 8));
            item.addElement(input);
        }
        else if(clz.isEnum()) //enum! //TODO this
        {
            ElementContextMenu input = new ElementContextMenu(item, o.toString(), Arrays.asList(clz.getEnumConstants()), (menu, listItem) -> {});
            input.setSize(80, 14);
            input.setConstraint(new Constraint(input).top(item, Constraint.Property.Type.TOP, 3).bottom(item, Constraint.Property.Type.BOTTOM, 3).right(item, Constraint.Property.Type.RIGHT, 8));
            item.addElement(input);
        }
        else if(o instanceof List) //lists
        {
            StringBuilder sb = new StringBuilder();
            List list = (List)o;
            for(int i = 0; i < list.size(); i++)
            {
                Object o1 = list.get(i);
                sb.append(o1);
                if(i < list.size() - 1)
                {
                    sb.append("\n");
                }
            }
            ElementButtonTooltip button = new ElementButtonTooltip(item, I18n.format("selectWorld.edit"), sb.toString());
            button.setSize(80, 14);
            button.setConstraint(new Constraint(button).top(item, Constraint.Property.Type.TOP, 3).bottom(item, Constraint.Property.Type.BOTTOM, 3).right(item, Constraint.Property.Type.RIGHT, 8));
            item.addElement(button);
        }
    }

}
