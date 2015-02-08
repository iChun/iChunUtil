package us.ichun.mods.ichunutil.client.gui.config.window.element;

import us.ichun.mods.ichunutil.client.gui.window.Window;
import us.ichun.mods.ichunutil.client.gui.window.element.ElementListTree;
import us.ichun.mods.ichunutil.common.core.config.ConfigBase;

public class ElementCatsList extends ElementListTree
{
    public int mX;
    public int mY;

    public ElementCatsList(Window window, int x, int y, int w, int h, int ID, boolean igMin, boolean drag)
    {
        super(window, x, y, w, h, ID, igMin, drag);
    }

    @Override
    public void draw(int mouseX, int mouseY, boolean hover)
    {
        mX = mouseX;
        mY = mouseY;
        super.draw(mouseX, mouseY, hover);
    }

    @Override
    public String tooltip()
    {
        int treeHeight = 0;
        int treeHeight1 = 0;
        for(int i = 0; i < trees.size(); i++)
        {
            Tree tree = trees.get(i);
            treeHeight1 += tree.getHeight();
        }

        int scrollHeight = 0;
        if(treeHeight1 > height)
        {
            scrollHeight = (int)((height - treeHeight1) * sliderProg);
        }

        for(int i = 0; i < trees.size(); i++)
        {
            Tree tree = trees.get(i);

            if(mX >= posX && mX < posX + width + (treeHeight1 > height ? 10 : 0) && mY >= posY + treeHeight + scrollHeight && mY < posY + treeHeight + scrollHeight + tree.getHeight())
            {
                return ((ConfigBase.CategoryInfo)tree.attachedObject).comment;
            }

            treeHeight += tree.getHeight();
        }
        return null; //return null for no tooltip. This is localized.
    }
}
