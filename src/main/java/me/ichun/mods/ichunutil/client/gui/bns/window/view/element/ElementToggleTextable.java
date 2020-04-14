package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class ElementToggleTextable<T extends ElementToggle> extends ElementToggle<T>
{
    public @Nonnull String offString;
    public @Nonnull String onString;

    public ElementToggleTextable(@Nonnull Fragment parent, @Nonnull String s, Consumer<T> callback)
    {
        this(parent, s, "gui.no", "gui.yes", callback);
    }

    public ElementToggleTextable(@Nonnull Fragment parent, @Nonnull String s, @Nonnull String off, @Nonnull String on, Consumer<T> callback)
    {
        super(parent, s, callback);
        this.tooltip = s;
        this.offString = I18n.format(off);
        this.onString = I18n.format(on);
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
