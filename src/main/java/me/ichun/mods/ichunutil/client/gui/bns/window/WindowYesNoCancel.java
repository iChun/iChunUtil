package me.ichun.mods.ichunutil.client.gui.bns.window;

import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementButton;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementTextWrapper;
import net.minecraft.client.resources.language.I18n;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class WindowYesNoCancel extends Window<Workspace>
{
    //title will be localised, text won't.
    public WindowYesNoCancel(Workspace parent, String title, String text, Consumer<Workspace> callbackYes, Consumer<Workspace> callbackNo, Consumer<Workspace> callbackCancel)
    {
        super(parent);

        setView(new ViewConfirmation(this, title, text, callbackYes, callbackNo, callbackCancel));
        disableDockingEntirely();

        isNotUnique();
    }

    public static class ViewConfirmation extends View<WindowYesNoCancel>
    {
        public ViewConfirmation(@Nonnull WindowYesNoCancel parent, String title, String text1, Consumer<Workspace> callbackYes, Consumer<Workspace> callbackNo, Consumer<Workspace> callbackCancel)
        {
            super(parent, title);

            ElementTextWrapper text = new ElementTextWrapper(this);
            text.setNoWrap().setText(text1);
            text.setConstraint(new Constraint(text).top(this, Constraint.Property.Type.TOP, 20).bottom(this, Constraint.Property.Type.BOTTOM, 40));
            elements.add(text);

            ElementButton<?> button = new ElementButton<>(this, I18n.get("gui.cancel"), button3 ->
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

            ElementButton<?> button1 = new ElementButton<>(this, I18n.get("gui.no"), button3 ->
            {
                parent.parent.removeWindow(parent);

                if(callbackNo != null)
                {
                    callbackNo.accept(parent.parent);
                }
            });
            button1.setSize(60, 20);
            button1.setConstraint(new Constraint(button1).right(button, Constraint.Property.Type.LEFT, 10));
            elements.add(button1);

            ElementButton<?> button2 = new ElementButton<>(this, I18n.get("gui.yes"), button3 ->
            {
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
    }

    public static void popup(Workspace parent, double widthRatio, double heightRatio, String text, Consumer<Workspace> callbackYes, Consumer<Workspace> callbackNo, Consumer<Workspace> callbackCancel)
    {
        popup(parent, widthRatio, heightRatio, "window.popup.title", text, callbackYes, callbackNo, callbackCancel);
    }

    public static void popup(Workspace parent, double widthRatio, double heightRatio, String title, String text, Consumer<Workspace> callbackYes, Consumer<Workspace> callbackNo, Consumer<Workspace> callbackCancel)
    {
        parent.openWindowInCenter(new WindowYesNoCancel(parent, title, text, callbackYes, callbackNo, callbackCancel), widthRatio, heightRatio, true);
    }
}
