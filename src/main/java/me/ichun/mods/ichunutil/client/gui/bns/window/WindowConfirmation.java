package me.ichun.mods.ichunutil.client.gui.bns.window;

import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementButton;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementTextWrapper;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class WindowConfirmation extends Window<Workspace>
{
    //title will be localised, text won't.
    public WindowConfirmation(Workspace parent, String title, String text, Consumer<Workspace> callbackOk, Consumer<Workspace> callbackCancel)
    {
        super(parent);

        setView(new ViewConfirmation(this, title, text, callbackOk, callbackCancel));
        disableDockingEntirely();

        isNotUnique();
    }

    public static class ViewConfirmation extends View<WindowConfirmation>
    {
        public ViewConfirmation(@Nonnull WindowConfirmation parent, String title, String text1, Consumer<Workspace> callbackOk, Consumer<Workspace> callbackCancel)
        {
            super(parent, title);

            ElementTextWrapper text = new ElementTextWrapper(this);
            text.setNoWrap().setText(text1);
            text.setConstraint(new Constraint(text).top(this, Constraint.Property.Type.TOP, 20).bottom(this, Constraint.Property.Type.BOTTOM, 40));
            elements.add(text);

            ElementButton<?> button = new ElementButton<>(this, I18n.format("gui.cancel"), btn ->
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

            ElementButton<?> button1 = new ElementButton<>(this, I18n.format("gui.ok"), btn ->
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
    }

    public static void popup(Workspace parent, double widthRatio, double heightRatio, String text, Consumer<Workspace> callbackOk, Consumer<Workspace> callbackCancel)
    {
        popup(parent, widthRatio, heightRatio, "window.popup.title", text, callbackOk, callbackCancel);
    }

    public static void popup(Workspace parent, double widthRatio, double heightRatio, String title, String text, Consumer<Workspace> callbackOk, Consumer<Workspace> callbackCancel)
    {
        parent.openWindowInCenter(new WindowConfirmation(parent, title, text, callbackOk, callbackCancel), widthRatio, heightRatio, true);
    }
}
