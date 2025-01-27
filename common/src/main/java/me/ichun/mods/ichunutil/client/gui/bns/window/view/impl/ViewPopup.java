package me.ichun.mods.ichunutil.client.gui.bns.window.view.impl;

import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.Window;
import me.ichun.mods.ichunutil.client.gui.bns.window.WindowGeneric;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementButton;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.ElementTextWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Consumer;

public class ViewPopup extends View<Window<?,?>>
{
    public <W extends Workspace<W>> ViewPopup(@NotNull Window<W,?> parent, String title, @Nullable Consumer<W> callback, String... text)
    {
        super(parent, title);

        ElementTextWrapper text1 = new ElementTextWrapper(this);
        if(text.length == 1)
        {
            text1.setText(text[0]);
        }
        else
        {
            text1.setText(Arrays.asList(text));
        }
        text1.setConstraint(new Constraint(text1).top(this, Constraint.Property.Type.TOP, 20).bottom(this, Constraint.Property.Type.BOTTOM, 40));
        elements.add(text1);

        ElementButton button = new ElementButton(this, I18n.get("gui.ok"), (btn, mouseX, mouseY) -> {
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

    @Override
    public void init()
    {
        super.init();

        ElementTextWrapper text = ((ElementTextWrapper)elements.getFirst());
        text.setWidth(Math.min(text.longestLine + 5, (int)(this.width * 0.9D)));
        text.init();
    }

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        super.resize(mc, width, height);

        ElementTextWrapper text = ((ElementTextWrapper)elements.getFirst());
        text.setWidth(Math.min(text.longestLine + 5, (int)(this.width * 0.9D)));
        text.init();
    }

    @Override
    public void setWindowGenericProperties(WindowGeneric<?, ?> window)
    {
        window.disableDocking();
        window.disableDockStacking();
        window.disableUndocking();

        window.isNotUnique();
    }


    public static <W extends Workspace<W>> void popup(W parent, double widthRatio, double heightRatio, @Nullable Consumer<W> callback, String...text)
    {
        popup(parent, widthRatio, heightRatio, "window.popup.title", callback, text);
    }

    public static <W extends Workspace<W>> void popup(W parent, double widthRatio, double heightRatio, String title, @Nullable Consumer<W> callback, String...text)
    {
        parent.openWindowInCenter(WindowGeneric.create(parent, windowGeneric -> new ViewPopup(windowGeneric, title, callback, text)), widthRatio, heightRatio, true);
    }
}
