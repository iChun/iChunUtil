package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;

import javax.annotation.Nonnull;

public class ElementButtonTooltip extends ElementButton
{
    public ElementButtonTooltip(@Nonnull Fragment<?> parent, String s, String tooltip)
    {
        super(parent, s);
        this.tooltip = tooltip;
    }

    @Override
    public <T extends Element<?>> T setTooltip(String s) //do nothing
    {
        return (T)this;
    }
}
