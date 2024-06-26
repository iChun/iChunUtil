package me.ichun.mods.ichunutil.client.gui.bns;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Widget;

public interface Rectangle extends Widget //Yes, ironic that "Boxes & Stuff" uses RECTANGLES
{
    int getLeft();
    int getRight();
    int getTop();
    int getBottom();
    int getWidth();
    int getHeight();

    //These methods are for recursiveness to include workspace who has these values
    <W extends Workspace> W getWorkspace();
    Minecraft getMinecraft();
    Theme getTheme();
    Font getFontRenderer();
    int renderMinecraftStyle();
}
