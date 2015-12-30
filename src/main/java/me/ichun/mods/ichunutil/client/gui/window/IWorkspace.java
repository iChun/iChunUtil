package me.ichun.mods.ichunutil.client.gui.window;

import com.google.common.base.Splitter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import me.ichun.mods.ichunutil.client.gui.Theme;
import me.ichun.mods.ichunutil.client.gui.window.element.Element;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementWindow;
import me.ichun.mods.ichunutil.client.gui.window.element.ITextInput;
import me.ichun.mods.ichunutil.client.render.RendererHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class IWorkspace extends GuiScreen
{
    public int oldWidth;
    public int oldHeight;

    public int liveTime;
    public boolean resize;

    public Element elementHovered;
    public int hoverTime;
    public boolean hovering;
    public int tooltipTime = 20;

    public Element elementDragged;
    public int elementDragX;
    public int elementDragY;

    public Element elementSelected;
    public int selectedMouseX;
    public int selectedMouseY;

    public Window windowDragged;
    public int dragType; //1 = title drag, 2 >= border drag.

    public boolean mouseLeftDown;
    public boolean mouseRightDown;
    public boolean mouseMiddleDown;

    public int VARIABLE_LEVEL = 4;

    public int TOP_DOCK_HEIGHT = 19;

    public Theme currentTheme = Theme.copyInstance();

    public ArrayList<ArrayList<Window>> levels = new ArrayList<ArrayList<Window>>();

    public abstract boolean canClickOnElement(Window window, Element element);

    public FontRenderer getFontRenderer()
    {
        return fontRendererObj;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        resize = true;
        screenResize();

        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void updateScreen()
    {
        if(resize)
        {
            resize = false;
            screenResize();
        }
        if(elementHovered != null)
        {
            hoverTime++;
        }
        for(int i = levels.size() - 1; i >= 0; i--)//clean up empty levels.
        {
            if(levels.get(i).isEmpty()&& i >= VARIABLE_LEVEL)
            {
                levels.remove(i);
            }
            else
            {
                for(int j = levels.get(i).size() - 1; j >= 0; j--)
                {
                    levels.get(i).get(j).update();
                }
            }
        }
        liveTime++;
    }

    public boolean drawWindows(int mouseX, int mouseY) //returns true if mouse is on a window
    {
        hovering = false;
        boolean hasClicked = false;
        boolean onWindow = false;
        Element prevElementSelected = elementSelected;
        elementSelected = null;

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.translate(0F, 0F, 100F);
        for(int i = levels.size() - 1; i >= 0 ; i--)
        {
            for(int j = levels.get(i).size() - 1; j >= 0; j--)
            {
                Window window = levels.get(i).get(j);
                if(mouseX >= window.posX && mouseX <= window.posX + window.getWidth() && mouseY >= window.posY && mouseY <= window.posY + window.getHeight())
                {
                    onWindow = true;
                    if(!hasClicked && liveTime > 5)
                    {
                        if(Mouse.isButtonDown(0) && !mouseLeftDown)
                        {
                            windowDragged = window;
                            dragType = window.onClick(mouseX - window.posX, mouseY - window.posY, 0);
                            hasClicked = true;
                        }
                        if(Mouse.isButtonDown(1) && !mouseRightDown)
                        {
                            windowDragged = window;
                            dragType = window.onClick(mouseX - window.posX, mouseY - window.posY, 1);
                            hasClicked = true;
                        }
                        if(Mouse.isButtonDown(2) && !mouseMiddleDown)
                        {
                            windowDragged = window;
                            dragType = window.onClick(mouseX - window.posX, mouseY - window.posY, 2);
                            hasClicked = true;
                        }
                    }
                }
                window.draw(mouseX - window.posX, mouseY - window.posY);
            }
            GlStateManager.translate(0F, 0F, -10F);
        }
        RendererHelper.endGlScissor();//end scissor in case any window does it incorrectly.

        if(!hasClicked)
        {
            if((Mouse.isButtonDown(0) && !mouseLeftDown || Mouse.isButtonDown(1) && !mouseRightDown || Mouse.isButtonDown(2) && !mouseMiddleDown) && prevElementSelected != null && !(mouseX >= prevElementSelected.getPosX() && mouseX <= prevElementSelected.getPosX() + prevElementSelected.width && mouseY >= prevElementSelected.getPosY() && mouseY <= prevElementSelected.getPosY() + prevElementSelected.height))
            {
                prevElementSelected.deselected();
            }
            else
            {
                elementSelected = prevElementSelected;
            }
        }
        else if(elementSelected != prevElementSelected)
        {
            if(elementSelected != null)
            {
                elementSelected.selected();
                if(elementSelected instanceof ITextInput)
                {
                    elementSelected.onClick(selectedMouseX, selectedMouseY, 0);
                }
            }
            if(prevElementSelected != null)
            {
                prevElementSelected.deselected();
            }
        }
        else
        {
            elementSelected = prevElementSelected;
        }
        return onWindow;
    }

    public void updateElementHovered(int mouseX, int mouseY, int scroll)
    {
        if(!hovering)
        {
            elementHovered = null;
            hoverTime = 0;
        }
        else if(elementHovered != null)
        {
            boolean activated = false;
            if(scroll != 0)//scroll up
            {
                activated = elementHovered.mouseScroll(mouseX - elementHovered.parent.posX, mouseY - elementHovered.parent.posY, (int)Math.round(scroll / 120));
            }
            if(activated)
            {
                if(elementSelected != null)
                {
                    elementSelected.deselected();
                }
                elementHovered.onClick(mouseX - elementHovered.parent.posX, mouseY - elementHovered.parent.posY, 2);
                elementSelected = elementHovered;
            }
            String tooltip = elementHovered.tooltip();
            if(hoverTime > tooltipTime && tooltip != null) //1s to draw tooltip
            {
                GlStateManager.translate(0F, 0F, 20F * levels.size());
                List<String> tips = Splitter.on("\n").splitToList(tooltip);
                List<String> tipss = Splitter.on("\\n").splitToList(tooltip);
                tips = new ArrayList<String>(tipss.size() > tips.size() ? tipss : tips);
                if(tips.size() == 1)
                {
                    tips.add(StatCollector.translateToLocal(tips.get(0)));
                    tips.remove(0);
                }
                int xOffset = 5;
                int yOffset = 20;
                int longest = 0;
                for(String tip : tips)
                {
                    int length = fontRendererObj.getStringWidth(tip);
                    if(length > longest)
                    {
                        longest = length;
                    }
                }
                int size = longest + ((Window.BORDER_SIZE - 1) * 2);
                int ySize = 1 + (tips.size() * (fontRendererObj.FONT_HEIGHT + 1));
                if(width - mouseX < size)
                {
                    xOffset -= size - (width - mouseX) + 20;
                }
                if(height - (mouseY + ySize + yOffset) < 0)
                {
                    yOffset = -20;
                }
                RendererHelper.drawColourOnScreen(currentTheme.windowBorder[0], currentTheme.windowBorder[1], currentTheme.windowBorder[2], 255, mouseX + xOffset, mouseY + yOffset, longest + ((Window.BORDER_SIZE - 1) * 2), ySize, 0);
                RendererHelper.drawColourOnScreen(currentTheme.windowBackground[0], currentTheme.windowBackground[1], currentTheme.windowBackground[2], 255, mouseX + xOffset + 1, mouseY + yOffset + 1, longest + ((Window.BORDER_SIZE - 1) * 2) - 2, ySize - 2, 0);
                for(int i = 0; i < tips.size(); i++)
                {
                    fontRendererObj.drawString(tips.get(i), mouseX + xOffset + (Window.BORDER_SIZE - 1), mouseY + yOffset + (Window.BORDER_SIZE - 1) + (i * (fontRendererObj.FONT_HEIGHT + 1)), currentTheme.getAsHex(currentTheme.font), false);
                }
                //            RendererHelper.drawColourOnScreen(34, 34, 34, 255, posX + BORDER_SIZE, posY + BORDER_SIZE, getWidth() - (BORDER_SIZE * 2), getHeight() - (BORDER_SIZE * 2), 0);
            }
        }
    }

    public void updateWindowDragged(int mouseX, int mouseY)
    {
        if(windowDragged != null)
        {
            if(windowDragged.clickId == 0 && !mouseLeftDown || windowDragged.clickId == 1 && !mouseRightDown || windowDragged.clickId == 2 && !mouseMiddleDown)
            {
                windowDragged = null;
            }
            else
            {
                bringWindowToFront(windowDragged);
                if(dragType == 1) // moving the window
                {
                    if(windowDragged.canBeDragged())
                    {
                        int moveX = windowDragged.clickX - (mouseX - windowDragged.posX);
                        int moveY = windowDragged.clickY - (mouseY - windowDragged.posY);
                        if(windowDragged.docked < 0)
                        {
                            windowDragged.posX -= moveX;
                            windowDragged.posY -= moveY;
                        }
                        else
                        {
                            if(Math.sqrt(moveX * moveX + moveY + moveY) > 5)
                            {
                                removeFromDock(windowDragged);
                                windowDragged.posX -= moveX;
                                windowDragged.posY -= moveY;
                            }
                        }


                        boolean tabbed = false;
                        for(int i = levels.size() - 1; i >= 0; i--)
                        {
                            for(int j = levels.get(i).size() - 1; j >= 0; j--)
                            {
                                Window window = levels.get(i).get(j);
                                if(tabbed || !window.canBeDragged() || window == windowDragged)
                                {
                                    continue;
                                }
                                if(mouseX - window.posX >= 0 && mouseX - window.posX <= window.getWidth() && mouseY - window.posY >= 0 && mouseY - window.posY <= 12)
                                {
                                    WindowTabs tabs;
                                    if(window instanceof WindowTabs)
                                    {
                                        tabs = (WindowTabs)window;
                                    }
                                    else
                                    {
                                        tabs = new WindowTabs(this, window);
                                    }
                                    if(windowDragged.minimized)
                                    {
                                        windowDragged.toggleMinimize();
                                    }
                                    tabs.addWindow(windowDragged, true);
                                    levels.get(i).remove(j);
                                    levels.get(i).add(j, tabs);
                                    if(i < VARIABLE_LEVEL)
                                    {
                                        redock(i, null);
                                    }
                                    removeWindow(windowDragged);
                                    windowDragged = null;
                                    tabbed = true;
                                }
                            }
                        }

                        if(mouseX <= 10)
                        {
                            addToDock(0, windowDragged);
                            windowDragged = null;
                        }
                        if(mouseX >= width - 10)
                        {
                            addToDock(1, windowDragged);
                            windowDragged = null;
                        }
                    }

                    if(windowDragged != null)
                    {
                        windowDragged.resized();
                    }
                }
                if(dragType >= 2)
                {
                    int bordersClicked = dragType - 3;
                    if((bordersClicked & 1) == 1 && !((windowDragged.docked == 0 || windowDragged.docked == 1) && !levels.get(windowDragged.docked).isEmpty() && levels.get(windowDragged.docked).get(0) == windowDragged)) // top
                    {
                        windowDragged.height += windowDragged.clickY - (mouseY - windowDragged.posY);
                        windowDragged.posY -= windowDragged.clickY - (mouseY - windowDragged.posY);
                        if(windowDragged.getHeight() < windowDragged.minHeight)
                        {
                            int resize = windowDragged.getHeight() - windowDragged.minHeight;
                            windowDragged.posY += resize;
                            windowDragged.height -= resize;
                        }
                        else
                        {
                            windowDragged.clickY = mouseY - windowDragged.posY;
                        }
                    }
                    if((bordersClicked >> 1 & 1) == 1 && windowDragged.docked != 0) // left
                    {
                        windowDragged.width += windowDragged.clickX - (mouseX - windowDragged.posX);
                        windowDragged.posX -= windowDragged.clickX - (mouseX - windowDragged.posX);
                        if(windowDragged.getWidth() < windowDragged.minWidth)
                        {
                            int resize = windowDragged.getWidth() - windowDragged.minWidth;
                            windowDragged.posX += resize;
                            windowDragged.width -= resize;
                        }
                        else
                        {
                            windowDragged.clickX = mouseX - windowDragged.posX;
                        }
                    }
                    if((bordersClicked >> 2 & 1) == 1) // bottom
                    {
                        windowDragged.height -= windowDragged.clickY - (mouseY - windowDragged.posY);
                        if(windowDragged.getHeight() < windowDragged.minHeight)
                        {
                            windowDragged.height = windowDragged.minHeight;
                        }
                        else
                        {
                            windowDragged.clickY = mouseY - windowDragged.posY;
                        }
                    }
                    if((bordersClicked >> 3 & 1) == 1 && windowDragged.docked != 1) // right
                    {
                        windowDragged.width -= windowDragged.clickX - (mouseX - windowDragged.posX);
                        if(windowDragged.getWidth() < windowDragged.minWidth)
                        {
                            windowDragged.width = windowDragged.minWidth;
                        }
                        else
                        {
                            windowDragged.clickX = mouseX - windowDragged.posX;
                        }
                    }
                    windowDragged.resized();

                    if(windowDragged.docked >= 0)
                    {
                        redock(windowDragged.docked, windowDragged);
                    }
                }
            }
        }
    }

    public void updateElementDragged(int mouseX, int mouseY)
    {
        if(elementDragged != null)
        {
            if(!mouseLeftDown)
            {
                elementDragged = null;
            }
            else if(!(mouseX - elementDragged.parent.posX >= 0 && mouseX - elementDragged.parent.posX <= elementDragged.parent.getWidth() && mouseY - elementDragged.parent.posY >= 0 && mouseY - elementDragged.parent.posY <= 12))
            {
                if(elementDragged instanceof ElementWindow)
                {
                    ((WindowTabs)((ElementWindow)elementDragged).parent).detach((ElementWindow)elementDragged);

                    ElementWindow element = (ElementWindow)elementDragged;

                    windowDragged = element.mountedWindow;
                    windowDragged.docked = -1;
                    dragType = 1;

                    windowDragged.width = element.oriWidth;
                    windowDragged.height = element.oriHeight;

                    windowDragged.posX = mouseX - (windowDragged.getWidth() / 2);
                    windowDragged.posY = mouseY - 6;

                    windowDragged.resized();

                    elementDragged = null;
                }
            }
        }
    }

    public void updateKeyStates()
    {
        mouseLeftDown = Mouse.isButtonDown(0);
        mouseRightDown = Mouse.isButtonDown(1);
        mouseMiddleDown = Mouse.isButtonDown(2);
    }

    public boolean canUseDocks()
    {
        return true;
    }

    public void addToDock(int dock, Window window)
    {
        if(canUseDocks() && window != null && window.docked < 0)
        {
            if(window.minimized)
            {
                window.toggleMinimize();
            }
            ArrayList<Window> docked = levels.get(dock);
            window.docked = dock;
            window.oriHeight = window.height;
            window.oriWidth = window.width;
            docked.add(window);
            for(int i = VARIABLE_LEVEL; i < levels.size(); i++)
            {
                levels.get(i).remove(window);
            }

            redock(dock, null);
        }
    }

    public void redock(int dock, Window pref)
    {
        ArrayList<Window> docked = levels.get(dock);
        int prefInt = -2;
        if(pref != null)
        {
            for(int j = 0; j < docked.size(); j++)
            {
                if(docked.get(j) == pref)
                {
                    prefInt = j;
                }
            }
        }
        for(int j = 0; j < docked.size(); j++)
        {
            Window window = docked.get(j);
            if(dock == 0)
            {
                window.posX = -1;
            }
            else if(dock == 1)
            {
                window.posX = width - window.getWidth() + 1;
            }
            if(dock <= 1)
            {
                if(prefInt != -2)
                {
                    docked.get(0).width = docked.get(prefInt).width;
                }
                else
                {
                    if(j == 0)
                    {
                        window.posY = TOP_DOCK_HEIGHT;
                    }
                    else
                    {
                        window.width = docked.get(0).width;
                        window.posY = docked.get(j - 1).posY + (docked.get(j - 1).minimized ? 12 : (docked.get(j - 1).height + docked.get(j - 1).posY + 2 >= height) ? docked.get(j - 1).oriHeight : docked.get(j - 1).height);
                        docked.get(j - 1).height = window.posY - docked.get(j - 1).posY + 2;
                    }
                }
                if(j - 1 == prefInt)
                {
                    window.height += window.posY - (docked.get(j - 1).posY + docked.get(j - 1).height) + 2;
                    window.posY -= window.posY - (docked.get(j - 1).posY + docked.get(j - 1).height) + 2;
                }
                if(j + 1 == prefInt)
                {
                    window.height = docked.get(j + 1).posY - window.posY + 2;
                    if(window.height < window.minHeight + 2)
                    {
                        window.height = window.minHeight + 2;
                        docked.get(prefInt).posY = window.posY + window.height - 2;
                        windowDragged = null;
                        dragType = 0;
                    }
                }
                window.width = docked.get(0).width;

                redock(2, null);
            }
            else if(dock == 2)
            {
                int pX1 = -1;
                int pX2 = width + 1;
                if(!levels.get(0).isEmpty())
                {
                    pX1 = levels.get(0).get(0).width - 2;
                }
                if(!levels.get(1).isEmpty())
                {
                    pX2 = levels.get(1).get(0).posX + 1;
                }
                window.posX = pX1;
                window.width = pX2 - pX1;
                window.posY = height - window.getHeight() + 1;
            }
            window.resized();
        }
        screenResize();
    }

    public void removeFromDock(Window window)
    {
        for(int i = 2; i >= 0; i--)
        {
            ArrayList<Window> docked = levels.get(i);
            for(int j = docked.size() - 1; j >= 0; j--)
            {
                Window window1 = docked.get(j);
                if(window1 == window)
                {
                    docked.remove(j);

                    redock(i, null);

                    break;
                }
            }
        }
        window.docked = -1;
        window.height = window.oriHeight;
        window.width = window.oriWidth;

        addWindowOnTop(window);

        window.resized();

        redock(2, null);
    }

    public void removeWindow(Window window)
    {
        removeWindow(window, false);
    }

    public void removeWindow(Window window, boolean checkTab)
    {
        for(int i = levels.size() - 1; i >= 0 ; i--)
        {
            for(int j = levels.get(i).size() - 1; j >= 0; j--)
            {
                Window window1 = levels.get(i).get(j);
                if(window1 instanceof WindowTabs && !(window instanceof WindowTabs) && checkTab)
                {
                    WindowTabs tabs = (WindowTabs)window1;
                    for(ElementWindow tab : tabs.tabs)
                    {
                        if(tab.mountedWindow == window)
                        {
                            Window win = WindowTabs.detach(tab);
                            win.docked = -1;

                            win.width = tab.oriWidth;
                            win.height = tab.oriHeight;

                            win.posX = (width / 2) - (win.width / 2);
                            win.posY = (height / 2) - (win.height / 2);

                            win.resized();

                            removeWindow(win);

                            return;
                        }
                    }
                }
                if(window1 == window)
                {
                    if(i < VARIABLE_LEVEL)
                    {
                        removeFromDock(window1);
                        removeWindow(window1, checkTab);
                    }
                    else
                    {
                        levels.get(i).remove(j);
                        if(levels.get(i).isEmpty())
                        {
                            levels.remove(i);
                        }
                    }
                    break;
                }
            }
        }
    }

    public void addWindowOnTop(Window window)
    {
        if(!window.allowMultipleInstances() && window.getClass() != Window.class)
        {
            for(int i = levels.size() - 1; i >= 0 ; i--)
            {
                for(int j = levels.get(i).size() - 1; j >= 0; j--)
                {
                    Window window1 = levels.get(i).get(j);
                    if(window == window1)
                    {
                        continue;
                    }
                    if(window1 instanceof WindowTabs)
                    {
                        WindowTabs tabs = (WindowTabs)window1;
                        for(ElementWindow tab : tabs.tabs)
                        {
                            if(tab.mountedWindow.getClass() == window.getClass())
                            {
                                Window win = WindowTabs.detach(tab);
                                win.docked = -1;

                                win.width = tab.oriWidth;
                                win.height = tab.oriHeight;

                                win.posX = (width / 2) - (win.width / 2);
                                win.posY = (height / 2) - (win.height / 2);

                                win.resized();

                                bringWindowToFront(win);
                                return;
                            }
                        }
                    }
                    else
                    {
                        if(window1.getClass() == window.getClass())
                        {
                            if(window1.docked >= 0)
                            {
                                removeFromDock(window1);
                            }
                            window1.docked = -1;

                            if(window1.height > height)
                            {
                                window1.height = window1.minHeight;
                            }
                            if(window1.width > width)
                            {
                                window1.width = window1.minWidth;
                            }

                            window1.posX = (width / 2) - (window1.width / 2);
                            window1.posY = (height / 2) - (window1.height / 2);

                            window1.resized();
                            bringWindowToFront(window1);
                            return;
                        }
                    }
                }
            }
        }
        ArrayList<Window> topLevel = new ArrayList<Window>();
        topLevel.add(window);
        levels.add(topLevel);
    }

    public void screenResize()
    {
        for(int i = 0; i < VARIABLE_LEVEL; i++)
        {
            ArrayList<Window> docked = levels.get(i);
            for(int j = 0; j < docked.size(); j++)
            {
                Window window = docked.get(j);

                if(i == 0)
                {
                    window.posX = -1;
                }
                else if(i == 1)
                {
                    window.posX = width - window.getWidth() + 1;
                }
                else if(i == 2)
                {
                    int pX1 = -1;
                    int pX2 = width + 1;
                    if(!levels.get(0).isEmpty())
                    {
                        pX1 = levels.get(0).get(0).width - 2;
                    }
                    if(!levels.get(1).isEmpty())
                    {
                        pX2 = levels.get(1).get(0).posX + 1;
                    }
                    window.posX = pX1;
                    window.width = pX2 - pX1;
                    window.posY = height - window.getHeight() + 1;
                }
                if(j == docked.size() - 1 && i != 2)
                {
                    window.height = height - window.posY + 1;
                }

                if(window.posX == (oldWidth - window.width) / 2 && window.posY == (oldHeight - window.height) / 2)
                {
                    window.posX = (width - window.width) / 2;
                    window.posY = (height - window.height) / 2;
                }

                window.resized();
            }
        }

        for(int i = VARIABLE_LEVEL; i < levels.size(); i++)
        {
            ArrayList<Window> docked = levels.get(i);
            for(int j = 0; j < docked.size(); j++)
            {
                Window window = docked.get(j);

                if(window.posX == (oldWidth - window.width) / 2 && window.posY == (oldHeight - window.height) / 2)
                {
                    window.putInMiddleOfScreen();
                }

                window.resized();
            }
        }

        oldWidth = width;
        oldHeight = height;
    }

    public void bringWindowToFront(Window window)
    {
        if(window.isStatic())
        {
            return;
        }
        for(int i = levels.size() - 1; i >= 0 ; i--)
        {
            for(int j = levels.get(i).size() - 1; j >= 0; j--)
            {
                Window window1 = levels.get(i).get(j);
                if(window1 == window && window.docked < 0 && !(i == levels.size() - 1 && levels.get(i).size() == 1))
                {
                    ArrayList<Window> topLevel = new ArrayList<Window>();
                    topLevel.add(window1);
                    levels.get(i).remove(j);
                    if(levels.get(i).isEmpty() && i >= VARIABLE_LEVEL)
                    {
                        levels.remove(i);
                    }
                    levels.add(topLevel);
                }
            }
        }
    }

    public String reString(String s, int width)
    {
        while(s.length() > 1 && getFontRenderer().getStringWidth(s) > width - 3)
        {
            if(s.startsWith("..."))
            {
                break;
            }
            if(s.endsWith("..."))
            {
                s = s.substring(0, s.length() - 4) + "...";
            }
            else
            {
                s = s.substring(0, s.length() - 1) + "...";
            }
        }
        return s;
    }
}
