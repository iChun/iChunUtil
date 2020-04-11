package me.ichun.mods.ichunutil.client.gui.bns.window;

import me.ichun.mods.ichunutil.client.gui.bns.Theme;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.INestedGuiEventHandler;

public interface IWindows extends INestedGuiEventHandler
{
    int getWidth();
    int getHeight();
    Theme getTheme();
    boolean renderMinecraftStyle();
    FontRenderer getFontRenderer();
    Window addWindow(Window window);
    void removeWindow(Window window);
    boolean isObstructed(Window mWindow, double mouseX, double mouseY);
    default boolean canDockWindows() { return false; }
    default Constraint.Property.Type dockType(double mouseX, double mouseY) { return null; } //returns null if not in a point where you can dock something
    default void addToDock(Window window, Constraint.Property.Type type) {}
    default void removeFromDock(Window window){}
    default boolean isDocked(Window window) { return false; }
}
