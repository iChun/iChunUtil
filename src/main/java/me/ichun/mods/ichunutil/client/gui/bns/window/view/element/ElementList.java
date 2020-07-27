package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.util.IOUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import org.apache.logging.log4j.util.TriConsumer;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class ElementList<P extends Fragment> extends ElementFertile<P>
{
    public List<Item<?>> items = new ArrayList<>();
    private @Nullable ElementScrollBar<?> scrollVert;
    private @Nullable ElementScrollBar<?> scrollHori;
    private @Nullable BiConsumer<Item<?>, Item<?>> dragHandler;
    private @Nullable BiConsumer<Item<?>, Integer> rearrangeHandler;

    public boolean renderBackground = true;

    public boolean hasInit;
    private MousePosItem pos;

    public ElementList(@Nonnull P parent)
    {
        super(parent);
    }

    public <T extends ElementList<P>> T setScrollVertical(ElementScrollBar<?> scroll)
    {
        scrollVert = scroll;
        scrollVert.setCallback((scr) -> alignItems());
        return (T)this;
    }

    public <T extends ElementList<P>> T setScrollHorizontal(ElementScrollBar<?> scroll)
    {
        scrollHori = scroll;
        scrollHori.setCallback((scr) -> alignItems());
        return (T)this;
    }

    public <T extends ElementList<P>> T setDragHandler(BiConsumer<Item<?>, Item<?>> dragHandler)
    {
        this.dragHandler = dragHandler;
        return (T)this;
    }

    public <T extends ElementList<P>> T setRearrangeHandler(BiConsumer<Item<?>, Integer> rearrangeHandler)
    {
        this.rearrangeHandler = rearrangeHandler;
        return (T)this;
    }

    public <T extends ElementList<P>> T disableBackground() //and border
    {
        this.renderBackground = false;
        return (T)this;
    }

    public <T extends Item<?>> T addItem(T item, int index)
    {
        if(index >= 0)
        {
            items.add(index, item);
        }
        else
        {
            items.add(item);
        }
        item.constraint = Constraint.sizeOnly(item);
        if(hasInit)
        {
            alignItems();
            updateScrollBarSizes();
        }
        return item;
    }

    public <T extends Item<?>> T addItem(T item)
    {
        return addItem(item, -1);
    }

    public <M extends Object> Item<M> addItem(M o, int index)
    {
        return addItem(new Item<>(this, o), index);
    }

    public <M extends Object> Item<M> addItem(M o)
    {
        return addItem(o, -1);
    }

    public boolean removeItemWithObject(Object o)
    {
        for(int i = items.size() - 1; i >= 0; i--)
        {
            Item<?> item = items.get(i);
            if(item.getObject().equals(o))
            {
                items.remove(item);
                updateScrollBarSizes();
                return true;
            }
        }
        return false;
    }

    public @Nullable Item<?> getItemWithObject(Object o)
    {
        for(Item<?> item : items)
        {
            if(item.getObject().equals(o))
            {
                return item;
            }
        }
        return null;
    }

    public List<Item<?>> getSelectedItems()
    {
        List<Item<?>> listItems = new ArrayList<>();
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
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTick)
    {
        if(renderBackground)
        {
            if(renderMinecraftStyle())
            {
                bindTexture(Fragment.VANILLA_HORSE);
                cropAndStitch(stack, getLeft(), getTop(), width, height, 2, 79, 17, 90, 54, 256, 256);
            }
            else
            {
                RenderHelper.drawColour(stack, getTheme().elementTreeBorder[0], getTheme().elementTreeBorder[1], getTheme().elementTreeBorder[2], 255, getLeft(), getTop(), width, 1, 0); //top
                RenderHelper.drawColour(stack, getTheme().elementTreeBorder[0], getTheme().elementTreeBorder[1], getTheme().elementTreeBorder[2], 255, getLeft(), getTop(), 1, height, 0); //left
                RenderHelper.drawColour(stack, getTheme().elementTreeBorder[0], getTheme().elementTreeBorder[1], getTheme().elementTreeBorder[2], 255, getLeft(), getBottom() - 1, width, 1, 0); //bottom
                RenderHelper.drawColour(stack, getTheme().elementTreeBorder[0], getTheme().elementTreeBorder[1], getTheme().elementTreeBorder[2], 255, getRight() - 1, getTop(), 1, height, 0); //right
            }
        }

        setScissor();
        items.forEach(item -> item.render(stack, mouseX, mouseY, partialTick));

        if(getListener() instanceof Item)
        {
            ((Item<?>)getListener()).render(stack, mouseX, mouseY, partialTick);
        }

        resetScissorToParent();
    }

    public Item<?> getItemAt(double mouseX, double mouseY)
    {
        Optional<IGuiEventListener> child = getEventListenerForPos(mouseX, mouseY);
        if(child.isPresent() && child.get() instanceof Item<?>)
        {
            return (Item<?>)child.get();
        }
        return null;
    }

    public int getMouseRelation(double mouseX, double mouseY, Item<?> item)
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
        if(isMouseOver(mouseX, mouseY)) // check for if we can drag or nah
        {
            boolean hasElement = defaultMouseClicked(mouseX, mouseY, button); //this calls setDragging();
            if(dragHandler != null && button == 0)
            {
                if(hasElement) //we clicked an element. let's drag it
                {
                    pos = new MousePosItem((int)mouseX, (int)mouseY, getItemAt(mouseX, mouseY));
                }
                else if(getListener() instanceof Fragment)
                {
                    setListener(null);
                }
            }
            return true;
        }
        return false;
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
            Item<?> item = getItemAt(mouseX, mouseY);
            Item<?> draggedItem = pos.item;
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
        for(Item<?> item : items)
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
            scrollHori.setScrollBarSize(width / (float)itemWidth); //if items width is higher than ours, scroll bar should appear
        }
    }

    public int getTotalItemHeight()
    {
        int itemHeight = 0;
        for(Item<?> item : items)
        {
            itemHeight += item.height;
        }
        return itemHeight;
    }

    public int getMinItemWidth()
    {
        int itemWidth = 0;
        for(Item<?> item : items)
        {
            if(item.getMinWidth() > itemWidth)
            {
                itemWidth = item.getMinWidth();
            }
        }
        return itemWidth;
    }

    @Override
    public List<? extends Item<?>> getEventListeners()
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

    public static class Item<M> extends ElementFertile<ElementList<?>>
    {
        protected final @Nonnull M heldObject; //height 13?
        public List<Element<?>> elements = new ArrayList<>();
        private boolean deselectOnUnfocus = true;
        private TriConsumer<Double, Double, Item<M>> rightClickConsumer;
        public boolean selected;
        private @Nullable Consumer<Item<M>> selectionHandler;
        private @Nullable Consumer<Item<M>> doubleClickHandler;
        private @Nullable Function<Item<M>, Boolean> enterResponder;
        private int borderSize = 1;
        private int clickTimeout;

        public Item(@Nonnull ElementList<?> parent, @Nonnull M heldObject)
        {
            super(parent);
            this.heldObject = heldObject;
        }

        public Item<M> staySelectedOnDefocus()
        {
            deselectOnUnfocus = false;
            return this;
        }

        public Item<M> setRightClickConsumer(TriConsumer<Double, Double, Item<M>> rightClickConsumer)
        {
            this.rightClickConsumer = rightClickConsumer;
            return this;
        }

        public Item<M> setEnterResponder(Function<Item<M>, Boolean> enterResponder)
        {
            this.enterResponder = enterResponder;
            return this;
        }

        public Item<M> setDefaultAppearance()
        {
            if(heldObject instanceof File)
            {
                File file = (File)heldObject;
                //name
                ElementTextWrapper wrapper = new ElementTextWrapper(this).setText(file.getName());
                wrapper.setNoWrap().setConstraint(new Constraint(wrapper).left(this, Constraint.Property.Type.LEFT, this.getBorderSize() + 2).top(this, Constraint.Property.Type.TOP, this.getBorderSize()));
                this.addElement(wrapper);

                //last modified
                ElementTextWrapper wrapper1 = new ElementTextWrapper(this).setText((new SimpleDateFormat()).format(new Date(file.lastModified())));
                wrapper1.setNoWrap().setConstraint(new Constraint(wrapper1).left(this, Constraint.Property.Type.LEFT, this.getBorderSize() + 2).top(wrapper, Constraint.Property.Type.BOTTOM, 0));
                this.addElement(wrapper1);

                //size
                wrapper = new ElementTextWrapper(this).setText(IOUtil.readableFileSize(file.length()));
                wrapper.setNoWrap().setConstraint(new Constraint(wrapper).right(this, Constraint.Property.Type.RIGHT, this.getBorderSize() + 4).top(this, Constraint.Property.Type.TOP, this.getBorderSize()));
                this.addElement(wrapper);
            }
            else
            {
                ElementTextWrapper wrapper = new ElementTextWrapper(this).setText(Workspace.getInterpretedInfo(heldObject));
                wrapper.setConstraint(Constraint.matchParent(wrapper, this, this.getBorderSize()).bottom(null, Constraint.Property.Type.BOTTOM, 0));
                elements.add(wrapper);
            }

            return this;
        }

        public Item<M> setSelectionHandler(Consumer<Item<M>> handler)
        {
            this.selectionHandler = handler;
            return this;
        }

        public Item<M> setDoubleClickHandler(Consumer<Item<M>> handler)
        {
            this.doubleClickHandler = handler;
            return this;
        }

        public Item<M> setBorderSize(int size)
        {
            this.borderSize = size;
            return this;
        }

        public Item<M> addTextWrapper(String s)
        {
            ElementTextWrapper wrapper = new ElementTextWrapper(this).setText(s);
            wrapper.setConstraint(Constraint.matchParent(wrapper, this, this.getBorderSize()).bottom(null, Constraint.Property.Type.BOTTOM, 0));
            this.addElement(wrapper);
            return this;
        }

        public Element<?> addElement(Element<?> e)
        {
            elements.add(e);
            return e;
        }

        public M getObject()
        {
            return heldObject;
        }

        @Override
        public void render(MatrixStack stack, int mouseX, int mouseY, float partialTick)
        {
            if(shouldRender())
            {
                boolean draggingUs = parentFragment.isDragging() && parentFragment.getListener() == this && parentFragment.pos != null;
                ElementList<?> list = parentFragment;
                MousePosItem pos = list.pos;

                if(draggingUs)
                {
                    stack.push();
                    double x = (mouseX - pos.x);
                    double y = (mouseY - pos.y);
                    stack.translate(x, y, 0D);
                }

                if(renderMinecraftStyle())
                {
                    bindTexture(Fragment.VANILLA_HORSE);
                    boolean canRearrange = false;

                    if(draggingUs && list.rearrangeHandler != null)
                    {
                        Item<?> item = list.getItemAt(mouseX, mouseY);
                        Item<?> draggedItem = pos.item;
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
                        cropAndStitch(stack, getLeft(), getTop(), width, height, 2, 79, 17, 90, 54, 256, 256);
                    }
                    else
                    {
                        cropAndStitch(stack, getLeft(), getTop(), width, height, 2, 43, 141, 18, 18, 256, 256);
                    }
                }
                else
                {
                    int[] borderColour = getTheme().elementTreeItemBorder;

                    if(draggingUs && list.rearrangeHandler != null)
                    {
                        Item<?> item = list.getItemAt(mouseX, mouseY);
                        Item<?> draggedItem = pos.item;
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
                    fill(stack, borderColour, 0);
                    fill(stack, parentFragment.isDragging() && parentFragment.getListener() == this ? getTheme().elementButtonClick : (isMouseOver(mouseX, mouseY) && !(parentFragment.isDragging() && parentFragment.getListener() != this)) ? getTheme().elementTreeItemBgHover : selected ? getTheme().elementTreeItemBgSelect : getTheme().elementTreeItemBg, getBorderSize());
                }

                elements.forEach(element -> element.render(stack, mouseX, mouseY, partialTick));

                if(draggingUs)
                {
                    stack.pop();
                }
            }
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button)
        {
            if(parentFragment.getListener() == this && isMouseOver(mouseX, mouseY))
            {
                boolean oldSelected = selected;
                if(button == 0)
                {
                    selected = true;

                    if(doubleClickHandler != null)
                    {
                        if(clickTimeout > 0)
                        {
                            clickTimeout = 0;
                            doubleClickHandler.accept(this);
                        }
                        else
                        {
                            clickTimeout = iChunUtil.configClient.guiDoubleClickSpeed;
                        }
                    }
                }
                else if(button == 1) //RMB
                {
                    if(rightClickConsumer != null)
                    {
                        rightClickConsumer.accept(mouseX, mouseY, this);
                    }
                    else
                    {
                        selected = false;
                    }
                }
                if(oldSelected != selected && selectionHandler != null)
                {
                    selectionHandler.accept(this);
                }
            }
            return super.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public void tick()
        {
            super.tick();
            if(clickTimeout > 0)
            {
                clickTimeout--;
            }
        }

        public boolean shouldRender()
        {
            return getRight() > parentFragment.getLeft() && getLeft() < parentFragment.getRight() && getBottom() > parentFragment.getTop() && getTop() < parentFragment.getBottom() ||
                    parentFragment.isDragging() && parentFragment.getListener() == this && parentFragment.pos != null;
        }

        @Override
        public List<? extends Fragment<?>> getEventListeners()
        {
            return elements;
        }

        @Override
        public boolean keyPressed(int keyCode, int p_keyPressed_2_, int p_keyPressed_3_)
        {
            if(parentFragment.getListener() == this)
            {
                if(keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_LEFT)
                {
                    for(int i = 0; i < parentFragment.items.size(); i++)
                    {
                        Item<?> item = parentFragment.items.get(i);
                        if(item == this)
                        {
                            if(i > 0)
                            {
                                Item item1 = parentFragment.items.get(i - 1);
                                parentFragment.setListener(item1);
                                boolean oldSelected = item1.selected;
                                item1.selected = true;
                                if(oldSelected != item1.selected && item1.selectionHandler != null)
                                {
                                    item1.selectionHandler.accept(item1);
                                }
                                return true;
                            }
                        }
                    }
                }
                else if(keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_RIGHT)
                {
                    for(int i = 0; i < parentFragment.items.size(); i++)
                    {
                        Item<?> item = parentFragment.items.get(i);
                        if(item == this)
                        {
                            if(i < parentFragment.items.size() - 1)
                            {
                                Item item1 = parentFragment.items.get(i + 1);
                                parentFragment.setListener(item1);
                                boolean oldSelected = item1.selected;
                                item1.selected = true;
                                if(oldSelected != item1.selected && item1.selectionHandler != null)
                                {
                                    item1.selectionHandler.accept(item1);
                                }
                                return true;
                            }
                        }
                    }
                }
            }
            if((keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) && enterResponder != null)
            {
                return enterResponder.apply(this);
            }
            return super.keyPressed(keyCode, p_keyPressed_2_, p_keyPressed_3_);
        }

        @Override
        public void unfocus(@Nullable IGuiEventListener guiReplacing)
        {
            super.unfocus(guiReplacing);
            if(deselectOnUnfocus)
            {
                boolean oldSelected = selected;
                selected = false;
                if(oldSelected && selectionHandler != null)
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

    public static class MousePosItem
    {
        int x;
        int y;
        Item<?> item;

        public MousePosItem(int x, int y, Item<?> item)
        {
            this.x = x;
            this.y = y;
            this.item = item;
        }
    }
}
