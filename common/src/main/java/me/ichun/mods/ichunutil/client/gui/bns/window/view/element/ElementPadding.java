package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.Fragment;
import org.jetbrains.annotations.NotNull;

public class ElementPadding extends Element
{
    public int minWidth;
    public int minHeight;

    public ElementPadding(@NotNull Fragment parent, int minWidth, int minHeight)
    {
        super(parent);
        this.minWidth = minWidth;
        this.minHeight = minHeight;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        return false;
    }

    @Override
    public boolean changeFocus(boolean direction) //we can't change focus on this
    {
        return false;
    }

    @Override
    public int getMinWidth()
    {
        return minWidth;
    }

    @Override
    public int getMinHeight()
    {
        return minHeight;
    }

}
