package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;

import javax.annotation.Nonnull;

public class ElementButtonTooltip extends ElementButton
{
    public ElementButtonTooltip(@Nonnull View parent, String s, String tooltip)
    {
        super(parent, s);
        this.tooltip = tooltip;
    }

    @Override
    public void setTooltip(String s){}

}
