package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.Fragment;
import net.minecraft.client.resources.language.I18n;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.NotNull;

public class ElementToggleTextable extends ElementToggleAbstract<ElementToggleTextable>
{
    public @NotNull String offString;
    public @NotNull String onString;

    public ElementToggleTextable(@NotNull Fragment<?> parent, @NotNull String tooltip, @NotNull String off, @NotNull String on, TriConsumer<ElementToggleTextable, Double, Double> callback)
    {
        super(parent, tooltip, callback);
        this.tooltip = tooltip;
        this.offString = I18n.get(off);
        this.onString = I18n.get(on);
        this.text = offString;
    }

    public ElementToggleTextable(@NotNull Fragment<?> parent, @NotNull String tooltip, TriConsumer<ElementToggleTextable, Double, Double> callback)
    {
        this(parent, tooltip, "gui.no", "gui.yes", callback);
    }

    @Override
    public ElementToggleTextable setToggled(boolean flag)
    {
        toggleState = flag;
        text = toggleState ? onString : offString;
        return this;
    }

    @Override
    public void onClickRelease()
    {
        super.onClickRelease();
        text = toggleState ? onString : offString;
    }
}
