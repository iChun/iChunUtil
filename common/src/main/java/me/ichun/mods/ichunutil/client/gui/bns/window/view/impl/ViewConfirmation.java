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

public class ViewConfirmation extends View<Window<? extends Workspace,? extends View<?>>>
{
    //title will be localised, text won't.
    public ViewConfirmation(@NotNull Window<? extends Workspace,? extends View<?>> parent, String title, String text1, @Nullable Consumer<Workspace> callbackOk, @Nullable Consumer<Workspace> callbackCancel, boolean isYesNo)
    {
        super(parent, title);

        ElementTextWrapper text = new ElementTextWrapper(this);
        text.setNoWrap().setText(text1);
        text.setConstraint(new Constraint(text).top(this, Constraint.Property.Type.TOP, 20).bottom(this, Constraint.Property.Type.BOTTOM, 40));
        elements.add(text);

        ElementButton button = new ElementButton(this, I18n.get(isYesNo ? "gui.no" : "gui.cancel"), (btn, mouseX, mouseY) ->
        {
            parent.parent.removeWindow(parent);

            if(callbackCancel != null)
            {
                callbackCancel.accept(parent.parent);
            }
        });
        button.setSize(60, 20);
        button.setConstraint(new Constraint(button).bottom(this, Constraint.Property.Type.BOTTOM, 10).right(this, Constraint.Property.Type.RIGHT, 10));
        elements.add(button);

        ElementButton button1 = new ElementButton(this, I18n.get(isYesNo ? "gui.yes" : "gui.ok"), (btn, mouseX, mouseY) ->
        {
            parent.parent.removeWindow(parent);

            if(callbackOk != null)
            {
                callbackOk.accept(parent.parent);
            }
        });
        button1.setSize(60, 20);
        button1.setConstraint(new Constraint(button1).right(button, Constraint.Property.Type.LEFT, 10));
        elements.add(button1);
    }

    @Override
    public void setWindowGenericProperties(WindowGeneric<?, ?> window)
    {
        window.disableDockingEntirely();

        window.isNotUnique();
    }

    public static <W extends Workspace> void popup(W parent, double widthRatio, double heightRatio, String text, @Nullable Consumer<W> callbackOk, @Nullable Consumer<W> callbackCancel)
    {
        popup(parent, widthRatio, heightRatio, "window.popup.title", text, callbackOk, callbackCancel);
    }

    public static <W extends Workspace> void popup(W parent, double widthRatio, double heightRatio, String title, String text, @Nullable Consumer<W> callbackOk, @Nullable Consumer<W> callbackCancel)
    {
        popup(parent, widthRatio, heightRatio, title, text, callbackOk, callbackCancel, true);
    }

    @SuppressWarnings("unchecked")
    public static <W extends Workspace> void popup(W parent, double widthRatio, double heightRatio, String title, String text, @Nullable Consumer<W> callbackOk, @Nullable Consumer<W> callbackCancel, boolean isYesNo)
    {
        parent.openWindowInCenter(WindowGeneric.create(parent, windowGeneric -> new ViewConfirmation(windowGeneric, title, text, (Consumer<Workspace>)callbackOk, (Consumer<Workspace>)callbackCancel, isYesNo)), widthRatio, heightRatio, true);
    }
}
