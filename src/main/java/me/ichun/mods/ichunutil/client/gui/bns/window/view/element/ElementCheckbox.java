package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class ElementCheckbox<T extends ElementCheckbox> extends ElementClickable<T>
{
    public boolean toggleState;

    public ElementCheckbox(@Nonnull Fragment parent, String tooltip, Consumer<T> callback)
    {
        super(parent, callback);
        this.tooltip = tooltip;
    }

    public ElementCheckbox<T> setToggled(boolean flag)
    {
        toggleState = flag;
        return this;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTick)
    {
        super.render(stack, mouseX, mouseY, partialTick);
        if(renderMinecraftStyle() > 0)
        {
            renderMinecraftStyleButton(stack, getLeft(), getTop(), width, height, disabled || (parentFragment.isDragging() && parentFragment.getFocused() == this) ? ButtonState.CLICK : (hover ? ButtonState.HOVER : ButtonState.IDLE), renderMinecraftStyle());
        }
        else
        {
            fill(stack, getTheme().elementButtonBorder, 0);
            int[] colour;
            if(disabled)
            {
                colour = getTheme().elementButtonBackgroundInactive;
            }
            else if(parentFragment.isDragging() && parentFragment.getFocused() == this)
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
            fill(stack, colour, 1);
        }
        if(toggleState)
        {
            drawString(stack, "X", getLeft() + 2, getTop() + 1);
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
