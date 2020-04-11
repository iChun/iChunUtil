package me.ichun.mods.ichunutil.client.gui.bns.window;

import me.ichun.mods.ichunutil.client.gui.bns.Theme;
import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public abstract class Window<M extends IWindows> extends Fragment
{
    public Supplier<Integer> borderSize;;
    public Supplier<Integer> titleSize = () -> borderSize.get() + 10;

    public @Nonnull final M parent;
    public @Nonnull List<View> views;
    public @Nonnull View currentView; //except for WindowDock

    public EdgeGrab edgeGrab; //set if a corner is grabbed

    //docked stuff
    private boolean showTitle = true;
    private boolean canDrag = true;
    private boolean canDragResize = true;
    private boolean canBringToFront = true;
    private boolean canBeDocked = true;
    private boolean canBeUndocked = true;
    //TODO ID for remembering docked windows and positions?

    public Window(M parent)
    {
        super(null);
        this.parent = parent;
        this.views = new ArrayList<>();
        borderSize = () -> (parent.isDocked(this) ? 1 : 0) + (renderMinecraftStyle() ? 4 : 3);
    }

    public <T extends Window> T pos(int x, int y)
    {
        posX = x;
        posY = y;
        return (T)this;
    }

    public <T extends Window> T size(int width, int height)
    {
        this.width = width;
        this.height = height;
        return (T)this;
    }

    public <T extends Window> T disableTitle()
    {
        showTitle = false;
        return (T)this;
    }

    public <T extends Window> T disableDrag()
    {
        canDrag = false;
        return (T)this;
    }

    public <T extends Window> T disableDragResize()
    {
        canDragResize = false;
        return (T)this;
    }

    public <T extends Window> T disableBringToFront()
    {
        canBringToFront = false;
        return (T)this;
    }

    public <T extends Window> T disableDocking()
    {
        canBeDocked = false;
        return (T)this;
    }

    public <T extends Window> T disableUndocking()
    {
        canBeUndocked = false;
        return (T)this;
    }

    @Override
    public void init()
    {
        //TODO check for special constraints
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

    public boolean canShowTitle()
    {
        return showTitle;
    }

    public boolean hasTitle()
    {
        return canShowTitle() && !currentView.title.isEmpty();
    }

    public boolean canDrag()
    {
        return canDrag;
    }

    public boolean canDragResize()
    {
        return canDragResize;
    }

    public boolean canBringToFront()
    {
        return canBringToFront;
    }

    public boolean canBeDocked() { return canBeDocked; }

    public boolean canBeUndocked() { return canBeUndocked; }

    @Override
    public void render(int mouseX, int mouseY, float partialTick)
    {
        setScissor();

        //render our background
        renderBackground();
        if(hasTitle())
        {
            drawString(currentView.title, getLeft() + borderSize.get() + 1, getTop() + borderSize.get());
        }

        //render the current view
        currentView.render(mouseX, mouseY, partialTick);

        endScissor();
    }

    public void renderBackground()
    {
        if(renderMinecraftStyle())
        {
            //draw the corners
            bindTexture(Fragment.VANILLA_TABS);

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
            fill(getTheme().windowBorder, 0);
        }
    }

    @Override
    public Workspace getWorkspace()
    {
        return (Workspace)parent;
    }

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        currentView.resize(mc, this.width, this.height);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(isMouseOver(mouseX, mouseY)) //only return true if we're clicking on us
        {
            if(button == 0 && (canDrag() || canDragResize())) //dragging
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
                    setDragging(true);
                }
            }

            if(edgeGrab == null) //we're not grabbing the window
            {
                super.mouseClicked(mouseX, mouseY, button); //this calls setDragging();
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if(edgeGrab != null)
        {
            if(edgeGrab.titleGrab && canBeDocked() && !parent.isDocked(this))
            {
                Constraint.Property.Type dockType = parent.dockType(mouseX, mouseY);
                if(dockType != null)
                {
                    parent.addToDock(this, dockType);
                }
            }
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
                    if(parent.isDocked(this) && canBeUndocked())
                    {
                        parent.removeFromDock(this);
                    }

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
                resize(Minecraft.getInstance(), parent.getWidth(), parent.getHeight());
            }
            return true; //drag is handled
        }
        return super.mouseDragged(mouseX, mouseY, button, distX, distY);
    }

    @Override
    public boolean mouseScrolled(double mouseY, double mouseZ, double amount)
    {
        return super.mouseScrolled(mouseY, mouseZ, amount);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return !parent.isObstructed(this, mouseX, mouseY) && isMouseBetween(mouseX, getLeft(), getLeft() + width) && isMouseBetween(mouseY, getTop(), getTop() + height);
    }

    @Override
    public boolean changeFocus(boolean direction)
    {
        if(parent.getFocused() == this)
        {
            return super.changeFocus(direction); //TODO make sure our children is just the current view
        }
        return false; //we're not focused anyway, so, nah
    }

    @Override
    public boolean requireScissor()
    {
        return true;
    }

    @Override
    public void resetScissorToParent()
    {
        endScissor();
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
        return parent.getWidth();
    }

    @Override
    public int getParentHeight()
    {
        return parent.getHeight();
    }

    @Override
    public Theme getTheme()
    {
        return parent.getTheme();
    }

    @Override
    public boolean renderMinecraftStyle()
    {
        return parent.renderMinecraftStyle();
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
