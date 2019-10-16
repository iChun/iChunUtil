package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.Theme;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nonnull;

public class ButtonElement extends Element<View>
{
    public String text; //localizable

    public ButtonElement(@Nonnull View parent, String s)
    {
        super(parent);
        text = I18n.format(s);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick)
    {
        if(renderMinecraftStyle())
        {
            //TODO button colour as well
        }
        else
        {
            RenderHelper.drawColour(getTheme().elementButtonBorder[0], getTheme().elementButtonBorder[1], getTheme().elementButtonBorder[2], 255, getLeft(), getTop(), width, height, 0);
            if(isInBounds(mouseX, mouseY))
            {
                //TODO if clicked
                //            if(Mouse.isButtonDown(0))
                //            {
                //                RenderHelper.drawColour(getTheme().elementButtonClick[0], getTheme().elementButtonClick[1], getTheme().elementButtonClick[2], 255, getLeft() + 1, getTop() + 1, width - 2, height - 2, 0);
                //            }
                //            else
                {
                    RenderHelper.drawColour(getTheme().elementButtonBackgroundHover[0], getTheme().elementButtonBackgroundHover[1], getTheme().elementButtonBackgroundHover[2], 255, getLeft() + 1, getTop() + 1, width - 2, height - 2, 0);
                }
            }
            else
            {
                RenderHelper.drawColour(getTheme().elementButtonBackgroundInactive[0], getTheme().elementButtonBackgroundInactive[1], getTheme().elementButtonBackgroundInactive[2], 255, getLeft() + 1, getTop() + 1, width - 2, height - 2, 0);
            }
        }
        drawString(text, getLeft() + (this.width - getFontRenderer().getStringWidth(text)) / 2F, getTop() + (height - getFontRenderer().FONT_HEIGHT) / 2F, Theme.getAsHex(getTheme().font));
    }
}
