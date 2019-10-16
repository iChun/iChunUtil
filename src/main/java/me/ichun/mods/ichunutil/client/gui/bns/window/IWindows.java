package me.ichun.mods.ichunutil.client.gui.bns.window;

import me.ichun.mods.ichunutil.client.gui.bns.Theme;
import net.minecraft.client.gui.FontRenderer;

public interface IWindows
{
    int getWidth();
    int getHeight();
    Theme getTheme();
    boolean renderMinecraftStyle();
    FontRenderer getFontRenderer();
}
