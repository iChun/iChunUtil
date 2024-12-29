package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.Fragment;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.NotNull;

public class ElementToggle extends ElementToggleAbstract<ElementToggle>
{
    public String text;
    public boolean toggleState;

    public ElementToggle(@NotNull Fragment<?> parent, @NotNull String s, TriConsumer<ElementToggle, Double, Double> callback, TriConsumer<ElementToggle, Double, Double> rightMouseCallback)
    {
        super(parent, s, callback, rightMouseCallback);
    }

    public ElementToggle(@NotNull Fragment<?> parent, @NotNull String s, TriConsumer<ElementToggle, Double, Double> callback)
    {
        this(parent, s, callback, null);
    }
}
