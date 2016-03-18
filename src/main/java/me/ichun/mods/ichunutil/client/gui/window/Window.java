package me.ichun.mods.ichunutil.client.gui.window;

import me.ichun.mods.ichunutil.client.gui.Theme;
import me.ichun.mods.ichunutil.client.gui.window.element.Element;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementMinimize;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementTitle;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.translation.I18n;

import java.util.ArrayList;

public class Window
{
    public final IWorkspace workspace;

    public int posX;
    public int posY;
    public int width;
    public int height;

    public int docked;
    public boolean minimized;

    public int clickX;
    public int clickY;
    public int clickId;

    public int oriWidth;
    public int oriHeight;

    public int minWidth;
    public int minHeight;

    public String titleLocale;
    public boolean hasTitle; //if it has title, it can minimize.

    public boolean isTab;

    public ArrayList<Element> elements = new ArrayList<Element>();

    public static final int BORDER_SIZE = 3;

    public Window(IWorkspace parent, int x, int y, int w, int h, int minW, int minH, String title, boolean hasTit)
    {
        workspace = parent;

        posX = x;
        posY = y;
        oriWidth = width = w;
        oriHeight = height = h;
        minWidth = minW;
        minHeight = minH;

        titleLocale = title;
        hasTitle = hasTit;

        if(hasTitle)
        {
            elements.add(new ElementTitle(this, 0, 0, parent.width - 13, 13, -100));
        }
        if(canMinimize())
        {
            elements.add(new ElementMinimize(this, width - 13, 2, 10, 10, -100));
        }

        docked = -1;
    }

    public Window putInMiddleOfScreen()
    {
        posX = (workspace.width - width) / 2;
        posY = (workspace.height - height) / 2;
        resized();
        return this;
    }

    public void update()
    {
        for(Element element : elements)
        {
            element.update();
        }
    }

    public void draw(int mouseX, int mouseY) //4 pixel border?
    {
        setScissor();
        drawBackground();
        drawTitle();

        for(Element element : elements)
        {
            if(element.ignoreMinimized && minimized || !minimized)
            {
                boolean boundary = element.mouseInBoundary(mouseX, mouseY);
                boolean obstructed = false;
                if(boundary)
                {
                    boolean found = false;
                    for(int i = 0; i < workspace.levels.size(); i++)
                    {
                        for(int j = 0; j < workspace.levels.get(i).size(); j++)
                        {
                            Window window = workspace.levels.get(i).get(j);
                            if(!found)
                            {
                                if(window == this)
                                {
                                    found = true;
                                }
                            }
                            else if(posX + mouseX >= window.posX && posX + mouseX <= window.posX + window.getWidth() && posY + mouseY >= window.posY && posY + mouseY <= window.posY + window.getHeight())
                            {
                                obstructed = true;
                                break;
                            }
                        }
                    }
                }
                if(boundary && !obstructed)
                {
                    workspace.hovering = true;
                    if(workspace.elementHovered != element)
                    {
                        workspace.elementHovered = element;
                        workspace.hoverTime = 0;
                    }
                }
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                element.draw(mouseX, mouseY, boundary && !obstructed);
            }
        }
        RendererHelper.endGlScissor();
    }

    public void setScissor()
    {
        if(isTab)
        {
            RendererHelper.startGlScissor(posX + 1, posY + 1 + 12, getWidth() - 2, getHeight() - 2 - 12);
        }
        else
        {
            RendererHelper.startGlScissor(posX + 1, posY + 1, getWidth() - 2, getHeight() - 2);
        }
    }

    public void drawBackground()
    {
        if(!minimized)
        {
            if(docked >= 0)
            {
                RendererHelper.drawColourOnScreen(workspace.currentTheme.windowBackground[0], workspace.currentTheme.windowBackground[1], workspace.currentTheme.windowBackground[2], 255, posX + 1, posY + 1, getWidth() - 2, getHeight() - 2, 0);
            }
            else
            {
                RendererHelper.drawColourOnScreen(workspace.currentTheme.windowBorder[0], workspace.currentTheme.windowBorder[1], workspace.currentTheme.windowBorder[2], 255, posX + 1, posY + 1, getWidth() - 2, getHeight() - 2, 0);
                RendererHelper.drawColourOnScreen(workspace.currentTheme.windowBackground[0], workspace.currentTheme.windowBackground[1], workspace.currentTheme.windowBackground[2], 255, posX + BORDER_SIZE, posY + BORDER_SIZE, getWidth() - (BORDER_SIZE * 2), getHeight() - (BORDER_SIZE * 2), 0);
            }
        }
    }

    public void drawTitle()
    {
        if(hasTitle)
        {
            RendererHelper.drawColourOnScreen(workspace.currentTheme.windowBorder[0], workspace.currentTheme.windowBorder[1], workspace.currentTheme.windowBorder[2], 255, posX + 1, posY + 1, getWidth() - 2, 12, 0);
            String titleToRender = I18n.translateToLocal(titleLocale);
            while(titleToRender.length() > 1 && workspace.getFontRenderer().getStringWidth(titleToRender) > getWidth() - (BORDER_SIZE * 2) - workspace.getFontRenderer().getStringWidth("  _"))
            {
                if(titleToRender.startsWith("..."))
                {
                    break;
                }
                if(titleToRender.endsWith("..."))
                {
                    titleToRender = titleToRender.substring(0, titleToRender.length() - 4) + "...";
                }
                else
                {
                    titleToRender = titleToRender.substring(0, titleToRender.length() - 1) + "...";
                }
            }
            workspace.getFontRenderer().drawString(titleToRender, posX + 4, posY + 3, Theme.getAsHex(workspace.currentTheme.font), false);
        }
    }

    public int onClick(int mouseX, int mouseY, int id) //returns > 0 if clicked on title//border with LMB.
    {
        clickX = mouseX;
        clickY = mouseY;
        clickId = id;

        boolean clickedElement = false;
        ArrayList<Element> els = new ArrayList<Element>(elements);
        for(int k = els.size() - 1; k >= 0; k--)
        {
            Element element = els.get(k);
            if(element.mouseInBoundary(mouseX, mouseY) && (minimized && element.ignoreMinimized || !minimized) && workspace.canClickOnElement(this, element) && element.onClick(mouseX, mouseY, id))
            {
                if(id == 0)
                {
                    workspace.elementDragged = element;
                    workspace.elementDragX = posX + mouseX;
                    workspace.elementDragY = posY + mouseY;
                }
                workspace.elementSelected = element;
                workspace.selectedMouseX = mouseX;
                workspace.selectedMouseY = mouseY;
                clickedElement = true;
            }
        }
        if(!clickedElement)
        {
            int borderClick = clickedOnBorder(mouseX, mouseY, id);
            if(borderClick > 1)
            {
                return borderClick + 2;
            }
            else if(clickedOnTitle(mouseX, mouseY, id))
            {
                return 1;
            }
        }
        return 0;
    }

    public int clickedOnBorder(int mouseX, int mouseY, int id)//only left clicks
    {
        if(id == 0 && !minimized)
        {
            return ((mouseY <= BORDER_SIZE + 1) ? 1 : 0) + (((mouseX <= BORDER_SIZE + 1) ? 1 : 0) << 1) + (((mouseY >= getHeight() - BORDER_SIZE - 1) ? 1 : 0) << 2) + (((mouseX >= getWidth() - BORDER_SIZE - 1) ? 1 : 0) << 3) + 1;
        }
        return 0;
    }

    public boolean clickedOnTitle(int mouseX, int mouseY, int id)
    {
        return mouseX >= 0 && mouseX <= getWidth() && mouseY >= 0 && mouseY <= 12;
    }

    public boolean allowMultipleInstances()
    {
        return false;
    }

    public boolean interactableWhileNoProjects()
    {
        return true;
    }

    public void elementTriggered(Element element)
    {
    }

    public void resized()
    {
        for(Element element : elements)
        {
            element.resized();
        }
    }

    public boolean canBeDragged()
    {
        return true;
    }

    public void shutdown(){}

    public void toggleMinimize()
    {
        minimized = !minimized;
        if(docked >= 0)
        {
            workspace.redock(docked, null);
        }
    }

    public boolean isStatic()
    {
        return false;
    }

    public boolean canMinimize() { return true; }

    public boolean invertMinimizeSymbol()
    {
        return false;
    }

    public int getHeight()
    {
        return minimized ? 13 : height;
    }

    public int getWidth()
    {
        return width;
    }
}
