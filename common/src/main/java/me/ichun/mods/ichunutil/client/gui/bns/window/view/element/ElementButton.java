package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.Fragment;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElementButton extends ElementButtonAbstract<ElementButton>
{
    public ElementButton(@NotNull Fragment<?> parent, String s, TriConsumer<ElementButton, Double, Double> callback, @Nullable TriConsumer<ElementButton, Double, Double> rightMouseCallback)
    {
        super(parent, s, callback, rightMouseCallback);
    }

    public ElementButton(@NotNull Fragment<?> parent, String s, TriConsumer<ElementButton, Double, Double> callback)
    {
        this(parent, s, callback, null);
    }
}
