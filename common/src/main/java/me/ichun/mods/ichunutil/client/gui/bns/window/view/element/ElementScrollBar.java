package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import me.ichun.mods.ichunutil.client.gui.bns.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.TextureDefinition;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.function.Consumer;

public class ElementScrollBar<T extends ElementScrollBar> extends Element<Fragment<?>>
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

    public ElementScrollBar(@NotNull Fragment parent, Orientation orientation, float scrollBarSize)
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
        float scroll = Mth.clamp(f, 0F, 1F);
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

            parent.resize(getWorkspace().getMinecraft(), parent.getParentWidth(), parent.getParentHeight());
            resizing = false;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if(width <= 0 || height <= 0)
        {
            return;
        }
        PoseStack stack = graphics.pose();

        int scrollBar = Math.max(8, (int)(getDistance() * scrollBarSize)); // the size of the scroll bar over the entire
        int space = getDistance() - scrollBar; //how much space we have.
        int preSpace = (int)(space * scrollProg);

        if(renderMinecraftStyle() > 0)
        {
            bindTexture(resourceTabItems());
            TextureDefinition texdefScroller = TEXDEF_SCROLLER;

            if(orientation == Orientation.VERTICAL)
            {
                //draw scroll bar slot
                BufferBuilder bufferbuilder = RenderHelper.startDrawBatch();
                int i = height - 6;
                int x = getTop() + 3;
                while(i > 0)
                {
                    int dist = Math.min(i, 106);
                    RenderHelper.drawBatch(stack, bufferbuilder, getLeft(), x, 14, dist, 0, 174D / 256D, 188D / 256D, 20D / 256D, 126D / 256D); //draw body
                    i -= dist;
                    x += dist;
                }

                RenderHelper.drawBatch(stack, bufferbuilder, getLeft(), getTop()       , 14, 3, 0, 174D / 256D, 188D / 256D, 17D / 256D, 20D / 256D); //draw top
                RenderHelper.drawBatch(stack, bufferbuilder, getLeft(), getBottom() - 3, 14, 3, 0, 174D / 256D, 188D / 256D, 126D / 256D, 129D / 256D); //draw bottom
                RenderHelper.endDrawBatch(bufferbuilder);

                //draw scroll bar
                bindTexture(resourceScroller());

                //x, y, width, height
                //getLeft(), getTop() + preSpace, 14, scrollBar

                bufferbuilder = RenderHelper.startDrawBatch();
                i = scrollBar - 7 - 2;
                x = getTop() + preSpace + 4 + 1;
                while(i > 0)
                {
                    int dist = Math.min(i, 8);
                    RenderHelper.drawBatch(stack, bufferbuilder, getLeft() + 1, x, 12, dist, 0, texdefScroller.x1() / texdefScroller.width(), texdefScroller.x2() / texdefScroller.width(), 4D / texdefScroller.height(), (4 + dist) / texdefScroller.height()); //draw body
                    i -= dist;
                    x += dist;
                }

                RenderHelper.drawBatch(stack, bufferbuilder, getLeft() + 1, getTop() + preSpace + 1, 12, 4, 0, texdefScroller.x1() / texdefScroller.width(), texdefScroller.x2() / texdefScroller.width(), 0D / texdefScroller.height(), 4D / texdefScroller.height()); //draw top of scroll
                RenderHelper.drawBatch(stack, bufferbuilder, getLeft() + 1, getTop() + preSpace + scrollBar - 3 - 1, 12, 3, 0, texdefScroller.x1() / texdefScroller.width(), texdefScroller.x2() / texdefScroller.width(), 12D / texdefScroller.height(), 15D / texdefScroller.height()); //draw bottom of scroll
                RenderHelper.endDrawBatch(bufferbuilder);
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
                bindTexture(resourceScroller());

                //x, y, width, height
                //getLeft() + preSpace, getTop(), scrollBar, 14
                i = scrollBar - 7 - 2;
                x = getLeft() + preSpace + 4 + 1;
                while(i > 0)
                {
                    int dist = Math.min(i, 8);
                    draw(stack, x, getTop() + 1, dist, 12, 0, texdefScroller.x1() / texdefScroller.width(), texdefScroller.x2() / texdefScroller.width(), 4D / texdefScroller.height(), (4 + dist) / texdefScroller.height()); //draw body
                    i -= dist;
                    x += dist;
                }

                draw(stack, getLeft() + preSpace + 1, getTop() + 1, 4, 12, 0, texdefScroller.x1() / texdefScroller.width(), texdefScroller.x2() / texdefScroller.width(), 0D / texdefScroller.height(), 4D / texdefScroller.height()); //draw top of scroll
                draw(stack, getLeft() + preSpace + scrollBar - 3 - 1, getTop() + 1, 3, 12, 0, texdefScroller.x1() / texdefScroller.width(), texdefScroller.x2() / texdefScroller.width(), 12D / texdefScroller.height(), 15D / texdefScroller.height()); //draw bottom of scroll
            }
        }
        else
        {
            //draw bg
            fill(graphics, getTheme().elementScrollBarBackground, 0);
            if(orientation == Orientation.VERTICAL)
            {
                //draw track
                RenderHelper.drawColour(graphics, getTheme().elementScrollBarBorder, 255, getLeft() + 6, getTop() + 4, 2, height - 8, 0);

                //draw bar
                RenderHelper.drawColour(graphics, getTheme().elementScrollBarBorder, 255, getLeft(), getTop() + preSpace, 14, scrollBar, 0);
                RenderHelper.drawColour(graphics, getTheme().elementScrollBar, 255, getLeft() + 1, getTop() + preSpace + 1, 12, scrollBar - 2, 0);
            }
            else
            {
                //draw track
                RenderHelper.drawColour(graphics, getTheme().elementScrollBarBorder, 255, getLeft() + 4, getTop() + 6, width - 8, 2, 0);

                //draw bar
                RenderHelper.drawColour(graphics, getTheme().elementScrollBarBorder, 255, getLeft() + preSpace, getTop(), scrollBar, 14, 0);
                RenderHelper.drawColour(graphics, getTheme().elementScrollBar, 255, getLeft() + preSpace + 1, getTop() + 1, scrollBar - 2, 12, 0);
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
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY)
    {
        if(isMouseOver(mouseX, mouseY) && scrollBarSize < 1F)
        {
            if(Screen.hasShiftDown())
            {
                setScrollProg((float)scrollY * -100F);
            }
            else if(Screen.hasControlDown())
            {
                setScrollProg(scrollProg + (float)(scrollY * -(1 / 100D)));
            }
            else
            {
                secondHandScroll(scrollY);
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
        parent.setFocused(null); //we're a one time click, stop focusing on us
        return getFocused() != null && getFocused().mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent event) //we can't change focus on this
    {
        return null;
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

    public static void draw(PoseStack stack, double posX, double posY, double width, double height, double zLevel, double u1, double u2, double v1, double v2) //In case you're wondering, yes this function is actually different from RenderHelper's -Past iChun
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix = stack.last().pose();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix, (float)posX, (float)(posY + height), (float)zLevel)          .setUv((float)u2, (float)v1);
        bufferbuilder.addVertex(matrix, (float)(posX + width), (float)(posY + height), (float)zLevel).setUv((float)u2, (float)v2);
        bufferbuilder.addVertex(matrix, (float)(posX + width), (float)posY, (float)zLevel)           .setUv((float)u1, (float)v2);
        bufferbuilder.addVertex(matrix, (float)posX, (float)posY, (float)zLevel)                     .setUv((float)u1, (float)v1);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }
}
