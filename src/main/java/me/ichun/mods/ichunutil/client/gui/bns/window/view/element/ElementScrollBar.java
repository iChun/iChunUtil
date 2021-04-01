package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class ElementScrollBar<T extends ElementScrollBar> extends Element
{
    public enum Orientation
    {
        VERTICAL,
        HORIZONTAL
    }

    public final Orientation orientation;
    private float scrollBarSize;
    public Consumer<T> callback;
    public float scrollProg;
    public boolean resizing;

    public MousePos pos;

    public ElementScrollBar(@Nonnull Fragment parent, Orientation orientation, float scrollBarSize)
    {
        super(parent);
        this.orientation = orientation;
        this.scrollBarSize = scrollBarSize;
    }

    public T setCallback(Consumer<T> callback)
    {
        this.callback = callback;
        return (T)this;
    }

    public void setScrollBarSize(float f)
    {
        f = Math.min(f, 1.01F);

        float oldSize = scrollBarSize;

        scrollBarSize = f;
        float size = (orientation == Orientation.VERTICAL ? height : width) * f;
        if(size < 4) // less than 4 pixels
        {
            scrollBarSize = 4F / getDistance();
        }

        updateSize(oldSize);
    }

    public float getScrollbarSize()
    {
        return scrollBarSize;
    }

    public void setScrollProg(float f)
    {
        float scroll = MathHelper.clamp(f, 0F, 1F);
        if(scroll != scrollProg)
        {
            scrollProg = scroll;
            if(callback != null)
            {
                callback.accept((T)this);
            }
        }
    }

    private void updateSize(float oldSize)
    {
        if(scrollBarSize > 1F)
        {
            switch(orientation)
            {
                case VERTICAL:
                {
                    width = 0;
                    break;
                }
                case HORIZONTAL:
                {
                    height = 0;
                    break;
                }
            }
            setScrollProg(0F);
        }
        else
        {
            setScrollProg(scrollProg / oldSize * scrollBarSize); //oldScrollProg / oldSize = newScrollProg / newSize
        }

        if(!resizing && (oldSize <= 1F && scrollBarSize > 1F || scrollBarSize <= 1F && oldSize > 1F))
        {
            resizing = true;
            constraint.apply();

            parentFragment.resize(getWorkspace().getMinecraft(), parentFragment.getParentWidth(), parentFragment.getParentHeight());
            resizing = false;
        }
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTick)
    {
        if(width <= 0 || height <= 0)
        {
            return;
        }

        int scrollBar = (int)(getDistance() * scrollBarSize); // the size of the scroll bar over the entire
        int space = getDistance() - scrollBar; //how much space we have.
        int preSpace = (int)(space * scrollProg);

        if(renderMinecraftStyle())
        {
            bindTexture(Fragment.VANILLA_TAB_ITEMS);

            if(orientation == Orientation.VERTICAL)
            {
                //draw scroll bar slot
                int i = height - 6;
                int x = getTop() + 3;
                while(i > 0)
                {
                    int dist = Math.min(i, 106);
                    RenderHelper.draw(stack, getLeft(), x, 14, dist, 0, 174D / 256D, 188D / 256D, 20D / 256D, 126D / 256D); //draw body
                    i -= dist;
                    x += dist;
                }

                RenderHelper.draw(stack, getLeft(), getTop()       , 14, 3, 0, 174D / 256D, 188D / 256D, 17D / 256D, 20D / 256D); //draw top
                RenderHelper.draw(stack, getLeft(), getBottom() - 3, 14, 3, 0, 174D / 256D, 188D / 256D, 126D / 256D, 129D / 256D); //draw bottom

                //draw scroll bar
                bindTexture(Fragment.VANILLA_TABS);

                //x, y, width, height
                //getLeft(), getTop() + preSpace, 14, scrollBar

                i = scrollBar - 7 - 2;
                x = getTop() + preSpace + 4 + 1;
                while(i > 0)
                {
                    int dist = Math.min(i, 8);
                    RenderHelper.draw(stack, getLeft() + 1, x, 12, dist, 0, 232D / 256D, 244D / 256D, 4D / 256D, (4 + dist) / 256D); //draw body
                    i -= dist;
                    x += dist;
                }

                RenderHelper.draw(stack, getLeft() + 1, getTop() + preSpace + 1, 12, 4, 0, 232D / 256D, 244D / 256D, 0D / 256D, 4D / 256D); //draw top of scroll
                RenderHelper.draw(stack, getLeft() + 1, getTop() + preSpace + scrollBar - 3 - 1, 12, 3, 0, 232D / 256D, 244D / 256D, 12D / 256D, 15D / 256D); //draw bottom of scroll
            }
            else
            {
                //draw scroll bar slot
                int i = width - 6;
                int x = getLeft() + 3;
                while(i > 0)
                {
                    int dist = Math.min(i, 106);
                    draw(stack, x, getTop(), dist, 14, 0, 174D / 256D, 188D / 256D, 20D / 256D, 126D / 256D); //draw body
                    i -= dist;
                    x += dist;
                }

                draw(stack, getLeft(), getTop(), 3, 14, 0, 174D / 256D, 188D / 256D, 17D / 256D, 20D / 256D); //draw top
                draw(stack, getRight() - 3, getTop(), 3, 14, 0, 174D / 256D, 188D / 256D, 126D / 256D, 129D / 256D); //draw bottom

                //draw scroll bar
                bindTexture(Fragment.VANILLA_TABS);

                //x, y, width, height
                //getLeft() + preSpace, getTop(), scrollBar, 14
                i = scrollBar - 7 - 2;
                x = getLeft() + preSpace + 4 + 1;
                while(i > 0)
                {
                    int dist = Math.min(i, 8);
                    draw(stack, x, getTop() + 1, dist, 12, 0, 232D / 256D, 244D / 256D, 4D / 256D, (4 + dist) / 256D); //draw body
                    i -= dist;
                    x += dist;
                }

                draw(stack, getLeft() + preSpace + 1, getTop() + 1, 4, 12, 0, 232D / 256D, 244D / 256D, 0D / 256D, 4D / 256D); //draw top of scroll
                draw(stack, getLeft() + preSpace + scrollBar - 3 - 1, getTop() + 1, 3, 12, 0, 232D / 256D, 244D / 256D, 12D / 256D, 15D / 256D); //draw bottom of scroll
            }
        }
        else
        {
            //draw bg
            fill(stack, getTheme().elementTreeScrollBar, 0);
            if(orientation == Orientation.VERTICAL)
            {
                //draw track
                RenderHelper.drawColour(stack, getTheme().elementTreeScrollBarBorder[0], getTheme().elementTreeScrollBarBorder[1], getTheme().elementTreeScrollBarBorder[2], 255, getLeft() + 6, getTop() + 4, 2, height - 8, 0);

                //draw bar
                RenderHelper.drawColour(stack, getTheme().elementTreeScrollBarBorder[0], getTheme().elementTreeScrollBarBorder[1], getTheme().elementTreeScrollBarBorder[2], 255, getLeft(), getTop() + preSpace, 14, scrollBar, 0);
                RenderHelper.drawColour(stack, getTheme().elementTreeScrollBar[0], getTheme().elementTreeScrollBar[1], getTheme().elementTreeScrollBar[2], 255, getLeft() + 1, getTop() + preSpace + 1, 12, scrollBar - 2, 0);
            }
            else
            {
                //draw track
                RenderHelper.drawColour(stack, getTheme().elementTreeScrollBarBorder[0], getTheme().elementTreeScrollBarBorder[1], getTheme().elementTreeScrollBarBorder[2], 255, getLeft() + 4, getTop() + 6, width - 8, 2, 0);

                //draw bar
                RenderHelper.drawColour(stack, getTheme().elementTreeScrollBarBorder[0], getTheme().elementTreeScrollBarBorder[1], getTheme().elementTreeScrollBarBorder[2], 255, getLeft() + preSpace, getTop(), scrollBar, 14, 0);
                RenderHelper.drawColour(stack, getTheme().elementTreeScrollBar[0], getTheme().elementTreeScrollBar[1], getTheme().elementTreeScrollBar[2], 255, getLeft() + preSpace + 1, getTop() + 1, scrollBar - 2, 12, 0);
            }
        }
    }

    public int getDistance()
    {
        return orientation == Orientation.VERTICAL ? height : width;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(isMouseOver(mouseX, mouseY))
        {
            pos = new MousePos((int)mouseX, (int)mouseY);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double distX, double distY)
    {
        if(pos != null)
        {
            int moved;
            if(orientation == Orientation.VERTICAL)
            {
                moved = (int)mouseY - pos.y;
            }
            else
            {
                moved = (int)mouseX - pos.x;
            }

            if(moved != 0)
            {
                setScrollProg(scrollProg + (moved / (getDistance() * (1.0F - scrollBarSize))));
            }

            pos.x = (int)mouseX;
            pos.y = (int)mouseY;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double dist)
    {
        if(isMouseOver(mouseX, mouseY) && scrollBarSize < 1F)
        {
            if(Screen.hasShiftDown())
            {
                setScrollProg((float)dist * -100F);
            }
            else if(Screen.hasControlDown())
            {
                setScrollProg(scrollProg + (float)(dist * -(1 / 100D)));
            }
            else
            {
                secondHandScroll(dist);
            }
            return true;
        }
        return false;
    }

    public void secondHandScroll(double dist)
    {
        setScrollProg(scrollProg + (float)(dist * -(1 / 10D)));
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        pos = null;
        super.mouseReleased(mouseX, mouseY, button); // unsets dragging;
        parentFragment.setListener(null); //we're a one time click, stop focusing on us
        return getListener() != null && getListener().mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean changeFocus(boolean direction) //we can't change focus on this
    {
        return false;
    }

    @Override
    public int getMinWidth()
    {
        return orientation == Orientation.VERTICAL && scrollBarSize < 1F ? 14 : 0;
    }

    @Override
    public int getMinHeight()
    {
        return orientation == Orientation.HORIZONTAL && scrollBarSize < 1F ? 14 : 0;
    }

    @Override
    public int getMaxWidth()
    {
        return orientation == Orientation.VERTICAL && scrollBarSize < 1F ? 14 : orientation == Orientation.HORIZONTAL ? 10000 : 0;
    }

    @Override
    public int getMaxHeight()
    {
        return orientation == Orientation.HORIZONTAL && scrollBarSize < 1F ? 14 : orientation == Orientation.VERTICAL ? 10000 : 0;
    }

    public static void draw(MatrixStack stack, double posX, double posY, double width, double height, double zLevel, double u1, double u2, double v1, double v2)
    {
        Matrix4f matrix = stack.getLast().getMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(matrix, (float)posX, (float)(posY + height), (float)zLevel)          .tex((float)u2, (float)v1).endVertex();
        bufferbuilder.pos(matrix, (float)(posX + width), (float)(posY + height), (float)zLevel).tex((float)u2, (float)v2).endVertex();
        bufferbuilder.pos(matrix, (float)(posX + width), (float)posY, (float)zLevel)           .tex((float)u1, (float)v2).endVertex();
        bufferbuilder.pos(matrix, (float)posX, (float)posY, (float)zLevel)                     .tex((float)u1, (float)v1).endVertex();
        tessellator.draw();
    }
}
