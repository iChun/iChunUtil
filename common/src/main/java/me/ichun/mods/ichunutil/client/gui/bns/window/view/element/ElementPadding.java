package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.Fragment;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import org.jetbrains.annotations.NotNull;

public class ElementPadding extends Element<Fragment<?>>
{
    public int minWidth;
    public int minHeight;

    public ElementPadding(@NotNull Fragment<?> parent, int minWidth, int minHeight)
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
    public ComponentPath nextFocusPath(FocusNavigationEvent event)
    {
        return null;
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
