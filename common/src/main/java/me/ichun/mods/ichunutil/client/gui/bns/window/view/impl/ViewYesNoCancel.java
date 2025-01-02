package me.ichun.mods.ichunutil.client.gui.bns.window.view.impl;

import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.Window;
import me.ichun.mods.ichunutil.client.gui.bns.window.WindowGeneric;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementButton;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementTextWrapper;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ViewYesNoCancel extends View<Window<? extends Workspace,? extends View<?>>>
{
    public ViewYesNoCancel(@NotNull Window<? extends Workspace,? extends View<?>> parent, String title, String text1, @Nullable Consumer<Workspace> callbackYes, @Nullable Consumer<Workspace> callbackNo, @Nullable Consumer<Workspace> callbackCancel)
    {
        super(parent, title);

        ElementTextWrapper text = new ElementTextWrapper(this);
        text.setNoWrap().setText(text1);
        text.setConstraint(new Constraint(text).top(this, Constraint.Property.Type.TOP, 20).bottom(this, Constraint.Property.Type.BOTTOM, 40));
        elements.add(text);

        ElementButton button = new ElementButton(this, I18n.get("gui.cancel"), (btn, mouseX, mouseY) -> {
            parent.parent.removeWindow(parent);

            if(callbackCancel != null)
            {
                callbackCancel.accept(parent.parent);
            }
        });
        button.setSize(60, 20);
        button.setConstraint(new Constraint(button).bottom(this, Constraint.Property.Type.BOTTOM, 10).right(this, Constraint.Property.Type.RIGHT, 10));
        elements.add(button);

        ElementButton button1 = new ElementButton(this, I18n.get("gui.no"), (btn, mouseX, mouseY) -> {
            parent.parent.removeWindow(parent);

            if(callbackNo != null)
            {
                callbackNo.accept(parent.parent);
            }
        });
        button1.setSize(60, 20);
        button1.setConstraint(new Constraint(button1).right(button, Constraint.Property.Type.LEFT, 10));
        elements.add(button1);

        ElementButton button2 = new ElementButton(this, I18n.get("gui.yes"), (btn, mouseX, mouseY) -> {
            parent.parent.removeWindow(parent);

            if(callbackYes != null)
            {
                callbackYes.accept(parent.parent);
            }
        });
        button2.setSize(60, 20);
        button2.setConstraint(new Constraint(button2).right(button1, Constraint.Property.Type.LEFT, 10));
        elements.add(button2);
    }

    @Override
    public void setWindowGenericProperties(WindowGeneric<?, ?> window)
    {
        window.disableDockingEntirely();

        window.isNotUnique();
    }

    public static <W extends Workspace> void popup(W parent, double widthRatio, double heightRatio, String text, @Nullable Consumer<W> callbackYes, @Nullable Consumer<W> callbackNo, @Nullable Consumer<W> callbackCancel)
    {
        popup(parent, widthRatio, heightRatio, "window.popup.title", text, callbackYes, callbackNo, callbackCancel);
    }

    @SuppressWarnings("unchecked")
    public static <W extends Workspace> void popup(W parent, double widthRatio, double heightRatio, String title, String text, @Nullable Consumer<W> callbackYes, @Nullable Consumer<W> callbackNo, @Nullable Consumer<W> callbackCancel)
    {
        parent.openWindowInCenter(WindowGeneric.create(parent, windowGeneric -> new ViewYesNoCancel(windowGeneric, title, text, (Consumer<Workspace>)callbackYes, (Consumer<Workspace>)callbackNo, (Consumer<Workspace>)callbackCancel)), widthRatio, heightRatio, true);
    }
}
