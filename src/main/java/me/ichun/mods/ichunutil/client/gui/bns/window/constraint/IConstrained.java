package me.ichun.mods.ichunutil.client.gui.bns.window.constraint;

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
    int getWidth();
    int getHeight();
    int getParentWidth();
    int getParentHeight();
}
