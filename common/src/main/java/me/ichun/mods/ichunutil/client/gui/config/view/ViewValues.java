package me.ichun.mods.ichunutil.client.gui.config.view;

import me.ichun.mods.ichunutil.client.gui.bns.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.WindowGeneric;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.*;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.impl.ViewEditList;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.impl.ViewPopup;
import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import me.ichun.mods.ichunutil.client.key.KeyBind;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.config.annotations.Prop;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class ViewValues extends View<WindowGeneric<WorkspaceConfigs, ViewValues>>
{
    public final TreeSet<ConfigBase> configs;
    public final ElementList<?> list;

    public ViewValues(@NotNull WindowGeneric<WorkspaceConfigs, ViewValues> parent, @NotNull String s, TreeSet<ConfigBase> configs)
    {
        super(parent, s);

        this.configs = configs;

        ElementToggle<?> lastRotatable = null;
        int i = 0;
        for(ConfigBase config : configs)
        {
            ElementToggle<?> rotatable = new ElementToggle<>(this, config.getConfigType().toString(), b -> {
                if(b.toggleState)
                {
                    elements.stream().filter(element -> "configType".equals(element.id)).forEach(e -> ((ElementToggle<?>)e).toggleState = false);
                    b.toggleState = true;

                    populateList();
                }
                else
                {
                    b.toggleState = true;
                }
            });
            rotatable.setSize(60, 14).setId("configType");
            rotatable.setConstraint(new Constraint(rotatable).left(lastRotatable != null ? lastRotatable : this, lastRotatable != null ? Constraint.Property.Type.RIGHT : Constraint.Property.Type.LEFT, 0).top(this, Constraint.Property.Type.TOP, 0));
            if(i == 0)
            {
                rotatable.setToggled(true);
            }
            elements.add(rotatable);
            lastRotatable = rotatable;
            i++;
        }

        ElementScrollBar<?> sv = new ElementScrollBar<>(this, ElementScrollBar.Orientation.VERTICAL, 0.6F);
        sv.setConstraint(new Constraint(sv).top(lastRotatable, Constraint.Property.Type.BOTTOM, 0).bottom(this, Constraint.Property.Type.BOTTOM, 0).right(this, Constraint.Property.Type.RIGHT, 0));
        elements.add(sv);

        this.list = new ElementList<>(this).setScrollVertical(sv);
        list.setConstraint(new Constraint(list).left(this, Constraint.Property.Type.LEFT, 0).bottom(this, Constraint.Property.Type.BOTTOM, 0).top(lastRotatable, Constraint.Property.Type.BOTTOM, 0).right(sv, Constraint.Property.Type.LEFT, 0));

        populateList();

        elements.add(list);
    }

    public void populateList()
    {
        ConfigBase config = getCurrentConfig();
        if(config != null)
        {
            list.setFocused(null);
            list.items.clear();

            if(config.getConfigType().equals(ConfigBase.Type.SERVER) && !(getMinecraft().player != null && iChunUtil.d().getServer().isSingleplayer() && iChunUtil.d().getServer().getPlayerList().getPlayerCount() <= 1)) //Trying to edit a SERVER config in a non-singerplayer world environment.
            {
                ViewPopup.popup(parent.parent, 0.6D, 140, null, I18n.get("gui.ichunutil.configs.noEditingServerConfig"));
                return;
            }

            for(ConfigBase.Category category : config.categories)
            {
                if(!category.showInGui)
                {
                    continue;
                }

                ElementList.Item<?> item = list.addItem(category).setBorderSize(0);
                ElementTextWrapper wrapper = new ElementTextWrapper(item).setText(ChatFormatting.YELLOW + config.getLocalisedName(category, false));
                wrapper.setConstraint(new Constraint(wrapper).left(item, Constraint.Property.Type.LEFT, 3).right(item, Constraint.Property.Type.RIGHT, 90));
                String desc = config.getLocalisedName(category, true);
                wrapper.setTooltip(desc);
                item.setTooltip(desc);
                item.addElement(wrapper);
                //Padding gives height to the list
                ElementPadding padding = new ElementPadding(item, 0, 20);
                padding.setConstraint(new Constraint(padding).right(item, Constraint.Property.Type.RIGHT, 0));
                item.addElement(padding);

                for(ConfigBase.Category.Entry entry : category.getEntries())
                {
                    item = list.addItem(entry).setBorderSize(0);
                    item.setSelectionHandler(itemObj -> {
                        if(itemObj.selected)
                        {
                            itemObj.selected = false;

                            Element<?> e = getControlElement(itemObj);
                            if(e != null)
                            {
                                e.parent.setFocused(e);
                                if(e instanceof ElementTextField text)
                                {
                                    text.focus();
                                }
                                else
                                {
                                    e.mouseClicked(e.getLeft() + e.getWidth() / 2D, e.getTop() + e.getHeight() / 2D, 0);
                                    e.mouseReleased(e.getLeft() + e.getWidth() / 2D, e.getTop() + e.getHeight() / 2D, 0);
                                }
                            }

                            save(); // prompts a restart button and also lets us save every time a config is changed.
                        }
                    });
                    wrapper = new ElementTextWrapper(item).setText(config.getLocalisedName(entry, false));
                    wrapper.setConstraint(new Constraint(wrapper).left(item, Constraint.Property.Type.LEFT, 8).right(item, Constraint.Property.Type.RIGHT, 90));
                    desc = config.getLocalisedName(entry, true);
                    wrapper.setTooltip(desc);
                    item.setTooltip(desc);
                    item.addElement(wrapper);
                    //Padding gives height to the list
                    padding = new ElementPadding(item, 0, 20);
                    padding.setConstraint(new Constraint(padding).right(item, Constraint.Property.Type.RIGHT, 0));
                    item.addElement(padding);
                    addControlFor(config, entry, item);
                }
            }

            list.init();
        }
    }

    public ConfigBase getCurrentConfig()
    {
        for(ConfigBase config : configs)
        {
            Optional<Element<?>> any = elements.stream().filter(element -> "configType".equals(element.id) && element instanceof ElementToggle<?> toggle && toggle.toggleState && toggle.text.equals(config.getConfigType().toString())).findAny();
            if(any.isPresent())
            {
                return config;
            }
        }
        return null;
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

    public void save()
    {
        ConfigBase config = getCurrentConfig();

        boolean isDirty = false;
        for(ElementList.Item<?> item : list.items)
        {
            Element<?> e = getControlElement(item);
            if(e != null) // we have the control element
            {
                ConfigBase.Category.Entry e1 = (ConfigBase.Category.Entry)item.getObject();

                Field field = e1.field;
                field.setAccessible(true);
                Class clz = field.getType();
                Object o;
                try
                {
                    o = field.get(config);

                    if(clz == int.class && e instanceof ElementNumberInput)
                    {
                        field.set(config, ((ElementNumberInput)e).getInt());
                    }
                    else if(clz == double.class && e instanceof ElementNumberInput)
                    {
                        field.set(config, ((ElementNumberInput)e).getDouble());
                    }
                    else if(clz == boolean.class && e instanceof ElementToggleTextable)
                    {
                        field.set(config, ((ElementToggle<?>)e).toggleState);
                    }
                    else if(clz == String.class && e instanceof ElementTextField)
                    {
                        field.set(config, ((ElementTextField)e).getText());
                    }
                    else if(clz.isEnum() && e instanceof ElementDropdownContextMenu) //enum!
                    {
                        Object[] enums = clz.getEnumConstants();
                        for(Object en : enums)
                        {
                            if(en.toString().equals(((ElementDropdownContextMenu<?>)e).text))
                            {
                                field.set(config, en);
                                break;
                            }
                        }
                    }
                    else if(o instanceof List) //lists
                    {
                        //List should have already been set by the editor.
                    }
                    if(o != field.get(config))
                    {
                        if(e1.prop.needsRestart())
                        {
                            parent.parent.viewConfigs.createRestartAlertButton();
                        }
                        isDirty = true;

                        config.onPropertyChanged(false, field.getName(), field, o, field.get(config));
                    }
                }
                catch(IllegalAccessException ex)
                {
                    iChunUtil.LOGGER.error("Error accessing field {} for config {} of type {}", field.getName(), config.getConfigName(), config.getConfigType(), ex);
                }
            }
        }
        if(isDirty)
        {
            config.save();
        }
    }

    @SuppressWarnings("all")
    public void addControlFor(ConfigBase config, final ConfigBase.Category.Entry entry, ElementList.Item<?> item)
    {
        Field field = entry.field;
        Class clz = field.getType();
        Prop props = entry.prop;

        Object o;
        try
        {
            o = field.get(config);
        }
        catch(IllegalAccessException e)
        {
            return;
        }

        boolean handled = false;
        if(!props.guiElementOverride().isEmpty())
        {
            BiFunction<ConfigBase.Category.Entry, ElementList.Item<?>, Boolean> entryGuiOverride = config.guiElementOverrides.get(props.guiElementOverride());
            if(entryGuiOverride != null && entryGuiOverride.apply(entry, item))
            {
                handled = true;
            }
        }
        if(!handled)
        {
            String entryName = config.getLocalisedName(entry, false);
            if(clz == int.class)
            {
                ElementNumberInput input = new ElementNumberInput(item, false);
                input.setMin(props.min() == -Double.MAX_VALUE ? Integer.MIN_VALUE : (int)props.min());
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
                ElementToggleTextable<?> toggle = new ElementToggleTextable<>(item, entryName, elementClickable -> {
                }).setToggled((boolean)o);
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
                ElementDropdownContextMenu<?> input = new ElementDropdownContextMenu<>(item, o.toString(), Arrays.asList(clz.getEnumConstants()), (menu, listItem) -> {
                    if(listItem.selected)
                    {
                        ElementDropdownContextMenu<?> contextMenu = (ElementDropdownContextMenu<?>)menu;
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
                ElementButton<?> button = new ElementButton<>(item, "selectWorld.edit", btn -> {
                    entry.field.setAccessible(true);
                    Type typefield = entry.field.getGenericType();
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
                                if(!(props.values().length == 1 && props.values()[0].isEmpty()))
                                {
                                    String[] values = props.values();
                                    validator = validator.and(s -> {
                                        for(String value1 : values)
                                        {
                                            if(value1.startsWith(s))
                                            {
                                                return true;
                                            }
                                        }
                                        return false;
                                    });
                                }

                                Predicate<String> finalValidator = validator;
                                WindowGeneric<WorkspaceConfigs, ViewEditList<WindowGeneric<?, ?>>> window = WindowGeneric.create(parent.parent, windowGeneric -> new ViewEditList<>(windowGeneric, entryName, list, finalValidator, list1 -> {
                                    try
                                    {
                                        List listToUse = list;
                                        if(list instanceof ArrayList)
                                        {
                                            listToUse = (List)((ArrayList)list).clone();
                                        }
                                        listToUse.clear();
                                        for(ElementList.Item<?> item1 : list1.items)
                                        {
                                            ElementTextField oriText = (ElementTextField)item1.elements.get(0);
                                            if(!oriText.getText().isEmpty())
                                            {
                                                if(types[0] == String.class)
                                                {
                                                    listToUse.add(oriText.getText());
                                                }
                                                else if(types[0] == Double.class)
                                                {
                                                    listToUse.add(Double.parseDouble(oriText.getText()));
                                                }
                                                else if(types[0] == Integer.class)
                                                {
                                                    listToUse.add(Integer.parseInt(oriText.getText()));
                                                }
                                            }
                                        }
                                        entry.field.set(config, listToUse);
                                        if(entry.prop.needsRestart())
                                        {
                                            parent.parent.viewConfigs.createRestartAlertButton();
                                        }
                                        config.save();
                                    }
                                    catch(IllegalAccessException ignored)
                                    {
                                    }
                                }));
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
            else if(clz == KeyBind.class)
            {
                WorkspaceConfigs.createButtonToKeyBinds(entry, item);
            }
        }
    }

    @Override
    public void setWindowGenericProperties(WindowGeneric<?, ?> window)
    {
        window.pos(20, 20);
        window.size(200, 300);
        window.disableUndocking();
        window.disableDrag();
        window.disableBringToFront();
    }
}
