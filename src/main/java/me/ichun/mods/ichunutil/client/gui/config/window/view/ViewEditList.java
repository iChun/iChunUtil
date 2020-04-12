package me.ichun.mods.ichunutil.client.gui.config.window.view;

import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementButton;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementList;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementScrollBar;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementTextField;
import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import me.ichun.mods.ichunutil.client.gui.config.window.WindowEditList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nonnull;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ViewEditList extends View<WindowEditList<?>>
{
    public final WorkspaceConfigs.ConfigInfo.ValueWrapperLocalised valueWrapper;
    public final ElementList list;

    public ViewEditList(@Nonnull WindowEditList<?> parent, @Nonnull String s, WorkspaceConfigs.ConfigInfo.ValueWrapperLocalised valueWrapper)
    {
        super(parent, s);
        this.valueWrapper = valueWrapper;

        ElementScrollBar sv = new ElementScrollBar(this, ElementScrollBar.Orientation.VERTICAL, 0.6F);
        sv.setConstraint(new Constraint(sv).top(this, Constraint.Property.Type.TOP, 0)
                .bottom(this, Constraint.Property.Type.BOTTOM, 30)
                .right(this, Constraint.Property.Type.RIGHT, 0)
        );
        elements.add(sv);

        list = new ElementList(this).setScrollVertical(sv)
                .setDragHandler((i, j) -> {})
                .setRearrangeHandler((i, j) -> {})
                ;
        list.setConstraint(new Constraint(list).left(this, Constraint.Property.Type.LEFT, 0)
                .bottom(this, Constraint.Property.Type.BOTTOM, 30)
                .top(this, Constraint.Property.Type.TOP, 0)
                .right(sv, Constraint.Property.Type.LEFT, 0)
        );

        ElementButton btn = new ElementButton(this, I18n.format("gui.done"), button -> {
            try
            {
                valueWrapper.value.field.setAccessible(true);
                Object o = valueWrapper.value.field.get(valueWrapper.value.parent);
                if(o instanceof List)
                {
                    Type typefield = valueWrapper.value.field.getGenericType();
                    if(typefield instanceof ParameterizedType)
                    {
                        ParameterizedType type = (ParameterizedType)typefield;
                        Type[] types = type.getActualTypeArguments();

                        if(types.length == 1)
                        {
                            List list1 = (List)o;
                            list1.clear();
                            for(ElementList.Item<?> item : list.items)
                            {
                                ElementTextField oriText = (ElementTextField)item.elements.get(0);
                                if(!oriText.getText().isEmpty())
                                {
                                    if(types[0] == String.class)
                                    {
                                        list1.add(oriText.getText());
                                    }
                                    else if(types[0] == Double.class)
                                    {
                                        list1.add(Double.parseDouble(oriText.getText()));
                                    }
                                    else if(types[0] == Integer.class)
                                    {
                                        list1.add(Integer.parseInt(oriText.getText()));
                                    }
                                }
                            }
                            valueWrapper.value.field.set(valueWrapper.value.parent, list1);
                            valueWrapper.value.parent.save();
                        }
                    }
                }
            }
            catch(IllegalAccessException ignored){}
            getWorkspace().setFocused(null);
            getWorkspace().removeWindow(parent);
        });
        btn.setWidth(60);
        btn.setHeight(20);
        btn.setConstraint(new Constraint(btn).right(this, Constraint.Property.Type.RIGHT, 10)
                .bottom(this, Constraint.Property.Type.BOTTOM, 5)
        );
        elements.add(btn);

        try
        {
            valueWrapper.value.field.setAccessible(true);
            Object o = valueWrapper.value.field.get(valueWrapper.value.parent);
            if(o instanceof List)
            {
                Type typefield = valueWrapper.value.field.getGenericType();
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
                            final Predicate<String> validatorFinal = validator;
                            Consumer<String> sharedResponder = (str) -> { // share a responder for all the text fields
                                Consumer<String> anyResponder = null;
                                boolean needRefresh = false;
                                List<ElementList.Item<?>> items = list.items;
                                for(int i = 0; i < items.size(); i++)
                                {
                                    ElementList.Item<?> item = items.get(i);
                                    ElementTextField oriText = (ElementTextField)item.elements.get(0);
                                    if(oriText.getResponder() != null)
                                    {
                                        anyResponder = oriText.getResponder();
                                    }
                                    if((oriText.getText().isEmpty()) != (i == items.size() - 1))
                                    {
                                        needRefresh = true;
                                    }
                                }

                                if(needRefresh)
                                {
                                    if(str.isEmpty()) //if the text changed is empty, remove all the fields and only add the ones with text. And one empty field
                                    {
                                        list.setFocused(null);
                                        List<ElementList.Item<?>> oriItems = new ArrayList<>(list.items);
                                        list.items.clear();
                                        for(ElementList.Item<?> oriItem : oriItems)
                                        {
                                            ElementTextField oriText = (ElementTextField)oriItem.elements.get(0);
                                            if(!oriText.getText().isEmpty())
                                            {
                                                String ori = oriText.getText();
                                                ElementList.Item<?> item = list.addItem(ori);
                                                ElementTextField textField = new ElementTextField(item);
                                                textField.setDefaultText(ori);
                                                textField.setValidator(oriText.getValidator());
                                                textField.setResponder(oriText.getResponder());
                                                textField.setConstraint(Constraint.matchParent(textField, item, item.getBorderSize()));
                                                item.addElement(textField);
                                                textField.init();
                                                anyResponder = oriText.getResponder();
                                            }
                                        }
                                        if(anyResponder != null)
                                        {
                                            ElementList.Item<?> item = list.addItem("");
                                            ElementTextField textField = new ElementTextField(item);
                                            textField.setDefaultText("");
                                            textField.setValidator(validatorFinal);
                                            textField.setResponder(anyResponder);
                                            textField.setConstraint(Constraint.matchParent(textField, item, item.getBorderSize()));
                                            item.addElement(textField);
                                            textField.init();
                                        }
                                    }
                                    else if(!((ElementTextField)list.items.get(list.items.size() - 1).elements.get(0)).getText().isEmpty()) //if the last field is not empty, add another empty field
                                    {
                                        if(anyResponder != null)
                                        {
                                            ElementList.Item<?> item = list.addItem("");
                                            ElementTextField textField = new ElementTextField(item);
                                            textField.setDefaultText("");
                                            textField.setValidator(validatorFinal);
                                            textField.setResponder(anyResponder);
                                            textField.setConstraint(Constraint.matchParent(textField, item, item.getBorderSize()));
                                            item.addElement(textField);
                                            textField.init();
                                        }
                                    }
                                    list.resize(Minecraft.getInstance(), list.getParentWidth(), list.getParentHeight());
                                }
                            };
                            List list1 = (List)o;
                            for(Object o1 : list1)
                            {
                                String ori = o1.toString();
                                ElementList.Item<?> item = list.addItem(ori);
                                ElementTextField textField = new ElementTextField(item);
                                textField.setDefaultText(ori);
                                textField.setValidator(validator);
                                textField.setResponder(sharedResponder);
                                textField.setConstraint(Constraint.matchParent(textField, item, item.getBorderSize()));
                                item.addElement(textField);
                            }
                            ElementList.Item<?> item = list.addItem("");
                            ElementTextField textField = new ElementTextField(item);
                            textField.setDefaultText("");
                            textField.setValidator(validator);
                            textField.setResponder(sharedResponder);
                            textField.setConstraint(Constraint.matchParent(textField, item, item.getBorderSize()));
                            item.addElement(textField);
                        }
                    }
                }
            }
        }
        catch(IllegalAccessException ignored){}
        elements.add(list);
    }
}
