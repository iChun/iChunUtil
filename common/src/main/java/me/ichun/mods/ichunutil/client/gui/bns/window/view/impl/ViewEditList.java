package me.ichun.mods.ichunutil.client.gui.bns.window.view.impl;

import me.ichun.mods.ichunutil.client.gui.bns.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.Window;
import me.ichun.mods.ichunutil.client.gui.bns.window.WindowGeneric;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementButton;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementList;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementScrollBar;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementTextField;
import net.minecraft.client.Minecraft;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ViewEditList<W extends Window<?,?>, I> extends View<W>
{
    public final List<I> objectList;
    public final Predicate<String> validatorFinal;
    public final Consumer<ElementList<ViewEditList<W, I>, I>> responder;

    public ViewEditList(@NotNull W parent, @NotNull String s, @NotNull List<I> objectList, @NotNull Predicate<String> validator, @NotNull Consumer<ElementList<ViewEditList<W, I>, I>> responder)
    {
        this(parent, s, objectList, validator, responder, null);
    }

    public ViewEditList(@NotNull W parent, @NotNull String s, @NotNull List<I> objectList, @NotNull Predicate<String> validator, @NotNull Consumer<ElementList<ViewEditList<W, I>, I>> responder, @Nullable BiFunction<String, Integer, FormattedCharSequence> textFormatter)
    {
        super(parent, s);
        this.objectList = objectList;
        this.validatorFinal = validator;
        this.responder = responder;

        ElementScrollBar sv = new ElementScrollBar(this, ElementScrollBar.Orientation.VERTICAL, 0.6F);
        sv.setConstraint(new Constraint(sv).top(this, Constraint.Property.Type.TOP, 0)
            .bottom(this, Constraint.Property.Type.BOTTOM, 30)
            .right(this, Constraint.Property.Type.RIGHT, 0)
        );
        elements.add(sv);

        ElementList<ViewEditList<W, I>, I> list = new ElementList<>(this);
        list.setScrollVertical(sv)
            .setDragHandler((i, j) -> {})
            .setRearrangeHandler((i, j) -> {})
            ;
        list.setConstraint(new Constraint(list).left(this, Constraint.Property.Type.LEFT, 0)
            .bottom(this, Constraint.Property.Type.BOTTOM, 30)
            .top(this, Constraint.Property.Type.TOP, 0)
            .right(sv, Constraint.Property.Type.LEFT, 0)
        );

        ElementButton btn = new ElementButton(this, "gui.cancel", (button, mouseX, mouseY) -> {
            parent.parent.setFocused(null);
            parent.parent.removeWindow(parent);
        });
        btn.setSize(60, 20);
        btn.setConstraint(new Constraint(btn).right(this, Constraint.Property.Type.RIGHT, 10)
            .bottom(this, Constraint.Property.Type.BOTTOM, 5)
        );
        elements.add(btn);

        ElementButton btn1 = new ElementButton(this, "gui.done", (button, mouseX, mouseY) -> {
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
            List<ElementList.Item<I>> items = list.items;
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
                    List<ElementList.Item<I>> oriItems = new ArrayList<>(list.items);
                    list.items.clear();
                    for(ElementList.Item<I> oriItem : oriItems)
                    {
                        ElementTextField oriText = (ElementTextField)oriItem.elements.get(0);
                        if(!oriText.getText().isEmpty())
                        {
                            String ori = oriText.getText();
                            ElementList.Item<I> item = list.addItem(oriItem.getObject());
                            setTextField(item, ori, oriText.getValidator(), oriText.getResponder(), textFormatter).init();
                            anyResponder = oriText.getResponder();
                        }
                    }
                    if(anyResponder != null)
                    {
                        ElementList.Item<I> item = list.addItem(null);
                        setTextField(item, "", validatorFinal, anyResponder, textFormatter).init();
                    }
                }
                else if(!((ElementTextField)list.items.get(list.items.size() - 1).elements.get(0)).getText().isEmpty()) //if the last field is not empty, add another empty field
                {
                    if(anyResponder != null)
                    {
                        ElementList.Item<I> item = list.addItem(null);
                        setTextField(item, "", validatorFinal, anyResponder, textFormatter).init();
                    }
                }
                list.resize(Minecraft.getInstance(), list.getParentWidth(), list.getParentHeight());
            }
        };

        //Add our objects
        for(I o1 : objectList)
        {
            String ori = o1.toString();
            ElementList.Item<I> item = list.addItem(o1);
            setTextField(item, ori, validator, sharedResponder, textFormatter);
        }

        //Add a new empty line
        ElementList.Item<I> item = list.addItem((I)null);
        setTextField(item, "", validator, sharedResponder, textFormatter);
        elements.add(list);
    }

    private ElementTextField setTextField(ElementList.Item<I> item, String text, Predicate<String> validator, Consumer<String> responder, BiFunction<String, Integer, FormattedCharSequence> textFormatter)
    {
        ElementTextField textField = new ElementTextField(item);
        textField.setDefaultText(text);
        textField.setValidator(validator);
        textField.setResponder(responder);
        if(textFormatter != null)
        {
            textField.setTextFormatter(textFormatter);
        }
        textField.setConstraint(Constraint.matchParent(textField, item, item.getBorderSize()));
        item.addElement(textField);
        return textField;
    }


    @Override
    public void setWindowGenericProperties(WindowGeneric<?, ?> window)
    {
        window.disableDockingEntirely();
    }

}
