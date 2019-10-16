package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.render.RenderHelper;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ElementButton extends ElementClickable<View>
{
    public @Nonnull String text; //already localized

    public ElementButton(@Nonnull View parent, String s)
    {
        this(parent, s, (button) -> {});
    }

    public ElementButton(@Nonnull View parent, String s, Consumer<ElementClickable> callback)
    {
        super(parent, callback);
        text = s;
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
            int yOffset = parentFragment.isDragging() && parentFragment.getFocused() == this ? 0 : hover ? 2 : 1;

            int i = width - 28;
            int x = getLeft() + 14;
            while(i > 0)
            {
                int dist = Math.min(i, 172);
                RenderHelper.draw(x, getTop(), dist, 20, 0, 14D / 256D, (14 + dist) / 256D, (46 + yOffset * 20) / 256D, (66 + yOffset * 20) / 256D); //draw body
                i -= dist;
                x += dist;
            }

            RenderHelper.draw(getLeft(), getTop(), 14, 20, 0, 0D/256D, 14D/256D, (46 + yOffset * 20)/256D, (66 + yOffset * 20)/256D); //draw leftblock
            RenderHelper.draw(getRight() - 14, getTop(), 14, 20, 0, 186D/256D, 200D/256D, (46 + yOffset * 20)/256D, (66 + yOffset * 20)/256D); //draw leftblock
        }
        else
        {
            RenderHelper.drawColour(getTheme().elementButtonBorder[0], getTheme().elementButtonBorder[1], getTheme().elementButtonBorder[2], 255, getLeft(), getTop(), width, height, 0);
            int[] colour = parentFragment.isDragging() && parentFragment.getFocused() == this ? getTheme().elementButtonClick : hover ? getTheme().elementButtonBackgroundHover : getTheme().elementButtonBackgroundInactive;
            RenderHelper.drawColour(colour[0], colour[1], colour[2], 255, getLeft() + 1, getTop() + 1, width - 2, height - 2, 0);
        }
        if(!text.isEmpty())
        {
            String s = reString(text, width - 4);
            if(s.equals(text))
            {
                setTooltip(null);
            }
            else
            {
                setTooltip(text);
            }
            drawString(s, getLeft() + (this.width - getFontRenderer().getStringWidth(s)) / 2F, getTop() + (height - getFontRenderer().FONT_HEIGHT) / 2F);
        }
    }

    @Override
    public void onClickRelease() {} //we don't do anything, we're a static button

    @Override
    public Supplier<Integer> getMinWidth()
    {
        return () -> 15;
    }

    @Override
    public Supplier<Integer> getMinHeight()
    {
        return () -> renderMinecraftStyle() ? 20 : 16;
    }

    @Override
    public Supplier<Integer> getMaxHeight()
    {
        return () -> renderMinecraftStyle() ? 20 : 10000;
    }
}
