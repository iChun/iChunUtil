package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.Theme;
import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ElementToggle<T extends ElementToggle> extends ElementClickable<T>
{
    public String text;
    public boolean toggleState;

    public ElementToggle(@Nonnull Fragment parent, @Nonnull String s, Consumer<T> callback)
    {
        super(parent, callback);
        text = I18n.format(s);
    }

    public <T extends ElementToggle<?>> T setToggled(boolean flag)
    {
        toggleState = flag;
        return (T)this;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick)
    {
        super.render(mouseX, mouseY, partialTick);
        if(renderMinecraftStyle())
        {
            renderMinecraftStyleButton(getLeft(), getTop(), width, height, parentFragment.isDragging() && parentFragment.getFocused() == this ? ButtonState.CLICK : hover ? ButtonState.HOVER : ButtonState.IDLE);
        }
        else
        {
            fill(getTheme().elementButtonBorder, 0);
            int[] colour;
            if(parentFragment.isDragging() && parentFragment.getFocused() == this)
            {
                colour = getTheme().elementButtonClick;
            }
            else if(toggleState && hover)
            {
                colour = getTheme().elementButtonToggleHover;
            }
            else if(hover)
            {
                colour = getTheme().elementButtonBackgroundHover;
            }
            else if(toggleState)
            {
                colour = getTheme().elementButtonToggle;
            }
            else
            {
                colour = getTheme().elementButtonBackgroundInactive;
            }
            fill(colour, 1);
        }
        renderText();
    }

    public void renderText()
    {
        if(!text.isEmpty())
        {
            String s = reString(text, width - 4);

            //draw the text
            if(renderMinecraftStyle())
            {
                getFontRenderer().drawStringWithShadow(s, getLeft() + (this.width - getFontRenderer().getStringWidth(s)) / 2F, getTop() + (height - getFontRenderer().FONT_HEIGHT) / 2F + 1, getMinecraftFontColour());
            }
            else
            {
                getFontRenderer().drawString(s, getLeft() + (this.width - getFontRenderer().getStringWidth(s)) / 2F, getTop() + (height - getFontRenderer().FONT_HEIGHT) / 2F + 1, Theme.getAsHex(toggleState ? getTheme().font : getTheme().fontDim));
            }
        }
    }

    @Nullable
    @Override
    public String tooltip(double mouseX, double mouseY)
    {
        if(!text.isEmpty())
        {
            String tooltip = super.tooltip(mouseX, mouseY);
            if(tooltip != null)
            {
                return text + " - " + tooltip;
            }
            return text;
        }
        return super.tooltip(mouseX, mouseY);
    }

    @Override
    public void onClickRelease()
    {
        toggleState = !toggleState;
    }

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
