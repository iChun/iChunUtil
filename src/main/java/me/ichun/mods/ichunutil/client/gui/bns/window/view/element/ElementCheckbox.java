package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class ElementCheckbox extends ElementClickable<Fragment>
{
    public boolean toggleState;

    public ElementCheckbox(@Nonnull View parent, String tooltip, Consumer<ElementClickable> callback)
    {
        super(parent, callback);
        this.tooltip = tooltip;
    }

    public ElementCheckbox setToggled(boolean flag)
    {
        toggleState = flag;
        return this;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick)
    {
        super.render(mouseX, mouseY, partialTick);
        if(renderMinecraftStyle())
        {
            renderMinecraftStyleButton(getLeft(), getTop(), width, height, (parentFragment.isDragging() && parentFragment.getFocused() == this) ? ButtonState.CLICK : (hover ? ButtonState.HOVER : ButtonState.IDLE));
        }
        else
        {
            fill(getTheme().elementButtonBorder, 0);
            int[] colour;
            if(parentFragment.isDragging() && parentFragment.getFocused() == this)
            {
                colour = getTheme().elementButtonClick;
            }
            else if(hover)
            {
                colour = getTheme().elementButtonToggleHover;
            }
            else
            {
                colour = getTheme().elementButtonBackgroundInactive;
            }
            fill(colour, 1);
        }
        if(toggleState)
        {
            drawString("X", getLeft() + 2, getTop() + 1);
        }
    }

    @Override
    public void onClickRelease()
    {
        toggleState = !toggleState;
    }

    @Override
    public int getMinWidth()
    {
        return 9;
    }

    @Override
    public int getMinHeight()
    {
        return this.getMinWidth();
    }

    @Override
    public int getMaxWidth()
    {
        return this.getMinWidth();
    }

    @Override
    public int getMaxHeight()
    {
        return this.getMinWidth();
    }
}
