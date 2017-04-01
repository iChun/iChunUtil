package me.ichun.mods.ichunutil.client.gui.window.element;

import me.ichun.mods.ichunutil.client.gui.Theme;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.input.Mouse;

public class ElementButton extends Element
{
    public int anchorH;//0 = left, 1 = right, 2 = middle
    public int spaceH;
    public int anchorV;
    public int spaceV;
    public String text;

    public ElementButton(Window window, int x, int y, int w, int h, int ID, boolean igMin, int sideH, int sideV, String Text)
    {
        super(window, x, y, w, h, ID, igMin);
        anchorH = sideH;
        switch(anchorH)
        {
            case 0:
                spaceH = posX;
                break;
            case 1:
                spaceH = parent.width - posX - width;
                break;
        }

        anchorV = sideV;
        switch(anchorV)
        {
            case 0:
                spaceV = posY;
                break;
            case 1:
                spaceV = parent.height - posY - height;
                break;
        }
        text = Text;
    }

    @Override
    public void draw(int mouseX, int mouseY, boolean hover)
    {
        RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementButtonBorder[0], parent.workspace.currentTheme.elementButtonBorder[1], parent.workspace.currentTheme.elementButtonBorder[2], 255, getPosX(), getPosY(), width, height, 0);
        if(hover)
        {
            if(Mouse.isButtonDown(0))
            {
                RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementButtonClick[0], parent.workspace.currentTheme.elementButtonClick[1], parent.workspace.currentTheme.elementButtonClick[2], 255, getPosX() + 1, getPosY() + 1, width - 2, height - 2, 0);
            }
            else
            {
                RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementButtonBackgroundHover[0], parent.workspace.currentTheme.elementButtonBackgroundHover[1], parent.workspace.currentTheme.elementButtonBackgroundHover[2], 255, getPosX() + 1, getPosY() + 1, width - 2, height - 2, 0);
            }
        }
        else
        {
            RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementButtonBackgroundInactive[0], parent.workspace.currentTheme.elementButtonBackgroundInactive[1], parent.workspace.currentTheme.elementButtonBackgroundInactive[2], 255, getPosX() + 1, getPosY() + 1, width - 2, height - 2, 0);
        }
        if(!(this instanceof ElementButtonTextured))
        {
            parent.workspace.getFontRenderer().drawString(I18n.translateToLocal(text), getPosX() + (width / 2) - (parent.workspace.getFontRenderer().getStringWidth(I18n.translateToLocal(text)) / 2), getPosY() + height - (height / 2) - (parent.workspace.getFontRenderer().FONT_HEIGHT / 2), Theme.getAsHex(parent.workspace.currentTheme.font), false);
        }
    }

    @Override
    public boolean onClick(int mouseX, int mouseY, int id)
    {
        parent.elementTriggered(this);
        return true;
    }

    @Override
    public void resized()
    {
        switch(anchorH)
        {
            case 0:
                posX = spaceH;
                break;
            case 1:
                posX = parent.width - spaceH - width;
                break;
            case 2:
                posX = (parent.width / 2) - (width / 2);
                break;
        }
        switch(anchorV)
        {
            case 0:
                posY = spaceV;
                break;
            case 1:
                posY = parent.height - spaceV - height;
                break;
            case 2:
                posY = (parent.height / 2) - (height / 2);
                break;
        }
    }
}
