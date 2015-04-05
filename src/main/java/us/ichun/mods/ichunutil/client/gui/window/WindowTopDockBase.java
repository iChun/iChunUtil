package us.ichun.mods.ichunutil.client.gui.window;

import us.ichun.mods.ichunutil.client.gui.window.element.Element;
import us.ichun.mods.ichunutil.client.render.RendererHelper;

public class WindowTopDockBase extends Window
{
    public WindowTopDockBase(IWorkspace parent, int w, int h)
    {
        super(parent, 0, 0, w, h, 20, 20, "", false);
    }

    @Override
    public int clickedOnBorder(int mouseX, int mouseY, int id)//only left clicks
    {
        return 0;
    }

    @Override
    public boolean clickedOnTitle(int mouseX, int mouseY, int id)
    {
        return false;
    }

    @Override
    public void resized()
    {
        posX = 0;
        posY = 0;
        width = workspace.width;
        height = 20;
        for(Element element : elements)
        {
            element.resized();
        }
    }

    @Override
    public boolean canBeDragged()
    {
        return false;
    }

    @Override
    public boolean canMinimize() { return false; }

    @Override
    public void toggleMinimize()
    {
    }

    @Override
    public boolean isStatic()
    {
        return true;
    }

    @Override
    public void setScissor()
    {
        RendererHelper.startGlScissor(posX, posY, getWidth(), getHeight());
    }

    @Override
    public void drawBackground()
    {
        RendererHelper.drawColourOnScreen(workspace.currentTheme.windowBackground[0], workspace.currentTheme.windowBackground[1], workspace.currentTheme.windowBackground[2], 255, posX, posY, getWidth(), getHeight(), 0);
    }

    @Override
    public int getHeight()
    {
        return 20;
    }
}
