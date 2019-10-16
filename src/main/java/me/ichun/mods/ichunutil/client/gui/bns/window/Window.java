package me.ichun.mods.ichunutil.client.gui.bns.window;

import me.ichun.mods.ichunutil.client.gui.bns.Theme;
import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public abstract class Window<M extends Workspace> extends Fragment
        implements IRenderable
{
    public Supplier<Integer> borderSize = () -> renderMinecraftStyle() ? 4 : 3;
    public Supplier<Integer> titleSize = () -> renderMinecraftStyle() ? 0 : borderSize.get() + 10;

    //TODO how to handle unfocusing
    public @Nonnull final M parent;
    public @Nonnull final String title;
    public @Nonnull List<View> views;
    public @Nonnull View currentView;

    public EdgeGrab edgeGrab; //set if a corner is grabbed

    public Window(M parent, String s)
    {
        super(null);
        this.parent = parent;
        this.title = I18n.format(s);
        this.views = new ArrayList<>();
    }

    public <T extends Window> T setPos(int x, int y)
    {
        posX = x;
        posY = y;
        return (T)this;
    }

    public <T extends Window> T setSize(int width, int height)
    {
        this.width = width;
        this.height = height;
        return (T)this;
    }

    @Override
    public void init()
    {
        //TODO constraints applying?
        views.forEach(Fragment::init);
    }

    @Override
    public List<View> children()
    {
        return views;
    }

    public void setView(View v)
    {
        this.views.add(v);
        this.currentView = v;
    }

    public boolean hasTitle()
    {
        return !title.isEmpty();
    }

    public boolean canDrag()
    {
        return true;
    }

    public boolean canDragResize()
    {
        return true;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick)
    {
        int width = getRight() - getLeft();
        int height = getBottom() - getTop();

        RenderHelper.startGlScissor(getLeft(), getTop(), width, height);

        //render our background
        renderBackground();
        if(hasTitle())
        {
            drawString(title, getLeft() + borderSize.get() + 1, getTop() + (renderMinecraftStyle() ? borderSize.get() : 3), Theme.getAsHex(getTheme().font));
        }

        //render the current view
        currentView.render(mouseX, mouseY, partialTick);

        RenderHelper.endGlScissor();
    }

    public void renderBackground()
    {
        if(renderMinecraftStyle())
        {
            //draw the corners
            parent.getMinecraft().getTextureManager().bindTexture(Fragment.VANILLA_TABS);

            //fill space
            RenderHelper.draw(getLeft() + 4, getTop() + 4, width - 8, height - 8, 0, 4D/256D, 24D/256D, 36D/256D, 60D/256D); //fill space

            //draw borders
            RenderHelper.draw(getLeft(), getTop() + 4, 4, height - 8, 0, 0D/256D, 4D/256D, 36D/256D, 60D/256D); //left border
            RenderHelper.draw(getLeft() + 4, getTop(), width - 8, 4, 0, 4D/256D, 24D/256D, 32D/256D, 36D/256D); //top border
            RenderHelper.draw(getRight() - 4, getTop() + 4, 4, height - 8, 0, 24D/256D, 28D/256D, 36D/256D, 60D/256D); //right border
            RenderHelper.draw(getLeft() + 4, getBottom() - 4, width - 8, 4, 0, 4D/256D, 24D/256D, 124D/256D, 128D/256D); //bottom left

            //draw corners
            RenderHelper.draw(getLeft(), getTop(), 4, 4, 0, 0D/256D, 4D/256D, 32D/256D, 36D/256D); //top left
            RenderHelper.draw(getRight() - 4, getTop(), 4, 4, 0, 24D/256D, 28D/256D, 32D/256D, 36D/256D); //top right
            RenderHelper.draw(getLeft(), getBottom() - 4, 4, 4, 0, 0D/256D, 4D/256D, 124D/256D, 128D/256D); //bottom left
            RenderHelper.draw(getRight() - 4, getBottom() - 4, 4, 4, 0, 24D/256D, 28D/256D, 124D/256D, 128D/256D); //bottom left
        }
        else
        {
            RenderHelper.drawColour(getTheme().windowBorder[0], getTheme().windowBorder[1], getTheme().windowBorder[2], 255, getLeft(), getTop(), width, height, 0);
        }
    }

    public void resize(Minecraft mc, int width, int height)
    {
        currentView.resize(mc, this.width, this.height);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(isInBounds(mouseX, mouseY))
        {
            if(canDrag() || canDragResize())
            {
                EdgeGrab grab = new EdgeGrab(
                        isMouseBetween(mouseX, getLeft(), getLeft() + borderSize.get()),
                        isMouseBetween(mouseX, getRight() - borderSize.get(), getRight()),
                        isMouseBetween(mouseY, getTop(), getTop() + borderSize.get()),
                        isMouseBetween(mouseY, getBottom() - borderSize.get(), getBottom()),
                        isMouseBetween(mouseY, getTop() + borderSize.get(), getTop() + titleSize.get()) && hasTitle(),
                        (int)mouseX,
                        (int)mouseY
                );

                if(grab.isActive())
                {
                    edgeGrab = grab;
                }
            }

            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if(edgeGrab != null)
        {
            edgeGrab = null;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double distX, double distY)
    {
        if(edgeGrab != null) //we're dragging a corner
        {
            if(edgeGrab.titleGrab)
            {
                if(canDrag())
                {
                    posX -= edgeGrab.x - (int)mouseX;
                    posY -= edgeGrab.y - (int)mouseY;
                    edgeGrab.x = (int)mouseX;
                    edgeGrab.y = (int)mouseY;
                }
            }
            else if(canDragResize())
            {
                int left = getLeft();
                int right = getRight();
                int top = getTop();
                int bottom = getBottom();
                if(edgeGrab.left)
                {
                    setLeft((int)mouseX);
                    setRight(right);
                }
                else if(edgeGrab.right)
                {
                    setRight((int)mouseX);
                }
                if(edgeGrab.top)
                {
                    setTop((int)mouseY);
                    setBottom(bottom);
                }
                else if(edgeGrab.bottom)
                {
                    setBottom((int)mouseY);
                }
                if(width < 20)
                {
                    width = 20;
                    setLeft(left);
                }
                if(height < 20)
                {
                    height = 20;
                    setTop(top);
                }
                resize(Minecraft.getInstance(), parent.width, parent.height);
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, distX, distY);
    }

    @Override
    public boolean mouseScrolled(double mouseY, double mouseZ, double amount)
    {
        return super.mouseScrolled(mouseY, mouseZ, amount);
    }

    //Parent is not fragment. We gotta override these.
    @Override
    public int getLeft()
    {
        return posX;
    }

    @Override
    public int getRight()
    {
        return posX + width;
    }

    @Override
    public int getTop()
    {
        return posY;
    }

    @Override
    public int getBottom()
    {
        return posY + height;
    }

    @Override
    public void setLeft(int x) // this will be a the new left
    {
        this.posX = x;
    }

    @Override
    public void setRight(int x)
    {
        this.width = x - posX;
    }

    @Override
    public void setTop(int y)
    {
        this.posY = y;
    }

    @Override
    public void setBottom(int y)
    {
        this.height = y - posY;
    }

    @Override
    public int getParentWidth()
    {
        return parent.width;
    }

    @Override
    public int getParentHeight()
    {
        return parent.height;
    }

    @Override
    public Theme getTheme()
    {
        return parent.getTheme();
    }

    @Override
    public boolean renderMinecraftStyle()
    {
        return parent.renderMinecraftStyle;
    }

    @Override
    public FontRenderer getFontRenderer()
    {
        return parent.getFontRenderer();
    }


    public class EdgeGrab
    {
        boolean left;
        boolean right;
        boolean top;
        boolean bottom;
        boolean titleGrab;
        int x;
        int y;

        public EdgeGrab(boolean left, boolean right, boolean top, boolean bottom, boolean titleGrab, int x, int y)
        {
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
            this.titleGrab = titleGrab;
            this.x = x;
            this.y = y;
        }

        public boolean isActive()
        {
            return left || right || top || bottom || titleGrab;
        }
    }
}
