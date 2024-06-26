package me.ichun.mods.ichunutil.client.gui.bns.window;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.client.gui.bns.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public abstract class Window<W extends Workspace, V extends View<?>> extends Fragment<W> //TODO have a "generic window" class, MOST of our windows don't do anything special
{
    //TODO get rid of this when working with more advanced UI eg Tabula/CCI
    public Supplier<Integer> borderSize;
    public Supplier<Integer> titleSize = () -> borderSize.get() + 10;


    @NotNull
    public W parent;

    @NotNull
    private List<V> views = new ArrayList<>(); //Not technically the children.... only the alternate views we have
    private V currentView = null; //should never be null (except for WindowDock)

    //docked stuff
    private boolean showTitle = true;
    private boolean canDrag = true;
    private boolean canDragResize = true;
    private boolean canBringToFront = true;
    private boolean canBeDocked = true;
    private boolean canBeUndocked = true;
    private boolean canDockStack = true;
    private boolean isUnique = true;

    public EdgeGrab edgeGrab; //set if a corner is grabbed

    public Window(@NotNull W parent)
    {
        super(parent);
        this.parent = parent;

        borderSize = () -> (parent.isDocked(this) ? 1 : 0) + (renderMinecraftStyle() > 0 ? 4 : 3);
    }

    //TODO get rid of this when working with more advanced UI eg Tabula/CCI
    public <T extends Window<W, V>> T setBorderSize(Supplier<Integer> borderSize)
    {
        this.borderSize = borderSize;
        return (T)this;
    }

    public <T extends Window<W, V>> T pos(int x, int y)
    {
        return (T)this.setPos(x, y);
    }

    public <T extends Window<W, V>> T size(int width, int height)
    {
        return (T)setSize(width, height);
    }

    public V getCurrentView()
    {
        return currentView;
    }

    public <T extends Window<W, V>> T setView(V v)
    {
        this.views.add(v);
        setCurrentView(v);
        return (T)this;
    }

    public <T extends Window<W, V>> T setCurrentView(V v)
    {
        this.currentView = v;
        return (T)this;
    }

    public <T extends Window<W, V>> T disableTitle()
    {
        showTitle = false;
        return (T)this;
    }

    public <T extends Window<W, V>> T disableDrag()
    {
        canDrag = false;
        return (T)this;
    }

    public <T extends Window<W, V>> T disableDragResize()
    {
        canDragResize = false;
        return (T)this;
    }

    public <T extends Window<W, V>> T disableBringToFront()
    {
        canBringToFront = false;
        return (T)this;
    }

    public <T extends Window<W, V>> T disableDockingEntirely()
    {
        canDockStack = canBeUndocked = canBeDocked = false;
        return (T)this;
    }

    public <T extends Window<W, V>> T disableDocking()
    {
        canBeDocked = false;
        return (T)this;
    }

    public <T extends Window<W, V>> T disableUndocking()
    {
        canBeUndocked = false;
        return (T)this;
    }

    public <T extends Window<W, V>> T disableDockStacking()
    {
        canDockStack = false;
        return (T)this;
    }

    public <T extends Window<W, V>> T isNotUnique() //you're plainer than a plain white tee
    {
        isUnique = false;
        return (T)this;
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
    public List<V> children()
    {
        return currentView != null ? ImmutableList.of(currentView) : views;
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
                    (!isDocked && !constraint.hasLeft() || isDocked && (!constraint.hasLeft() || parent.sameDockStack(this, constraint.get(Constraint.Property.Type.LEFT).getReference()))) && isMouseBetween(mouseX, getLeft(), getLeft() + borderSize.get()),
                    (!isDocked && !constraint.hasRight() || isDocked && (!constraint.hasRight() || parent.sameDockStack(this, constraint.get(Constraint.Property.Type.RIGHT).getReference()))) && isMouseBetween(mouseX, getRight() - borderSize.get(), getRight()),
                    (!isDocked && !constraint.hasTop() || isDocked && (!constraint.hasTop() || parent.sameDockStack(this, constraint.get(Constraint.Property.Type.TOP).getReference()))) && isMouseBetween(mouseY, getTop(), getTop() + borderSize.get()),
                    (!isDocked && !constraint.hasBottom() || isDocked && (!constraint.hasBottom() || parent.sameDockStack(this, constraint.get(Constraint.Property.Type.BOTTOM).getReference()))) && isMouseBetween(mouseY, getBottom() - borderSize.get(), getBottom()),
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
                WindowDock.DockInfo dockInfo = parent.getDockInfo(mouseX, mouseY, canDockStack());
                pos(oriX, oriY);
                if(dockInfo != null)
                {
                    if(dockInfo.window() != null)
                    {
                        parent.addToDocked(dockInfo.window(), this);
                    }
                    else
                    {
                        parent.addToDock(this, dockInfo.type());
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
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY)
    {
        return super.mouseScrolled(mouseX, mouseY, scrollY);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return !parent.isObstructed(this, mouseX, mouseY) && isMouseBetween(mouseX, getLeft(), getRight()) && isMouseBetween(mouseY, getTop(), getBottom());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if(isMouseOver(mouseX, mouseY))
        {
            if(canDrag() || canDragResize()) //dragging
            {
                boolean isDocked = parent.isDocked(this);
                EdgeGrab grab = new EdgeGrab(
                    (!isDocked && !constraint.hasLeft() || isDocked && (!constraint.hasLeft() || parent.sameDockStack(this, constraint.get(Constraint.Property.Type.LEFT).getReference()))) && isMouseBetween(mouseX, getLeft(), getLeft() + borderSize.get()),
                    (!isDocked && !constraint.hasRight() || isDocked && (!constraint.hasRight() || parent.sameDockStack(this, constraint.get(Constraint.Property.Type.RIGHT).getReference()))) && isMouseBetween(mouseX, getRight() - borderSize.get(), getRight()),
                    (!isDocked && !constraint.hasTop() || isDocked && (!constraint.hasTop() || parent.sameDockStack(this, constraint.get(Constraint.Property.Type.TOP).getReference()))) && isMouseBetween(mouseY, getTop(), getTop() + borderSize.get()),
                    (!isDocked && !constraint.hasBottom() || isDocked && (!constraint.hasBottom() || parent.sameDockStack(this, constraint.get(Constraint.Property.Type.BOTTOM).getReference()))) && isMouseBetween(mouseY, getBottom() - borderSize.get(), getBottom()),
                    (!isDocked || canBeUndocked()) && isMouseBetween(mouseY, getTop() + borderSize.get(), getTop() + titleSize.get()) && hasTitle(),
                    (int)mouseX,
                    (int)mouseY
                );

                if(grab.isActive())
                {
                    if(grab.titleGrab)
                    {
                        getWorkspace().cursorState = Workspace.CURSOR_CROSSHAIR;
                    }
                    else
                    {
                        getWorkspace().cursorState = grab.left || grab.right ? Workspace.CURSOR_HRESIZE : Workspace.CURSOR_VRESIZE;
                    }
                }
            }
        }

        //render dock highlight
        renderDockHighlight(graphics, mouseX, mouseY, partialTick);

        setScissor();

        //render our background
        renderBackground(graphics);
        if(hasTitle())
        {
            drawString(graphics, currentView.title, getLeft() + borderSize.get() + 1, getTop() + borderSize.get());
        }

        //render the current view
        currentView.render(graphics, mouseX, mouseY, partialTick);

        endScissor();
    }

    public void renderBackground(GuiGraphics graphics)
    {
        if(renderMinecraftStyle() > 0)
        {
            PoseStack stack = graphics.pose();
            //draw the corners
            bindTexture(resourceHorse());

            RenderHelper.startDrawBatch();

            //fill space
            RenderHelper.drawBatch(stack, getLeft() + 4, getTop() + 4, width - 8, height - 8, 0, 82D/256D, 166D/256D, 20D/256D, 68D/256D); //fill space

            //draw borders
            RenderHelper.drawBatch(stack, getLeft(), getTop() + 4, 4, height - 8, 0, 0D/256D, 4D/256D, 4D/256D, 162D/256D); //left border
            RenderHelper.drawBatch(stack, getLeft() + 4, getTop(), width - 8, 4, 0, 4D/256D, 172D/256D, 0D/256D, 4D/256D); //top border
            RenderHelper.drawBatch(stack, getRight() - 4, getTop() + 4, 4, height - 8, 0, 172D/256D, 176D/256D, 4D/256D, 162D/256D); //right border
            RenderHelper.drawBatch(stack, getLeft() + 4, getBottom() - 4, width - 8, 4, 0, 4D/256D, 172D/256D, 162D/256D, 166D/256D); //bottom border

            //draw corners
            RenderHelper.drawBatch(stack, getLeft(), getTop(), 4, 4, 0, 0D/256D, 4D/256D, 0D/256D, 4D/256D); //top left
            RenderHelper.drawBatch(stack, getRight() - 4, getTop(), 4, 4, 0, 172D/256D, 176D/256D, 0D/256D, 4D/256D); //top right
            RenderHelper.drawBatch(stack, getLeft(), getBottom() - 4, 4, 4, 0, 0D/256D, 4D/256D, 162D/256D, 166D/256D); //bottom left
            RenderHelper.drawBatch(stack, getRight() - 4, getBottom() - 4, 4, 4, 0, 172D/256D, 176D/256D, 162D/256D, 166D/256D); //bottom right

            RenderHelper.endDrawBatch();
        }
        else
        {
            fill(graphics, parent.getFocused() == this ? getTheme().windowBorderActive : getTheme().windowBorderInactive, 0);

            //TODO draw the inside background as well - when ridding border size & handling proper padding
        }
    }

    public void renderDockHighlight(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if(getWorkspace().canDockWindows() && getWorkspace().getFocused() == this  && getWorkspace().isDragging() && (canBeDocked() || canDockStack()) && edgeGrab != null && edgeGrab.titleGrab)
        {
            WindowDock<?> dock = getWorkspace().getDock();

            //Render BORDER HIGHLIGHT
            double left = 0;
            double top = 0;
            double right = getWorkspace().getWidth();
            double bottom = getWorkspace().getHeight();
            for(Map.Entry<WindowDock.ArrayListHolder, Constraint.Property.Type> e : dock.docked.entrySet())
            {
                for(Window<?,?> key : e.getKey().windows())
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

            Window<W, V> window = this;
            int oriX = window.posX;
            int oriY = window.posY;
            window.pos(-10000, -10000);
            WindowDock.DockInfo info = dock.getDockInfo(mouseX, mouseY, window.canDockStack());
            window.pos(oriX, oriY);

            boolean draw = info != null && info.window() != null;
            if(draw)
            {
                left = info.window().getLeft();
                right = info.window().getRight();
                top = info.window().getTop();
                bottom = info.window().getBottom();
            }
            else if(canBeDocked() && !getWorkspace().isDocked(this))
            {
                HashSet<Constraint.Property.Type> disabledDocks = getWorkspace().getDock().disabledDocks;

                int dockSnap = iChunUtil.configClient.bnsDockBorder;
                if(mouseY >= top && mouseY < bottom)
                {
                    if(mouseX >= left && mouseX < left + dockSnap && !disabledDocks.contains(Constraint.Property.Type.LEFT))
                    {
                        right = left + dockSnap;
                        draw = true;
                    }
                    else if(mouseX >= right - dockSnap && mouseX < right && !disabledDocks.contains(Constraint.Property.Type.RIGHT))
                    {
                        left = right - dockSnap;
                        draw = true;
                    }
                }
                if(mouseX >= left && mouseX < right)
                {
                    if(mouseY >= top && mouseY < top + dockSnap && !disabledDocks.contains(Constraint.Property.Type.TOP))
                    {
                        bottom = top + dockSnap;
                        draw = true;
                    }
                    else if(mouseY >= bottom - dockSnap && bottom < right && !disabledDocks.contains(Constraint.Property.Type.BOTTOM))
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
                if(renderMinecraftStyle() > 0) //glint render taken from 1.12.2 RenderItem.renderEffect
                {
                    //CCI windows can never be docked, figure out how to port this in iChunUtil.
                    //                    float scale = 8;
                    //                    float scaleTex = 512F;
                    //                    RenderSystem.depthMask(false);
                    //                    RenderSystem.depthFunc(514);
                    //                    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
                    //                    bindTexture(ItemRenderer.ENCHANT_GLINT_LOCATION);
                    //                    RenderSystem.matrixMode(5890);
                    //                    RenderSystem.pushMatrix();
                    //                    RenderSystem.scalef((float)scale, (float)scale, (float)scale);
                    //                    float f = (float)(Util.getMillis() % 3000L) / 3000.0F / (float)scale;
                    //                    RenderSystem.translatef(f, 0.0F, 0.0F);
                    //                    RenderSystem.rotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                    //                    RenderHelper.draw(stack, left, top, right - left, bottom - top, 0, left / scaleTex, right / scaleTex, top / scaleTex, bottom / scaleTex);
                    //                    RenderSystem.popMatrix();
                    //                    RenderSystem.pushMatrix();
                    //                    RenderSystem.scalef((float)scale, (float)scale, (float)scale);
                    //                    float f1 = (float)(Util.getMillis() % 4873L) / 4873.0F / (float)scale;
                    //                    RenderSystem.translatef(-f1, 0.0F, 0.0F);
                    //                    RenderSystem.rotatef(10.0F, 0.0F, 0.0F, 1.0F);
                    //                    RenderHelper.draw(stack, left, top, right - left, bottom - top, 0, left / scaleTex, right / scaleTex, top / scaleTex, bottom / scaleTex);
                    //                    RenderSystem.popMatrix();
                    //                    RenderSystem.matrixMode(5888);
                    //                    RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                    //                    RenderSystem.depthFunc(515);
                    //                    RenderSystem.depthMask(true);
                }
                else
                {
                    RenderHelper.drawColour(graphics, getTheme().elementTabBorderActive, 150, left, top, right - left, bottom - top, 0);
                }
                RenderSystem.disableBlend();
            }
            //END RENDER BORDER HIGHLIGHT
        }
    }

    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent event)
    {
        if(parent.getFocused() == this)
        {
            return super.nextFocusPath(event);
        }
        return null; //we're not focused anyway, so, nah
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

    @Override
    public W getWorkspace()
    {
        return super.getWorkspace();
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
