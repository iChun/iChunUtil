package us.ichun.mods.ichunutil.client.gui.window.element;

import org.lwjgl.input.Mouse;
import us.ichun.mods.ichunutil.client.gui.Theme;
import us.ichun.mods.ichunutil.client.gui.window.Window;
import us.ichun.mods.ichunutil.client.render.RendererHelper;

public class ElementCheckBox extends ElementToggle
{
    public ElementCheckBox(Window window, int x, int y, int ID, boolean igMin, int sideH, int sideV, String Tooltip, boolean state)
    {
        super(window, x, y, 9, 9, ID, igMin, sideH, sideV, "", Tooltip, state);
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
            if(toggledState)
            {
                RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementButtonToggle[0], parent.workspace.currentTheme.elementButtonToggle[1], parent.workspace.currentTheme.elementButtonToggle[2], 255, getPosX() + 1, getPosY() + 1, width - 2, height - 2, 0);
            }
            else
            {
                RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementButtonBackgroundInactive[0], parent.workspace.currentTheme.elementButtonBackgroundInactive[1], parent.workspace.currentTheme.elementButtonBackgroundInactive[2], 255, getPosX() + 1, getPosY() + 1, width - 2, height - 2, 0);
            }
        }
        if(toggledState)
        {
            parent.workspace.getFontRenderer().drawString("X", getPosX() + 2, getPosY() + height - (height / 2) - (parent.workspace.getFontRenderer().FONT_HEIGHT / 2), Theme.getAsHex(!toggledState ? parent.workspace.currentTheme.elementButtonToggleHover : parent.workspace.currentTheme.font), false);
        }
    }
}
