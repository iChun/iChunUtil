package me.ichun.mods.ichunutil.client.gui.bns.window;

import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementButton;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementTextWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.function.Consumer;

public class WindowPopup extends Window<Workspace>
{
    //title will be localised, text won't.
    public WindowPopup(Workspace parent, String title, Consumer<Workspace> callback, String...text)
    {
        super(parent);

        setView(new ViewPopup(this, title, callback, text));
        disableDocking();
        disableDockStacking();
        disableUndocking();
    }

    public static class ViewPopup extends View<WindowPopup>
    {
        public ViewPopup(@Nonnull WindowPopup parent, String title, Consumer<Workspace> callback, String...text1)
        {
            super(parent, title);

            ElementTextWrapper text = new ElementTextWrapper(this);
            if(text1.length == 1)
            {
                text.setNoWrap().setText(text1[0]);
            }
            else
            {
                text.setNoWrap().setText(Arrays.asList(text1));
            }
            text.setConstraint(new Constraint(text).top(this, Constraint.Property.Type.TOP, 20).bottom(this, Constraint.Property.Type.BOTTOM, 40));
            elements.add(text);

            ElementButton<?> button = new ElementButton<>(this, I18n.format("gui.ok"), elementClickable ->
            {
                parent.parent.removeWindow(parent);

                if(callback != null)
                {
                    callback.accept(parent.parent);
                }
            });
            button.setSize(60, 20);
            button.setConstraint(new Constraint(button).bottom(this, Constraint.Property.Type.BOTTOM, 20));
            elements.add(button);
        }
    }

    public static void popup(Workspace parent, double widthRatio, double heightRatio, Consumer<Workspace> callback, String...text)
    {
        popup(parent, widthRatio, heightRatio, "window.popup.title", callback, text);
    }

    public static void popup(Workspace parent, double widthRatio, double heightRatio, String title, Consumer<Workspace> callback, String...text)
    {
        parent.openWindowInCenter(new WindowPopup(parent, title, callback, text), widthRatio, heightRatio);
    }
}
