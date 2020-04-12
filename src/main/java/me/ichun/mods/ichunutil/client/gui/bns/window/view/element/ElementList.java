package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.systems.RenderSystem;
import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ElementList extends ElementFertile<Fragment>
{
    public List<Item> items = new ArrayList<>();
    private @Nullable ElementScrollBar scrollVert;
    private @Nullable ElementScrollBar scrollHori;
    private @Nullable BiConsumer<Item, Item> dragHandler;
    private @Nullable BiConsumer<Item, Integer> rearrangeHandler;

    public boolean hasInit;
    private MousePosItem pos;

    public ElementList(@Nonnull View parent)
    {
        super(parent);
    }

    public ElementList setScrollVertical(ElementScrollBar scroll)
    {
        scrollVert = scroll;
        scrollVert.setCallback((scr) -> alignItems());
        return this;
    }

    public ElementList setScrollHorizontal(ElementScrollBar scroll)
    {
        scrollHori = scroll;
        scrollHori.setCallback((scr) -> alignItems());
        return this;
    }

    public ElementList setDragHandler(BiConsumer<Item, Item> dragHandler)
    {
        this.dragHandler = dragHandler;
        return this;
    }

    public ElementList setRearrangeHandler(BiConsumer<Item, Integer> rearrangeHandler)
    {
        this.rearrangeHandler = rearrangeHandler;
        return this;
    }

    public Item addItem(Object o)
    {
        Item item = new Item(this, o);
        items.add(item);
        item.constraint = Constraint.sizeOnly(item);
        if(hasInit)
        {
            alignItems();
            updateScrollBarSizes();
        }
        return item;
    }

    public boolean removeItemWithObject(Object o)
    {
        for(int i = items.size() - 1; i >= 0; i--)
        {
            Item item = items.get(i);
            if(item.getObject().equals(o))
            {
                items.remove(item);
                updateScrollBarSizes();
                return true;
            }
        }
        return false;
    }

    public @Nullable Item getItemWithObject(Object o)
    {
        for(Item item : items)
        {
            if(item.getObject().equals(o))
            {
                return item;
            }
        }
        return null;
    }

    public List<Item> getSelectedItems()
    {
        List<Item> listItems = new ArrayList<>();
        items.forEach(item -> {
            if(item.selected)
            {
                listItems.add(item);
            }
        });
        return listItems;
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
    public void render(int mouseX, int mouseY, float partialTick)
    {
        if(renderMinecraftStyle())
        {
            bindTexture(Fragment.VANILLA_HORSE);
            cropAndStitch(getLeft(), getTop(), width, height, 2, 79, 17, 90, 54, 256, 256);
        }
        else
        {
            RenderHelper.drawColour(getTheme().elementTreeBorder[0], getTheme().elementTreeBorder[1], getTheme().elementTreeBorder[2], 255, getLeft(), getTop(), width, 1, 0); //top
            RenderHelper.drawColour(getTheme().elementTreeBorder[0], getTheme().elementTreeBorder[1], getTheme().elementTreeBorder[2], 255, getLeft(), getTop(), 1, height, 0); //left
            RenderHelper.drawColour(getTheme().elementTreeBorder[0], getTheme().elementTreeBorder[1], getTheme().elementTreeBorder[2], 255, getLeft(), getBottom() - 1, width, 1, 0); //bottom
            RenderHelper.drawColour(getTheme().elementTreeBorder[0], getTheme().elementTreeBorder[1], getTheme().elementTreeBorder[2], 255, getRight() - 1, getTop(), 1, height, 0); //right
        }

        setScissor();
        items.forEach(item -> item.render(mouseX, mouseY, partialTick));

        if(getFocused() instanceof Item)
        {
            ((Item)getFocused()).render(mouseX, mouseY, partialTick);
        }

        resetScissorToParent();
    }

    public Item getItemAt(double mouseX, double mouseY)
    {
        Optional<IGuiEventListener> child = getEventListenerForPos(mouseX, mouseY);
        if(child.isPresent() && child.get() instanceof Item)
        {
            return (Item)child.get();
        }
        return null;
    }

    public int getMouseRelation(double mouseX, double mouseY, Item item)
    {
        if(rearrangeHandler != null)
        {
            if(mouseY < item.getTop() + 3)
            {
                return -1;
            }
            else if(mouseY > item.getBottom() - 3)
            {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public void unfocus(@Nullable IGuiEventListener guiReplacing)
    {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(isMouseOver(mouseX, mouseY) && dragHandler != null && button == 0) // check for if we can drag or nah
        {
            boolean hasElement = defaultMouseClicked(mouseX, mouseY, button); //this calls setDragging();
            if(hasElement) //we clicked an element. let's drag it
            {
                pos = new MousePosItem((int)mouseX, (int)mouseY, getItemAt(mouseX, mouseY));
            }
            else if(getFocused() instanceof Fragment)
            {
                setFocused(null);
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double distX, double distY)
    {
        return pos != null;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if(pos != null)
        {
            Item item = getItemAt(mouseX, mouseY);
            Item draggedItem = pos.item;
            if(draggedItem != null && item != draggedItem)
            {
                if(item != null)
                {
                    int relation = getMouseRelation(mouseX, mouseY, item);
                    if(relation != 0)
                    {
                        int itemIndex = items.indexOf(item);
                        int draggedIndex = items.indexOf(pos.item);
                        if(!(itemIndex == draggedIndex - 1 && relation == 1 || itemIndex == draggedIndex + 1 && relation == -1))
                        {
                            int newIndex = relation > 0 ? itemIndex + 1 : itemIndex;
                            if(draggedIndex < newIndex)
                            {
                                newIndex--;
                            }
                            items.remove(draggedItem);
                            items.add(newIndex, draggedItem);
                            rearrangeHandler.accept(draggedItem, draggedIndex);
                        }
                    }
                    else
                    {
                        dragHandler.accept(draggedItem, item); //pos will only be set if dragHandler isn't null
                    }
                }
                else if(rearrangeHandler != null)
                {
                    int draggedIndex = items.indexOf(pos.item);
                    items.remove(draggedItem);
                    if(mouseY < getTop())
                    {
                        items.add(0, draggedItem);
                    }
                    else
                    {
                        items.add(draggedItem);
                    }
                    rearrangeHandler.accept(draggedItem, draggedIndex);
                }
                alignItems();
            }
            pos = null;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double dist)
    {
        if(isMouseOver(mouseX, mouseY))
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
        for(Item item : items)
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
        for(Item item : items)
        {
            itemHeight += item.height;
        }
        return itemHeight;
    }

    public int getMinItemWidth()
    {
        int itemWidth = 0;
        for(Item item : items)
        {
            if(item.getMinWidth() > itemWidth)
            {
                itemWidth = item.getMinWidth();
            }
        }
        return itemWidth;
    }

    @Override
    public List<? extends IGuiEventListener> children()
    {
        return items;
    }

    @Override
    public void setScissor()
    {
        RenderHelper.startGlScissor(getLeft() + 1, getTop() + 1, width - 2, height - 2);
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
        return 1;
    }

    public static class Item<M> extends ElementFertile<Fragment>
    {
        protected final @Nonnull M heldObject; //height 13?
        private List<Element> elements = new ArrayList<>();
        private boolean deselectOnUnfocus = true;
        public boolean selected;
        private @Nullable Consumer<Item<M>> selectionHandler;
        private int borderSize = 1;

        public Item(@Nonnull Fragment parent, @Nonnull M heldObject)
        {
            super(parent);
            this.heldObject = heldObject;
        }

        public Item staySelectedOnDefocus()
        {
            deselectOnUnfocus = false;
            return this;
        }

        public Item setDefaultAppearance()
        {
            ElementTextWrapper wrapper = new ElementTextWrapper(this).setText(Workspace.getInterpretedInfo(heldObject));
            wrapper.setConstraint(Constraint.matchParent(wrapper, this, this.getBorderSize()).bottom(null, Constraint.Property.Type.BOTTOM, 0));
            elements.add(wrapper);

            return this;
        }

        public Item setSelectionHandler(Consumer<Item<M>> handler)
        {
            this.selectionHandler = handler;
            return this;
        }

        public Item setBorderSize(int size)
        {
            this.borderSize = size;
            return this;
        }

        public Item addTextWrapper(String s)
        {
            ElementTextWrapper wrapper = new ElementTextWrapper(this).setText(s);
            wrapper.setConstraint(Constraint.matchParent(wrapper, this, this.getBorderSize()).top(this, Constraint.Property.Type.TOP, this.getBorderSize()).bottom(null, Constraint.Property.Type.BOTTOM, 0));
            this.addElement(wrapper);
            return this;
        }

        public Element addElement(Element e)
        {
            elements.add(e);
            return e;
        }

        public M getObject()
        {
            return heldObject;
        }

        @Override
        public void render(int mouseX, int mouseY, float partialTick)
        {
            if(shouldRender())
            {
                boolean draggingUs = parentFragment.isDragging() && parentFragment.getFocused() == this && ((ElementList)parentFragment).pos != null;
                ElementList list = (ElementList)parentFragment;
                MousePosItem pos = list.pos;

                if(draggingUs)
                {
                    RenderSystem.pushMatrix();
                    double x = (mouseX - pos.x);
                    double y = (mouseY - pos.y);
                    RenderSystem.translated(x, y, 0D);
                }

                if(renderMinecraftStyle())
                {
                    bindTexture(Fragment.VANILLA_HORSE);
                    boolean canRearrange = false;

                    if(draggingUs && list.rearrangeHandler != null)
                    {
                        Item item = list.getItemAt(mouseX, mouseY);
                        Item draggedItem = pos.item;
                        if(draggedItem != null && item != draggedItem)
                        {
                            if(item != null)
                            {
                                int relation = list.getMouseRelation(mouseX, mouseY, item);
                                if(relation != 0)
                                {
                                    int itemIndex = list.items.indexOf(item);
                                    int draggedIndex = list.items.indexOf(pos.item);
                                    if(!(itemIndex == draggedIndex - 1 && relation == 1 || itemIndex == draggedIndex + 1 && relation == -1))
                                    {
                                        canRearrange = true;
                                    }
                                }
                            }
                            else
                            {
                                canRearrange = true;
                            }
                        }
                    }

                    if(canRearrange)
                    {
                        cropAndStitch(getLeft(), getTop(), width, height, 2, 79, 17, 90, 54, 256, 256);
                    }
                    else
                    {
                        cropAndStitch(getLeft(), getTop(), width, height, 2, 43, 141, 18, 18, 256, 256);
                    }
                }
                else
                {
                    int[] borderColour = getTheme().elementTreeItemBorder;

                    if(draggingUs && list.rearrangeHandler != null)
                    {
                        Item item = list.getItemAt(mouseX, mouseY);
                        Item draggedItem = pos.item;
                        if(draggedItem != null && item != draggedItem)
                        {
                            if(item != null)
                            {
                                int relation = list.getMouseRelation(mouseX, mouseY, item);
                                if(relation != 0)
                                {
                                    int itemIndex = list.items.indexOf(item);
                                    int draggedIndex = list.items.indexOf(pos.item);
                                    if(!(itemIndex == draggedIndex - 1 && relation == 1 || itemIndex == draggedIndex + 1 && relation == -1))
                                    {
                                        borderColour = getTheme().elementTreeItemBgHover;
                                    }
                                }
                            }
                            else
                            {
                                borderColour = getTheme().elementTreeItemBgHover;
                            }
                        }
                    }

                    //RENDER
                    fill(borderColour, 0);
                    fill(parentFragment.isDragging() && parentFragment.getFocused() == this ? getTheme().elementButtonClick : isMouseOver(mouseX, mouseY) ? getTheme().elementTreeItemBgHover : selected ? getTheme().elementTreeItemBgSelect : getTheme().elementTreeItemBg, getBorderSize());
                }

                elements.forEach(element -> element.render(mouseX, mouseY, partialTick));

                if(draggingUs)
                {
                    RenderSystem.popMatrix();
                }
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            if(super.mouseClicked(mouseX, mouseY, button)) // clicking this
            {
                if(button == 0)
                {
                    selected = true;
                }
                else if(button == 1) //RMB
                {
                    selected = false;
                }
                if(selectionHandler != null)
                {
                    selectionHandler.accept(this);
                }
                return true;
            }
            return false;
        }

        public boolean shouldRender()
        {
            return getRight() > parentFragment.getLeft() && getLeft() < parentFragment.getRight() && getBottom() > parentFragment.getTop() && getTop() < parentFragment.getBottom() ||
                    parentFragment.isDragging() && parentFragment.getFocused() == this && ((ElementList)parentFragment).pos != null;
        }

        @Override
        public List<? extends IGuiEventListener> children()
        {
            return elements;
        }

        @Override
        public void unfocus(@Nullable IGuiEventListener guiReplacing)
        {
            super.unfocus(guiReplacing);
            if(deselectOnUnfocus)
            {
                selected = false;
                if(selectionHandler != null)
                {
                    selectionHandler.accept(this);
                }
            }
        }

        @Override
        public int getBorderSize()
        {
            return borderSize;
        }
    }

    public class MousePosItem
    {
        int x;
        int y;
        Item item;

        public MousePosItem(int x, int y, Item item)
        {
            this.x = x;
            this.y = y;
            this.item = item;
        }
    }
}
