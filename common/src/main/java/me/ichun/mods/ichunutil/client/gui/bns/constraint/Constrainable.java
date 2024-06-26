package me.ichun.mods.ichunutil.client.gui.bns.constraint;

public interface Constrainable
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
    int getMinWidth();
    int getMinHeight();
    int getMaxWidth();
    int getMaxHeight();
}
