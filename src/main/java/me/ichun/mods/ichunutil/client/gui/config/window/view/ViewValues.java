package me.ichun.mods.ichunutil.client.gui.config.window.view;

import me.ichun.mods.ichunutil.client.gui.bns.window.WindowEditList;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.*;
import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import me.ichun.mods.ichunutil.client.gui.config.window.WindowValues;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.config.annotations.Prop;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Predicate;

public class ViewValues extends View<WindowValues>
{
    public final WorkspaceConfigs.ConfigInfo info;
    public final String category;
    public final TreeSet<WorkspaceConfigs.ConfigInfo.ValueWrapperLocalised> values;
    public final ElementList<?> list;

    public ViewValues(@Nonnull WindowValues parent, @Nonnull String s, WorkspaceConfigs.ConfigInfo info, String category, TreeSet<WorkspaceConfigs.ConfigInfo.ValueWrapperLocalised> values)
    {
        super(parent, s);
        this.info = info;
        this.category = category;
        this.values = values;

        ElementScrollBar<?> sv = new ElementScrollBar<>(this, ElementScrollBar.Orientation.VERTICAL, 0.6F);
        sv.setConstraint(new Constraint(sv).top(this, Constraint.Property.Type.TOP, 0)
                .bottom(this, Constraint.Property.Type.BOTTOM, 0)
                .right(this, Constraint.Property.Type.RIGHT, 0)
        );
        elements.add(sv);

        this.list = new ElementList<>(this).setScrollVertical(sv)
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
            ElementList.Item<?> item = list.addItem(value).setBorderSize(0);
            item.setSelectionHandler(itemObj -> {
                if(itemObj.selected)
                {
                    Element<?> e = getControlElement(itemObj);
                    if(e != null)
                    {
                        e.parentFragment.setFocused(e);
                        e.mouseClicked(e.getLeft() + e.getWidth() / 2D, e.getTop() + e.getHeight() / 2D, 0);
                        e.mouseReleased(e.getLeft() + e.getWidth() / 2D, e.getTop() + e.getHeight() / 2D, 0);
                    }
                }
            });
            ElementTextWrapper wrapper = new ElementTextWrapper(item).setText(value.name);
            wrapper.setConstraint(new Constraint(wrapper).left(item, Constraint.Property.Type.LEFT, 3).right(item, Constraint.Property.Type.RIGHT, 90));
            wrapper.setTooltip(value.desc);
            item.addElement(wrapper);
            ElementPadding padding = new ElementPadding(item, 0, 20);
            padding.setConstraint(new Constraint(padding).right(item, Constraint.Property.Type.RIGHT, 0));
            item.addElement(padding);
            addControlFor(value, item);
        }

        elements.add(list);
    }

    public Element<?> getControlElement(ElementList.Item<?> item)
    {
        for(Element<?> element : item.elements)
        {
            if(element instanceof ElementTextWrapper || element instanceof ElementPadding)
            {
                continue;
            }
            return element;
        }
        return null;
    }

    public void addControlFor(final WorkspaceConfigs.ConfigInfo.ValueWrapperLocalised value, ElementList.Item<?> item)
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
            ElementToggleTextable<?> toggle = new ElementToggleTextable<>(item, value.name, elementClickable -> {}).setToggled((boolean)o);
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
        else if(clz.isEnum()) //enum!
        {
            ElementContextMenu<?> input = new ElementContextMenu<>(item, o.toString(), Arrays.asList(clz.getEnumConstants()), (menu, listItem) ->
            {
                if(listItem.selected)
                {
                    ElementContextMenu<?> contextMenu = (ElementContextMenu<?>)menu;
                    contextMenu.text = listItem.getObject().toString();
                }
            });
            input.setSize(80, 14);
            input.setConstraint(new Constraint(input).top(item, Constraint.Property.Type.TOP, 3).bottom(item, Constraint.Property.Type.BOTTOM, 3).right(item, Constraint.Property.Type.RIGHT, 8));
            item.addElement(input);
        }
        else if(o instanceof List) //lists
        {
            StringBuilder sb = new StringBuilder();
            final List list = (List)o;
            for(int i = 0; i < list.size(); i++)
            {
                Object o1 = list.get(i);
                sb.append(o1);
                if(i < list.size() - 1)
                {
                    sb.append("\n");
                }
            }
            ElementButton<?> button = new ElementButton<>(item, "selectWorld.edit", btn ->
            {
                value.value.field.setAccessible(true);
                Type typefield = value.value.field.getGenericType();
                if(typefield instanceof ParameterizedType)
                {
                    ParameterizedType type = (ParameterizedType)typefield;
                    Type[] types = type.getActualTypeArguments();

                    if(types.length == 1)
                    {
                        Predicate<String> validator = null; //get which kind of validator we should use
                        if(types[0] == String.class)
                        {
                            validator = str -> true;
                        }
                        else if(types[0] == Double.class)
                        {
                            validator = ElementTextField.NUMBERS;
                        }
                        else if(types[0] == Integer.class)
                        {
                            validator = ElementTextField.INTEGERS;
                        }

                        if(validator != null)
                        {
                            WindowEditList<?> window = new WindowEditList<>(getWorkspace(), value.name, list, validator, list1 -> {
                                try
                                {
                                    list.clear();
                                    for(ElementList.Item<?> item1 : list1.items)
                                    {
                                        ElementTextField oriText = (ElementTextField)item1.elements.get(0);
                                        if(!oriText.getText().isEmpty())
                                        {
                                            if(types[0] == String.class)
                                            {
                                                list.add(oriText.getText());
                                            }
                                            else if(types[0] == Double.class)
                                            {
                                                list.add(Double.parseDouble(oriText.getText()));
                                            }
                                            else if(types[0] == Integer.class)
                                            {
                                                list.add(Integer.parseInt(oriText.getText()));
                                            }
                                        }
                                    }
                                    value.value.field.set(value.value.parent, list1);
                                    value.value.parent.save();
                                }
                                catch(IllegalAccessException ignored){}
                            });
                            getWorkspace().openWindowInCenter(window, 0.6D, 0.8D);
                            window.init();//reinit cause we're using lists and they're weird
                        }
                    }
                }
            });
            button.setTooltip(sb.toString());
            button.setSize(80, 14);
            button.setConstraint(new Constraint(button).top(item, Constraint.Property.Type.TOP, 3).bottom(item, Constraint.Property.Type.BOTTOM, 3).right(item, Constraint.Property.Type.RIGHT, 8));
            item.addElement(button);
        }
    }

}
