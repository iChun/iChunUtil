package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.Fragment;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ElementToggleTextable<T extends ElementToggleTextable> extends ElementToggle<T>
{
    public @NotNull String offString;
    public @NotNull String onString;

    public ElementToggleTextable(@NotNull Fragment parent, @NotNull String tooltip, Consumer<T> callback)
    {
        this(parent, tooltip, "gui.no", "gui.yes", callback);
    }

    public ElementToggleTextable(@NotNull Fragment parent, @NotNull String tooltip, @NotNull String off, @NotNull String on, Consumer<T> callback)
    {
        super(parent, tooltip, callback);
        this.tooltip = tooltip;
        this.offString = I18n.get(off);
        this.onString = I18n.get(on);
        this.text = offString;
    }

    @Override
    public <T extends ElementToggle<?>> T setToggled(boolean flag)
    {
        toggleState = flag;
        text = toggleState ? onString : offString;
        return (T)this;
    }

    @Override
    public void onClickRelease()
    {
        super.onClickRelease();
        text = toggleState ? onString : offString;
    }
}
