package me.ichun.mods.ichunutil.client.gui.bns.window;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.ichun.mods.ichunutil.client.gui.bns.Theme;
import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.util.Util;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint.Property.Type.*;

@SuppressWarnings("unchecked")
public abstract class Window<M extends IWindows> extends Fragment
{
    public Supplier<Integer> borderSize;
    public Supplier<Integer> titleSize = () -> borderSize.get() + 10;

    public @Nonnull final M parent;
    public @Nonnull List<View<?>> views;
    public View<? extends Window<?>> currentView; //should never be null (except for WindowDock)

    public EdgeGrab edgeGrab; //set if a corner is grabbed

    //docked stuff
    private boolean showTitle = true;
    private boolean canDrag = true;
    private boolean canDragResize = true;
    private boolean canBringToFront = true;
    private boolean canBeDocked = true;
    private boolean canBeUndocked = true;
    private boolean canDockStack = true;
    private boolean isUnique = true;
    //TODO ID for remembering docked windows and positions?

    public Window(M parent)
    {
        super(null);
        this.parent = parent;
        this.views = new ArrayList<>();
        borderSize = () -> (parent.isDocked(this) ? 1 : 0) + (renderMinecraftStyle() ? 4 : 3);
    }

    public <T extends Window<?>> T pos(int x, int y)
    {
        posX = x;
        posY = y;
        return (T)this;
    }

    public <T extends Window<?>> T size(int width, int height)
    {
        this.width = width;
        this.height = height;
        return (T)this;
    }

    public <T extends Window<?>> T setBorderSize(Supplier<Integer> borderSize)
    {
        this.borderSize = borderSize;
        return (T)this;
    }

    public <T extends Window<?>> T disableTitle()
    {
        showTitle = false;
        return (T)this;
    }

    public <T extends Window<?>> T disableDrag()
    {
        canDrag = false;
        return (T)this;
    }

    public <T extends Window<?>> T disableDragResize()
    {
        canDragResize = false;
        return (T)this;
    }

    public <T extends Window<?>> T disableBringToFront()
    {
        canBringToFront = false;
        return (T)this;
    }

    public <T extends Window<?>> T disableDockingEntirely()
    {
        canDockStack = canBeUndocked = canBeDocked = false;
        return (T)this;
    }

    public <T extends Window<?>> T disableDocking()
    {
        canBeDocked = false;
        return (T)this;
    }

    public <T extends Window<?>> T disableUndocking()
    {
        canBeUndocked = false;
        return (T)this;
    }

    public <T extends Window<?>> T disableDockStacking()
    {
        canDockStack = false;
        return (T)this;
    }

    public <T extends Window<?>> T isNotUnique() //you're plainer than a plain white tee
    {
        isUnique = false;
        return (T)this;
    }

    @Override
    public void init()
    {
        constraint.apply();
        views.forEach(Fragment::init);
    }

    @Override
    public List<View<?>> getEventListeners()
    {
        return views;
    }

    public void setView(View<?> v)
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

    public boolean canDockStack() { return canDockStack; }

    public boolean isUnique() { return isUnique; }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTick)
    {
        //render dock highlight
        renderDockHighlight(stack, mouseX, mouseY, partialTick);

        setScissor();

        //render our background
        renderBackground(stack);
        if(hasTitle())
        {
            drawString(stack, currentView.title, getLeft() + borderSize.get() + 1, getTop() + borderSize.get());
        }

        //render the current view
        currentView.render(stack, mouseX, mouseY, partialTick);

        endScissor();
    }

    public void renderDockHighlight(MatrixStack stack, int mouseX, int mouseY, float partialTick)
    {
        if(getWorkspace().canDockWindows() && getWorkspace().getListener() == this  && getWorkspace().isDragging() && (canBeDocked() || canDockStack()) && edgeGrab != null && edgeGrab.titleGrab)
        {
            WindowDock<?> dock = getWorkspace().getDock();

            //Render BORDER HIGHLIGHT
            double left = 0;
            double top = 0;
            double right = getWorkspace().getWidth();
            double bottom = getWorkspace().getHeight();
            for(Map.Entry<WindowDock.ArrayListHolder, Constraint.Property.Type> e : dock.docked.entrySet())
            {
                for(Window<?> key : e.getKey().windows)
                {
                    Constraint.Property.Type value = e.getValue();
                    switch(value)
                    {
                        case LEFT:
                        {
                            if(key.getRight() > left)
                            {
                                left = key.getRight();
                            }
                            break;
                        }
                        case TOP:
                        {
                            if(key.getBottom() > top)
                            {
                                top = key.getBottom();
                            }
                            break;
                        }
                        case RIGHT:
                        {
                            if(key.getLeft() < right)
                            {
                                right = key.getLeft();
                            }
                            break;
                        }
                        case BOTTOM:
                        {
                            if(key.getTop() < bottom)
                            {
                                bottom = key.getTop();
                            }
                            break;
                        }
                    }
                }
            }

            Window<?> window = this;
            int oriX = window.posX;
            int oriY = window.posY;
            window.pos(-10000, -10000);
            IWindows.DockInfo info = dock.getDockInfo(mouseX, mouseY, window.canDockStack());
            window.pos(oriX, oriY);

            boolean draw = info != null && info.window != null;
            if(draw)
            {
                left = info.window.getLeft();
                right = info.window.getRight();
                top = info.window.getTop();
                bottom = info.window.getBottom();
            }
            else if(canBeDocked() && !getWorkspace().isDocked(this))
            {
                HashSet<Constraint.Property.Type> disabledDocks = getWorkspace().getDock().disabledDocks;

                int dockSnap = iChunUtil.configClient.guiDockBorder;
                if(mouseY >= top && mouseY < bottom)
                {
                    if(mouseX >= left && mouseX < left + dockSnap && !disabledDocks.contains(LEFT))
                    {
                        right = left + dockSnap;
                        draw = true;
                    }
                    else if(mouseX >= right - dockSnap && mouseX < right && !disabledDocks.contains(RIGHT))
                    {
                        left = right - dockSnap;
                        draw = true;
                    }
                }
                if(mouseX >= left && mouseX < right)
                {
                    if(mouseY >= top && mouseY < top + dockSnap && !disabledDocks.contains(TOP))
                    {
                        bottom = top + dockSnap;
                        draw = true;
                    }
                    else if(mouseY >= bottom - dockSnap && bottom < right && !disabledDocks.contains(BOTTOM))
                    {
                        top = bottom - dockSnap;
                        draw = true;
                    }
                }
            }
            if(draw)
            {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                if(renderMinecraftStyle()) //glint render taken from 1.12.2 RenderItem.renderEffect //TODO test that this doesn't uses matrix stack
                {
                    float scale = 8;
                    float scaleTex = 512F;
                    RenderSystem.depthMask(false);
                    RenderSystem.depthFunc(514);
                    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
                    Fragment.bindTexture(ItemRenderer.RES_ITEM_GLINT);
                    RenderSystem.matrixMode(5890);
                    RenderSystem.pushMatrix();
                    RenderSystem.scalef((float)scale, (float)scale, (float)scale);
                    float f = (float)(Util.milliTime() % 3000L) / 3000.0F / (float)scale;
                    RenderSystem.translatef(f, 0.0F, 0.0F);
                    RenderSystem.rotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                    RenderHelper.draw(stack, left, top, right - left, bottom - top, 0, left / scaleTex, right / scaleTex, top / scaleTex, bottom / scaleTex);
                    RenderSystem.popMatrix();
                    RenderSystem.pushMatrix();
                    RenderSystem.scalef((float)scale, (float)scale, (float)scale);
                    float f1 = (float)(Util.milliTime() % 4873L) / 4873.0F / (float)scale;
                    RenderSystem.translatef(-f1, 0.0F, 0.0F);
                    RenderSystem.rotatef(10.0F, 0.0F, 0.0F, 1.0F);
                    RenderHelper.draw(stack, left, top, right - left, bottom - top, 0, left / scaleTex, right / scaleTex, top / scaleTex, bottom / scaleTex);
                    RenderSystem.popMatrix();
                    RenderSystem.matrixMode(5888);
                    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                    RenderSystem.depthFunc(515);
                    RenderSystem.depthMask(true);
                }
                else
                {
                    RenderHelper.drawColour(stack, getTheme().tabBorder[0], getTheme().tabBorder[1], getTheme().tabBorder[2], 150, left, top, right - left, bottom - top, 0);
                }
                RenderSystem.disableBlend();
            }
            //END RENDER BORDER HIGHLIGHT
        }
    }


    public void renderBackground(MatrixStack stack)
    {
        if(renderMinecraftStyle())
        {
            RenderSystem.enableAlphaTest();
            //draw the corners
            bindTexture(Fragment.VANILLA_TABS);

            //fill space
            RenderHelper.draw(stack, getLeft() + 4, getTop() + 4, width - 8, height - 8, 0, 4D/256D, 24D/256D, 36D/256D, 60D/256D); //fill space

            //draw borders
            RenderHelper.draw(stack, getLeft(), getTop() + 4, 4, height - 8, 0, 0D/256D, 4D/256D, 36D/256D, 60D/256D); //left border
            RenderHelper.draw(stack, getLeft() + 4, getTop(), width - 8, 4, 0, 4D/256D, 24D/256D, 32D/256D, 36D/256D); //top border
            RenderHelper.draw(stack, getRight() - 4, getTop() + 4, 4, height - 8, 0, 24D/256D, 28D/256D, 36D/256D, 60D/256D); //right border
            RenderHelper.draw(stack, getLeft() + 4, getBottom() - 4, width - 8, 4, 0, 4D/256D, 24D/256D, 124D/256D, 128D/256D); //bottom left

            //draw corners
            RenderHelper.draw(stack, getLeft(), getTop(), 4, 4, 0, 0D/256D, 4D/256D, 32D/256D, 36D/256D); //top left
            RenderHelper.draw(stack, getRight() - 4, getTop(), 4, 4, 0, 24D/256D, 28D/256D, 32D/256D, 36D/256D); //top right
            RenderHelper.draw(stack, getLeft(), getBottom() - 4, 4, 4, 0, 0D/256D, 4D/256D, 124D/256D, 128D/256D); //bottom left
            RenderHelper.draw(stack, getRight() - 4, getBottom() - 4, 4, 4, 0, 24D/256D, 28D/256D, 124D/256D, 128D/256D); //bottom left
        }
        else
        {
            fill(stack, getTheme().windowBorder, 0);
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
        constraint.apply();
        currentView.resize(mc, this.width, this.height);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(isMouseOver(mouseX, mouseY)) //only return true if we're clicking on us
        {
            if(button == 0 && (canDrag() || canDragResize())) //dragging
            {
                boolean isDocked = parent.isDocked(this);
                EdgeGrab grab = new EdgeGrab(
                        (!isDocked || (!constraint.hasLeft() || parent.sameDockStack(this, constraint.get(Constraint.Property.Type.LEFT).getReference()))) && isMouseBetween(mouseX, getLeft(), getLeft() + borderSize.get()),
                        (!isDocked || (!constraint.hasRight() || parent.sameDockStack(this, constraint.get(Constraint.Property.Type.RIGHT).getReference()))) && isMouseBetween(mouseX, getRight() - borderSize.get(), getRight()),
                        (!isDocked || (!constraint.hasTop() || parent.sameDockStack(this, constraint.get(Constraint.Property.Type.TOP).getReference()))) && isMouseBetween(mouseY, getTop(), getTop() + borderSize.get()),
                        (!isDocked || (!constraint.hasBottom() || parent.sameDockStack(this, constraint.get(Constraint.Property.Type.BOTTOM).getReference()))) && isMouseBetween(mouseY, getBottom() - borderSize.get(), getBottom()),
                        (!isDocked || canBeUndocked()) && isMouseBetween(mouseY, getTop() + borderSize.get(), getTop() + titleSize.get()) && hasTitle(),
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
                int oriX = posX;
                int oriY = posY;
                pos(-10000, -10000);
                IWindows.DockInfo dockInfo = parent.getDockInfo(mouseX, mouseY, canDockStack());
                pos(oriX, oriY);
                if(dockInfo != null)
                {
                    if(dockInfo.window != null)
                    {
                        parent.addToDocked(dockInfo.window, this);
                    }
                    else
                    {
                        parent.addToDock(this, dockInfo.type);
                    }
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
                        int oriX = posX;
                        int oriY = posY;
                        int oriWidth = width;
                        parent.removeFromDock(this);
                        posX += oriX - posX + ((oriWidth - width) / 2);
                        posY += oriY - posY;
                    }

                    posX -= edgeGrab.x - (int)mouseX;
                    posY -= edgeGrab.y - (int)mouseY;
                    edgeGrab.x = (int)mouseX;
                    edgeGrab.y = (int)mouseY;
                }
            }
            else if(canDragResize())
            {
                if(parent.isDocked(this))
                {
                    dragResize(mouseX, mouseY, edgeGrab);

                    if(edgeGrab.left)
                    {
                        getWorkspace().getDock().edgeGrab(this, mouseX, mouseY, new EdgeGrab(true, false, false, false, false, edgeGrab.x, edgeGrab.y));
                    }
                    if(edgeGrab.right)
                    {
                        getWorkspace().getDock().edgeGrab(this, mouseX, mouseY, new EdgeGrab(false, true, false, false, false, edgeGrab.x, edgeGrab.y));
                    }
                    if(edgeGrab.top)
                    {
                        getWorkspace().getDock().edgeGrab(this, mouseX, mouseY, new EdgeGrab(false, false, true, false, false, edgeGrab.x, edgeGrab.y));
                    }
                    if(edgeGrab.bottom)
                    {
                        getWorkspace().getDock().edgeGrab(this, mouseX, mouseY, new EdgeGrab(false, false, false, true, false, edgeGrab.x, edgeGrab.y));
                    }
                }
                else
                {
                    dragResize(mouseX, mouseY, edgeGrab);
                }
            }
            return true; //drag is handled
        }
        return super.mouseDragged(mouseX, mouseY, button, distX, distY);
    }

    public void dragResize(double mouseX, double mouseY, EdgeGrab grab)
    {
        int left = getLeft();
        int right = getRight();
        int top = getTop();
        int bottom = getBottom();
        if(grab.left)
        {
            setLeft((int)mouseX);
            setRight(right);
        }
        else if(grab.right)
        {
            setRight((int)mouseX);
        }
        if(grab.top)
        {
            setTop((int)mouseY);
            setBottom(bottom);
        }
        else if(grab.bottom)
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

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount)
    {
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return !parent.isObstructed(this, mouseX, mouseY) && isMouseBetween(mouseX, getLeft(), getLeft() + width) && isMouseBetween(mouseY, getTop(), getTop() + height);
    }

    @Override
    public boolean changeFocus(boolean direction)
    {
        if(parent.getListener() == this)
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


    public static class EdgeGrab
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
