package me.ichun.mods.ichunutil.client.gui.bns.window.constraint;

import java.util.function.Supplier;

public interface IConstrained
{
    void setLeft(int x);
    void setRight(int x);
    void setTop(int y);
    void setBottom(int y);
    void setPosX(int x);
    void setPosY(int y);
    void setWidth(int width);
    void setHeight(int height);
    void expandX(int width);
    void expandY(int height);
    void contractX(int width);
    void contractY(int height);
    int getWidth();
    int getHeight();
    int getParentWidth();
    int getParentHeight();
    Supplier<Integer> getMinWidth();
    Supplier<Integer> getMinHeight();
    Supplier<Integer> getMaxWidth();
    Supplier<Integer> getMaxHeight();
}
