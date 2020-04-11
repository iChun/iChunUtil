package me.ichun.mods.ichunutil.client.gui.bns.window;

import me.ichun.mods.ichunutil.client.gui.bns.Theme;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.INestedGuiEventHandler;

public interface IWindows extends INestedGuiEventHandler
{
    int getWidth();
    int getHeight();
    Theme getTheme();
    boolean renderMinecraftStyle();
    FontRenderer getFontRenderer();
    boolean isObstructed(Window mWindow, double mouseX, double mouseY);
    default boolean canDockWindows() { return true; }
}
