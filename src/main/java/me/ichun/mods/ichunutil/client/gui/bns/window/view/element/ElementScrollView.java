package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ElementScrollView extends ElementFertile
{
    public List<Element<?>> elements = new ArrayList<>();
    private @Nullable ElementScrollBar<?> scrollVert;
    private @Nullable ElementScrollBar<?> scrollHori;

    public boolean hasInit;

    public ElementScrollView(@Nonnull Fragment parent)
    {
        super(parent);
    }

    public <T extends ElementScrollView> T setScrollVertical(ElementScrollBar<?> scroll)
    {
        scrollVert = scroll;
        scrollVert.setCallback((scr) -> alignItems());
        return (T)this;
    }

    public <T extends ElementScrollView> T setScrollHorizontal(ElementScrollBar<?> scroll)
    {
        scrollHori = scroll;
        scrollHori.setCallback((scr) -> alignItems());
        return (T)this;
    }

    public Element<?> addElement(Element<?> e)
    {
        Element<?> anchor = elements.isEmpty() ? null : elements.get(elements.size() - 1);

        elements.add(e);

        e.setConstraint(Constraint.sizeOnly(e));
//        e.constraint = new Constraint(e).left(this, Constraint.Property.Type.LEFT, getBorderSize()).right(this, Constraint.Property.Type.RIGHT, getBorderSize());
//        if(anchor == null)
//        {
//            e.constraint.top(this, Constraint.Property.Type.TOP, getBorderSize());
//        }
//        else
//        {
//            e.constraint.top(anchor, Constraint.Property.Type.BOTTOM, 0);
//        }
        if(hasInit)
        {
            alignItems();
            updateScrollBarSizes();
        }
        return e;
    }

    @Override
    public void init()
    {
        super.init();
        hasInit = true;
        alignItems();
        updateScrollBarSizes();
    }

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        super.resize(mc, width, height);//code is here twice to fix resizing when init
        alignItems();
        super.resize(mc, width, height);
        alignItems();
        updateScrollBarSizes();
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTick)
    {
        setScissor();
        elements.forEach(item -> item.render(stack, mouseX, mouseY, partialTick));
        resetScissorToParent();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double dist)
    {
        if(isMouseOver(mouseX, mouseY))
        {
            boolean defaultScroll = super.mouseScrolled(mouseX, mouseY, dist);
            if(defaultScroll)
            {
                return true;
            }
            else
            {
                if(Screen.hasShiftDown())
                {
                    if(scrollHori != null)
                    {
                        scrollHori.secondHandScroll(dist);
                        return true;
                    }
                }
                else
                {
                    if(scrollVert != null)
                    {
                        scrollVert.secondHandScroll(dist);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void alignItems()
    {
        int itemHeight = getTotalItemHeight();
        int itemWidth = getMinItemWidth();

        int offsetY = 0;
        if(scrollVert != null)
        {
            offsetY = (int)(Math.max(0, itemHeight - (height - 2)) * scrollVert.scrollProg);
        }
        int offsetX = 0;
        if(scrollHori != null)
        {
            offsetX = (int)(Math.max(0, itemWidth - (width - 2)) * scrollHori.scrollProg);
        }

        int currentWidth = 1; // we draw a 1px border
        int currentHeight = 1; // we draw a 1px border
        for(Element<?> item : elements)
        {
            item.posX = currentWidth - offsetX;
            item.posY = currentHeight - offsetY;

            boolean flag = false;
            if(item.width != (width - 2))
            {
                item.width = Math.max(itemWidth, (width - 2));
                flag = true;
            }
            if(item.height != item.getMinHeight())
            {
                item.height = item.getMinHeight();
                flag = true;
            }
            if(flag)
            {
                item.constraint.apply(); // make sure we're not too big or small
            }

            currentHeight += item.getHeight();
        }
    }

    public void updateScrollBarSizes()
    {
        if(scrollVert != null)
        {
            //set the height
            int itemHeight = getTotalItemHeight();
            scrollVert.setScrollBarSize(height / (float)itemHeight); //if items height is higher than ours, scroll bar should appear
        }
        if(scrollHori != null)
        {
            //set the width
            int itemWidth = getMinItemWidth();
            scrollHori.setScrollBarSize(width / (float)itemWidth); //if items height is higher than ours, scroll bar should appear
        }
    }

    public int getTotalItemHeight()
    {
        int itemHeight = 0;
        for(Element<?> item : elements)
        {
            itemHeight += item.height;
        }
        return itemHeight;
    }

    public int getMinItemWidth()
    {
        int itemWidth = 0;
        for(Element<?> item : elements)
        {
            if(item.getMinWidth() > itemWidth)
            {
                itemWidth = item.getMinWidth();
            }
        }
        return itemWidth;
    }

    @Override
    public List<? extends Element<?>> children()
    {
        return elements;
    }

    @Override
    public boolean requireScissor()
    {
        return true;
    }

    @Override
    public boolean changeFocus(boolean direction) //we can't change focus on this
    {
        return false;
    }

    @Override
    public int getMinWidth()
    {
        if(scrollHori != null)
        {
            return 14;
        }
        return super.getMinWidth();
    }

    @Override
    public int getMinHeight()
    {
        if(scrollVert != null)
        {
            return 14;
        }
        return super.getMinHeight();
    }

    @Override
    public int getBorderSize()
    {
        return 0;
    }
}
