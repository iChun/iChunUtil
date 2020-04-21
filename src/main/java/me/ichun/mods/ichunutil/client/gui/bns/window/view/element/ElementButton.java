package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ElementButton<T extends ElementButton> extends ElementClickable<T>
{
    public @Nonnull String text;
    public boolean renderBackground = true;

    public ElementButton(@Nonnull Fragment parent, String s, Consumer<T> callback)
    {
        super(parent, callback);
        text = I18n.format(s);
    }

    public ElementButton<T> disableBackground()
    {
        renderBackground = false;
        return this;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick)
    {
        super.render(mouseX, mouseY, partialTick);
        if(renderBackground)
        {
            if(renderMinecraftStyle())
            {
                renderMinecraftStyleButton(getLeft(), getTop(), width, height, parentFragment.isDragging() && parentFragment.getFocused() == this ? ButtonState.CLICK : hover ? ButtonState.HOVER : ButtonState.IDLE);
            }
            else
            {
                fill(getTheme().elementButtonBorder, 0);
                int[] colour = parentFragment.isDragging() && parentFragment.getFocused() == this ? getTheme().elementButtonClick : hover ? getTheme().elementButtonBackgroundHover : getTheme().elementButtonBackgroundInactive;
                fill(colour, 1);
            }
        }
        if(!text.isEmpty())
        {
            String s = reString(text, width - 4);
            drawString(s, getLeft() + (this.width - getFontRenderer().getStringWidth(s)) / 2F, getTop() + (height - getFontRenderer().FONT_HEIGHT) / 2F + 1);
        }
    }

    @Nullable
    @Override
    public String tooltip(double mouseX, double mouseY)
    {
        if(!text.isEmpty())
        {
            String s = reString(text, width - 4);
            if(!s.equals(text))
            {
                String tooltip = super.tooltip(mouseX, mouseY);
                if(tooltip != null)
                {
                    return text + " - " + tooltip;
                }
                return text;
            }
        }
        return super.tooltip(mouseX, mouseY);
    }

    @Override
    public void onClickRelease() {} //we don't do anything, we're a static button

    @Override
    public int getMinWidth()
    {
        return 14;
    }

    @Override
    public int getMinHeight()
    {
        return 14;
    }
}
