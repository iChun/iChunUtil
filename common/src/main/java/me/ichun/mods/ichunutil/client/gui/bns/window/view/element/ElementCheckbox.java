package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.client.gui.bns.Fragment;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ElementCheckbox<T extends ElementCheckbox> extends ElementClickable<T>
{
    public boolean toggleState;

    public ElementCheckbox(@NotNull Fragment parent, String tooltip, Consumer<T> callback)
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
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.render(graphics, mouseX, mouseY, partialTick);
        PoseStack stack = graphics.pose();
        if(renderMinecraftStyle() > 0)
        {
            renderMinecraftStyleButton(stack, getLeft(), getTop(), width, height, disabled || (parent.isDragging() && parent.getFocused() == this) ? ButtonState.CLICK : (hover ? ButtonState.HOVER : ButtonState.IDLE));
        }
        else
        {
            fill(graphics, getTheme().elementButtonBorder, 0);
            int[] colour;
            if(disabled)
            {
                colour = getTheme().elementButtonBackgroundInactive;
            }
            else if(parent.isDragging() && parent.getFocused() == this)
            {
                colour = getTheme().elementButtonClick;
            }
            else if(hover)
            {
                colour = getTheme().elementButtonToggleHover;
            }
            else
            {
                colour = getTheme().elementButtonBackgroundActive;
            }
            fill(graphics, colour, 1);
        }
        if(toggleState)
        {
            drawString(graphics, "X", getLeft() + 2, getTop() + 1);
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
