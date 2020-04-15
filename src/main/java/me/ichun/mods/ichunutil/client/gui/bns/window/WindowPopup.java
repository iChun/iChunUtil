package me.ichun.mods.ichunutil.client.gui.bns.window;

import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementButton;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementTextWrapper;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class WindowPopup extends Window<Workspace>
{
    //title will be localised, text won't.
    public WindowPopup(Workspace parent, String title, String text, Consumer<Workspace> callback)
    {
        super(parent);

        setView(new ViewPopup(this, title, text, callback));
        disableDocking();
        disableDockStacking();
        disableUndocking();
    }

    public static class ViewPopup extends View<WindowPopup>
    {
        public ViewPopup(@Nonnull WindowPopup parent, String title, String text1, Consumer<Workspace> callback)
        {
            super(parent, title);

            ElementTextWrapper text = new ElementTextWrapper(this);
            text.setNoWrap().setText(text1);
            text.setConstraint(new Constraint(text).top(this, Constraint.Property.Type.TOP, 20));
            elements.add(text);

            ElementButton<?> button = new ElementButton<>(this, I18n.format("gui.ok"), elementClickable ->
            {
                if(callback != null)
                {
                    callback.accept(parent.parent);
                }

                parent.parent.setFocused(null);
                parent.parent.removeWindow(parent);
            });
            button.setSize(60, 20);
            button.setConstraint(new Constraint(button).bottom(this, Constraint.Property.Type.BOTTOM, 20));
            elements.add(button);
        }
    }

    public static void popup(Workspace parent, double widthRatio, double heightRatio, String text, Consumer<Workspace> callback)
    {
        popup(parent, widthRatio, heightRatio, "window.popup.title", text, callback);
    }

    public static void popup(Workspace parent, double widthRatio, double heightRatio, String title, String text, Consumer<Workspace> callback)
    {
        parent.openWindowInCenter(new WindowPopup(parent, title, text, callback), widthRatio, heightRatio);
    }
}
