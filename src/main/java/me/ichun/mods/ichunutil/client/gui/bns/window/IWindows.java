package me.ichun.mods.ichunutil.client.gui.bns.window;

import me.ichun.mods.ichunutil.client.gui.bns.Theme;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.IConstrainable;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.INestedGuiEventHandler;

import javax.annotation.Nullable;

public interface IWindows extends INestedGuiEventHandler
{
    int getWidth();
    int getHeight();
    Theme getTheme();
    int renderMinecraftStyle();
    FontRenderer getFontRenderer();
    Window<?> addWindow(Window<?> window);
    void removeWindow(Window<?> window);
    boolean isObstructed(Window<?> mWindow, double mouseX, double mouseY);
    default boolean canDockWindows() { return false; }
    default DockInfo getDockInfo(double mouseX, double mouseY, boolean dockStack) { return null; } //returns null if not in a point where you can dock something
    default void addToDock(Window<?> window, Constraint.Property.Type type) {}
    default void addToDocked(Window<?> docked, Window<?> window) {}
    default void removeFromDock(Window<?> window){}
    default boolean isDocked(Window<?> window) { return false; }
    default boolean sameDockStack(IConstrainable window, IConstrainable window1) { return false; }

    class DockInfo
    {
        public final @Nullable Window<?> window;
        public final @Nullable Constraint.Property.Type type;

        public DockInfo(Window<?> window, Constraint.Property.Type type) {
            this.window = window;
            this.type = type;
        }
    }
}
