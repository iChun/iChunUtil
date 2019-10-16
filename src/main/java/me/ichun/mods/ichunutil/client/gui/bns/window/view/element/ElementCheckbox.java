package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.render.RenderHelper;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ElementCheckbox extends ElementClickable<View>
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
            bindTexture(Fragment.VANILLA_WIDGETS);

            //min width = 15
            //draw middle if required
            //draw ends (15 each)
            //46 = clicked
            //66 = idle
            //86 = hover
            int yOffset = (parentFragment.isDragging() && parentFragment.getFocused() == this) ? 0 : (hover ? 2 : 1);

            RenderHelper.draw(getLeft(), getBottom() - 5, 5, 5, 0, 0D/256D, 5D/256D, (61 + yOffset * 20)/256D, (66 + yOffset * 20)/256D); //draw bottomLeft
            RenderHelper.draw(getLeft(), getTop(), 5, 5, 0, 0D/256D, 5D/256D, (46 + yOffset * 20)/256D, (51 + yOffset * 20)/256D); //draw topLeft
            RenderHelper.draw(getRight() - 5, getTop(), 5, 5, 0, 195D/256D, 200D/256D, (46 + yOffset * 20)/256D, (51 + yOffset * 20)/256D); //draw topRight
            RenderHelper.draw(getRight() - 5, getBottom() - 5, 5, 5, 0, 195D/256D, 200D/256D, (61 + yOffset * 20)/256D, (66 + yOffset * 20)/256D); //draw topRight
        }
        else
        {
            RenderHelper.drawColour(getTheme().elementButtonBorder[0], getTheme().elementButtonBorder[1], getTheme().elementButtonBorder[2], 255, getLeft(), getTop(), width, height, 0);
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
            RenderHelper.drawColour(colour[0], colour[1], colour[2], 255, getLeft() + 1, getTop() + 1, width - 2, height - 2, 0);
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
    public Supplier<Integer> getMinWidth()
    {
        return () -> 9;
    }

    @Override
    public Supplier<Integer> getMinHeight()
    {
        return this.getMinWidth();
    }

    @Override
    public Supplier<Integer> getMaxWidth()
    {
        return this.getMinWidth();
    }

    @Override
    public Supplier<Integer> getMaxHeight()
    {
        return this.getMinWidth();
    }
}
