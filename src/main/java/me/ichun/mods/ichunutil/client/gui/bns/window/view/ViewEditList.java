package me.ichun.mods.ichunutil.client.gui.bns.window.view;

import me.ichun.mods.ichunutil.client.gui.bns.window.WindowEditList;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementButton;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementList;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementScrollBar;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementTextField;
import net.minecraft.client.Minecraft;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ViewEditList extends View<WindowEditList<?>>
{
    public final List<?> objectList;
    public final Predicate<String> validatorFinal;
    public final Consumer<ElementList<?>> responder;

    public ViewEditList(@Nonnull WindowEditList<?> parent, @Nonnull String s, @Nonnull List<?> objectList, @Nonnull Predicate<String> validator, @Nonnull Consumer<ElementList<?>> responder)
    {
        this(parent, s, objectList, validator, responder, null);
    }

    public ViewEditList(@Nonnull WindowEditList<?> parent, @Nonnull String s, @Nonnull List<?> objectList, @Nonnull Predicate<String> validator, @Nonnull Consumer<ElementList<?>> responder, @Nullable BiFunction<String, Integer, FormattedCharSequence> textFormatter)
    {
        super(parent, s);
        this.objectList = objectList;
        this.validatorFinal = validator;
        this.responder = responder;

        ElementScrollBar<?> sv = new ElementScrollBar<>(this, ElementScrollBar.Orientation.VERTICAL, 0.6F);
        sv.setConstraint(new Constraint(sv).top(this, Constraint.Property.Type.TOP, 0)
                .bottom(this, Constraint.Property.Type.BOTTOM, 30)
                .right(this, Constraint.Property.Type.RIGHT, 0)
        );
        elements.add(sv);

        ElementList<?> list = new ElementList<>(this).setScrollVertical(sv)
                .setDragHandler((i, j) -> {})
                .setRearrangeHandler((i, j) -> {})
                ;
        list.setConstraint(new Constraint(list).left(this, Constraint.Property.Type.LEFT, 0)
                .bottom(this, Constraint.Property.Type.BOTTOM, 30)
                .top(this, Constraint.Property.Type.TOP, 0)
                .right(sv, Constraint.Property.Type.LEFT, 0)
        );

        ElementButton<?> btn = new ElementButton<>(this, "gui.cancel", button -> {
            parent.parent.setFocused(null);
            parent.parent.removeWindow(parent);
        });
        btn.setSize(60, 20);
        btn.setConstraint(new Constraint(btn).right(this, Constraint.Property.Type.RIGHT, 10)
                .bottom(this, Constraint.Property.Type.BOTTOM, 5)
        );
        elements.add(btn);

        ElementButton<?> btn1 = new ElementButton<>(this, "gui.done", button -> {
            responder.accept(list);
            parent.parent.setFocused(null);
            parent.parent.removeWindow(parent);
        });
        btn1.setSize(60, 20);
        btn1.setConstraint(new Constraint(btn1).right(btn, Constraint.Property.Type.LEFT, 10)
                .bottom(this, Constraint.Property.Type.BOTTOM, 5)
        );
        elements.add(btn1);

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
                            if(textFormatter != null)
                            {
                                textField.setTextFormatter(textFormatter);
                            }
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
                        if(textFormatter != null)
                        {
                            textField.setTextFormatter(textFormatter);
                        }
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
                        if(textFormatter != null)
                        {
                            textField.setTextFormatter(textFormatter);
                        }
                        textField.setConstraint(Constraint.matchParent(textField, item, item.getBorderSize()));
                        item.addElement(textField);
                        textField.init();
                    }
                }
                list.resize(Minecraft.getInstance(), list.getParentWidth(), list.getParentHeight());
            }
        };
        for(Object o1 : objectList)
        {
            String ori = o1.toString();
            ElementList.Item<?> item = list.addItem(ori);
            ElementTextField textField = new ElementTextField(item);
            textField.setDefaultText(ori);
            textField.setValidator(validator);
            textField.setResponder(sharedResponder);
            if(textFormatter != null)
            {
                textField.setTextFormatter(textFormatter);
            }
            textField.setConstraint(Constraint.matchParent(textField, item, item.getBorderSize()));
            item.addElement(textField);
        }
        ElementList.Item<?> item = list.addItem("");
        ElementTextField textField = new ElementTextField(item);
        textField.setDefaultText("");
        textField.setValidator(validator);
        textField.setResponder(sharedResponder);
        if(textFormatter != null)
        {
            textField.setTextFormatter(textFormatter);
        }
        textField.setConstraint(Constraint.matchParent(textField, item, item.getBorderSize()));
        item.addElement(textField);
        elements.add(list);
    }
}
