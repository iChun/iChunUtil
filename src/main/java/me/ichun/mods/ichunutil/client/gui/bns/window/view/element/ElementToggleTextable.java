package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class ElementToggleTextable<T extends ElementToggleTextable> extends ElementToggle<T>
{
    public @Nonnull String offString;
    public @Nonnull String onString;

    public ElementToggleTextable(@Nonnull Fragment parent, @Nonnull String tooltip, Consumer<T> callback)
    {
        this(parent, tooltip, "gui.no", "gui.yes", callback);
    }

    public ElementToggleTextable(@Nonnull Fragment parent, @Nonnull String tooltip, @Nonnull String off, @Nonnull String on, Consumer<T> callback)
    {
        super(parent, tooltip, callback);
        this.tooltip = tooltip;
        this.offString = I18n.format(off);
        this.onString = I18n.format(on);
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
