package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.Theme;
import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class ElementToggle extends ElementClickable<Fragment>
{
    public String text;
    public boolean toggleState;

    public ElementToggle(@Nonnull Fragment parent, @Nonnull String s)
    {
        this(parent, s, (button) -> {});
    }

    public ElementToggle(@Nonnull Fragment parent, @Nonnull String s, Consumer<ElementClickable> callback)
    {
        super(parent, callback);
        text = s;
    }

    public <T extends ElementToggle> T setToggled(boolean flag)
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
            if(s.equals(text))
            {
                setTooltip(null);
            }
            else
            {
                setTooltip(text);
            }

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

    @Override
    public void onClickRelease()
    {
        toggleState = !toggleState;
    }

    @Override
    public int getMinWidth()
    {
        return 15;
    }

    @Override
    public int getMinHeight()
    {
        return 14;
    }
}
