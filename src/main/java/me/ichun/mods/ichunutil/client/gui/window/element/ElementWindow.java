package me.ichun.mods.ichunutil.client.gui.window.element;

import me.ichun.mods.ichunutil.client.gui.Theme;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.gui.window.WindowTabs;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import net.minecraft.util.text.TextComponentTranslation;

public class ElementWindow extends Element
{
    public Window mountedWindow;
    public int oriWidth;
    public int oriHeight;

    public ElementWindow(Window window, int x, int y, int w, int h, int ID, Window mount)
    {
        super(window, x, y, w, h, ID, true);
        mountedWindow = mount;
        oriWidth = mountedWindow.width;
        oriHeight = mountedWindow.height;
    }

    @Override
    public void update()
    {
        mountedWindow.update();
    }

    @Override
    public void draw(int mouseX, int mouseY, boolean hover)
    {
        String titleToRender = new TextComponentTranslation(mountedWindow.titleLocale).getFormattedText();
        while(titleToRender.length() > 1 && parent.workspace.getFontRenderer().getStringWidth(titleToRender) > width)
        {
            if(titleToRender.startsWith("... "))
            {
                break;
            }
            if(titleToRender.endsWith("... "))
            {
                titleToRender = titleToRender.substring(0, titleToRender.length() - 5) + "... ";
            }
            else
            {
                titleToRender = titleToRender.substring(0, titleToRender.length() - 1) + "... ";
            }
        }
        parent.workspace.getFontRenderer().drawString(titleToRender, parent.posX + posX + 4, parent.posY + posY + 3, Theme.getAsHex(parent.workspace.currentTheme.font), false);

        mountedWindow.docked = parent.docked;
        mountedWindow.posX = parent.posX;
        mountedWindow.posY = parent.posY;
        mountedWindow.width = parent.width;
        mountedWindow.height = parent.height;

        if(parent instanceof WindowTabs)
        {
            WindowTabs tab = (WindowTabs)parent;
            if(tab.minimized && id != tab.tabs.size() - 1)
            {
                RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.tabSideInactive[0], parent.workspace.currentTheme.tabSideInactive[1], parent.workspace.currentTheme.tabSideInactive[2], 255, getPosX() + width + 1, getPosY() + 1, 1, height, 0);
            }
            if(tab.selectedTab == id && !tab.minimized)
            {
                mountedWindow.isTab = true;
                mountedWindow.draw(mouseX, mouseY);
                mountedWindow.isTab = false;
                RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.windowBorder[0], parent.workspace.currentTheme.windowBorder[1], parent.workspace.currentTheme.windowBorder[2], 255, getPosX(), getPosY() + 1 + height, width + 3, 2, 0);
            }
            else if(hover)
            {
                RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.tabBorder[0], parent.workspace.currentTheme.tabBorder[1], parent.workspace.currentTheme.tabBorder[2], 255, getPosX() + 2, getPosY() + 1, width, 1, 0);
                RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.tabBorder[0], parent.workspace.currentTheme.tabBorder[1], parent.workspace.currentTheme.tabBorder[2], 255, getPosX() + 2, getPosY() + 1, 1, height, 0);
                RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.tabBorder[0], parent.workspace.currentTheme.tabBorder[1], parent.workspace.currentTheme.tabBorder[2], 255, getPosX() + 2, getPosY() + 1 + height - 1, width, 1, 0);
                RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.tabBorder[0], parent.workspace.currentTheme.tabBorder[1], parent.workspace.currentTheme.tabBorder[2], 255, getPosX() + width + 1, getPosY() + 1, 1, height, 0);
            }
        }
    }

    @Override
    public void resized()
    {
        if(parent instanceof WindowTabs)
        {
            WindowTabs tab = (WindowTabs)parent;
            int space = tab.getWidth() - (tab.BORDER_SIZE * 2) - tab.workspace.getFontRenderer().getStringWidth("  _");
            int totalSpace = 0;
            for(ElementWindow tab1 : tab.tabs)
            {
                totalSpace += tab.workspace.getFontRenderer().getStringWidth(new TextComponentTranslation(tab1.mountedWindow.titleLocale).getFormattedText() + " ");
            }
            if(totalSpace > space)
            {
                posX = tab.BORDER_SIZE + (id * (space / tab.tabs.size()));
                posY = 0;
                width = space / tab.tabs.size();
                height = 12;
            }
            else
            {
                posX = tab.BORDER_SIZE;
                for(int i = 0; i < id; i++)
                {
                    posX += tab.workspace.getFontRenderer().getStringWidth(new TextComponentTranslation(tab.tabs.get(i).mountedWindow.titleLocale).getFormattedText() + " ");
                }
                posY = 0;
                width = tab.workspace.getFontRenderer().getStringWidth(new TextComponentTranslation(mountedWindow.titleLocale).getFormattedText() + " ");
                height = 12;
            }
            mountedWindow.docked = parent.docked;
            mountedWindow.posX = parent.posX;
            mountedWindow.posY = parent.posY;
            mountedWindow.width = parent.width;
            mountedWindow.height = parent.height;

        }
        mountedWindow.resized();
    }

    @Override
    public String tooltip()
    {
        String titleToRender = new TextComponentTranslation(mountedWindow.titleLocale).getFormattedText();
        if(parent.workspace.getFontRenderer().getStringWidth(titleToRender) > width)
        {
            return mountedWindow.titleLocale;
        }
        return null; //return null for no tooltip. This is localized.
    }

    @Override
    public boolean onClick(int mouseX, int mouseY, int id)
    {
        if(id == 0)
        {
            if(parent instanceof WindowTabs)
            {
                ((WindowTabs)parent).selectedTab = this.id;
                if(parent.minimized)
                {
                    parent.toggleMinimize();
                }
            }
        }
        return true;
    }
}
