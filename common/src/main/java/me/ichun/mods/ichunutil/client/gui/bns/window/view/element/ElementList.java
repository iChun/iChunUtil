package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.client.gui.bns.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.constraint.Constraint;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.util.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.Screen;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

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
public class ElementList<P extends Fragment<?>, I> extends ElementFertile<P>
{
    public List<Item<I>> items = new ArrayList<>();
    private @Nullable ElementScrollBar scrollVert;
    private @Nullable ElementScrollBar scrollHori;
    private @Nullable BiConsumer<Item<I>, Item<I>> dragHandler;
    private @Nullable BiConsumer<Item<I>, Integer> rearrangeHandler;

    public boolean renderBackground = true;

    public boolean hasInit;
    private MousePosItem<I> pos;

    public ElementList(@NotNull P parent)
    {
        super(parent);
    }

    public <T extends ElementList<P, I>> T setScrollVertical(ElementScrollBar scroll)
    {
        scrollVert = scroll;
        scrollVert.setCallback((scr) -> alignItems());
        return (T)this;
    }

    public <T extends ElementList<P, I>> T setScrollHorizontal(ElementScrollBar scroll)
    {
        scrollHori = scroll;
        scrollHori.setCallback((scr) -> alignItems());
        return (T)this;
    }

    public <T extends ElementList<P, I>> T setDragHandler(BiConsumer<Item<I>, Item<I>> dragHandler)
    {
        this.dragHandler = dragHandler;
        return (T)this;
    }

    public <T extends ElementList<P, I>> T setRearrangeHandler(BiConsumer<Item<I>, Integer> rearrangeHandler)
    {
        this.rearrangeHandler = rearrangeHandler;
        return (T)this;
    }

    public <T extends ElementList<P, I>> T disableBackground() //and border
    {
        this.renderBackground = false;
        return (T)this;
    }

    private Item<I> addItem(Item<I> item, int index)
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

    private Item<I> addItem(Item<I> item)
    {
        return addItem(item, -1);
    }

    public Item<I> addItem(I o, int index)
    {
        return addItem(new Item<>(this, o), index);
    }

    public Item<I> addItem(I o)
    {
        return addItem(o, -1);
    }

    public boolean removeItemWithObject(@Nullable Object o)
    {
        for(int i = items.size() - 1; i >= 0; i--)
        {
            Item<I> item = items.get(i);
            if(item.getObject() != null && item.getObject().equals(o) || item.getObject() == null && o == null)
            {
                items.remove(item);
                updateScrollBarSizes();
                return true;
            }
        }
        return false;
    }

    public @Nullable Item<I> getItemWithObject(@Nullable Object o)
    {
        for(Item<I> item : items)
        {
            if(item.getObject() != null && item.getObject().equals(o) || item.getObject() == null && o == null)
            {
                return item;
            }
        }
        return null;
    }

    public List<Item<I>> getSelectedItems()
    {
        List<Item<I>> listItems = new ArrayList<>();
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
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        PoseStack stack = graphics.pose();
        if(renderBackground)
        {
            if(renderMinecraftStyle() > 0)
            {
                cropAndStitch(resourceHorse(), stack, getLeft(), getTop(), width, height, 2, 79, 17, 90, 54, 256, 256);
            }
            else
            {
                RenderHelper.drawColour(graphics, getTheme().elementListBorder, 255, getLeft(), getTop(), width, 1, 0); //top
                RenderHelper.drawColour(graphics, getTheme().elementListBorder, 255, getLeft(), getTop(), 1, height, 0); //left
                RenderHelper.drawColour(graphics, getTheme().elementListBorder, 255, getLeft(), getBottom() - 1, width, 1, 0); //bottom
                RenderHelper.drawColour(graphics, getTheme().elementListBorder, 255, getRight() - 1, getTop(), 1, height, 0); //right
            }
        }

        setScissor();
        items.forEach(item -> item.render(graphics, mouseX, mouseY, partialTick));

        if(getFocused() instanceof Item)
        {
            ((Item<I>)getFocused()).render(graphics, mouseX, mouseY, partialTick);
        }

        resetScissorToParent();
    }

    public Item<I> getItemAt(double mouseX, double mouseY)
    {
        Optional<GuiEventListener> child = getChildAt(mouseX, mouseY);
        if(child.isPresent() && child.get() instanceof Item<?>)
        {
            return (Item<I>)child.get();
        }
        return null;
    }

    public int getMouseRelation(double mouseX, double mouseY, Item<I> item)
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
    public void unfocus(@Nullable GuiEventListener guiReplacing)
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
                else if(getFocused() instanceof Fragment)
                {
                    setFocused(null);
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
            Item<I> item = getItemAt(mouseX, mouseY);
            Item<I> draggedItem = pos.item;
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
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY)
    {
        if(isMouseOver(mouseX, mouseY))
        {
            boolean defaultScroll = super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
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
                        scrollHori.secondHandScroll((scrollY * 70 / getTotalItemHeight()) * 2D);
                        return true;
                    }
                }
                else
                {
                    if(scrollVert != null)
                    {
                        scrollVert.secondHandScroll((scrollY * 70 / getTotalItemHeight()) * 2D);
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
        for(Item<I> item : items)
        {
            item.posX = currentWidth - offsetX;
            item.posY = currentHeight - offsetY;

            if(item.width != (width - 2))
            {
                item.width = Math.max(itemWidth, (width - 2));
            }
            if(item.height != item.getMinHeight())
            {
                item.height = item.getMinHeight();
            }
            item.resize(getMinecraft(), item.parent.width, item.parent.height); // make sure we're not too big or small

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
        for(Item<I> item : items)
        {
            itemHeight += item.height;
        }
        return itemHeight;
    }

    public int getMinItemWidth()
    {
        int itemWidth = 0;
        for(Item<I> item : items)
        {
            if(item.getMinWidth() > itemWidth)
            {
                itemWidth = item.getMinWidth();
            }
        }
        return itemWidth;
    }

    @Override
    public List<Item<I>> children()
    {
        return items;
    }

    @Override
    public void setScissor()
    {
        super.drawBatch();
        RenderHelper.startGlScissor(getLeft() + 1, getTop() + 1, width - 2, height - 2);
    }

    @Override
    public boolean requireScissor()
    {
        return true;
    }

    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent event) //we can't change focus on this
    {
        return null;
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

    public static class Item<I> extends ElementFertile<ElementList<?, I>>
    {
        protected final I heldObject; //This may be set null but must be taken into consideration by whoever is manipulating the list.
        public List<Element<?>> elements = new ArrayList<>();
        private boolean deselectOnUnfocus = true;
        private TriConsumer<Double, Double, Item<I>> rightClickConsumer;
        public boolean selected;
        private @Nullable Consumer<Item<I>> selectionHandler;
        private @Nullable Consumer<Item<I>> doubleClickHandler;
        private @Nullable Function<Item<I>, Boolean> enterResponder;
        private int borderSize = 1;
        private int clickTimeout;

        public Item(@NotNull ElementList<?, I> parent, I heldObject)
        {
            super(parent);
            this.heldObject = heldObject;
        }

        public Item<I> staySelectedOnDefocus()
        {
            deselectOnUnfocus = false;
            return this;
        }

        public Item<I> setRightClickConsumer(TriConsumer<Double, Double, Item<I>> rightClickConsumer)
        {
            this.rightClickConsumer = rightClickConsumer;
            return this;
        }

        public Item<I> setEnterResponder(Function<Item<I>, Boolean> enterResponder)
        {
            this.enterResponder = enterResponder;
            return this;
        }

        public Item<I> setDefaultAppearance()
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
                wrapper = new ElementTextWrapper(this).setText(StringUtil.readableFileSize(file.length()));
                wrapper.setNoWrap().setConstraint(new Constraint(wrapper).right(this, Constraint.Property.Type.RIGHT, this.getBorderSize() + 4).top(this, Constraint.Property.Type.TOP, this.getBorderSize()));
                this.addElement(wrapper);
            }
            else
            {
                ElementTextWrapper wrapper = new ElementTextWrapper(this).setText(StringUtil.getInterpretedInfo(heldObject));
                wrapper.setConstraint(Constraint.matchParent(wrapper, this, this.getBorderSize()).bottom(null, Constraint.Property.Type.BOTTOM, 0));
                elements.add(wrapper);
            }

            return this;
        }

        public Item<I> setSelectionHandler(Consumer<Item<I>> handler)
        {
            this.selectionHandler = handler;
            return this;
        }

        public Item<I> setDoubleClickHandler(Consumer<Item<I>> handler)
        {
            this.doubleClickHandler = handler;
            return this;
        }

        public Item<I> setBorderSize(int size)
        {
            this.borderSize = size;
            return this;
        }

        public Item<I> addTextWrapper(String s)
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

        public I getObject()
        {
            return heldObject;
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
        {
            PoseStack stack = graphics.pose();
            if(shouldRender())
            {
                boolean draggingUs = parent.isDragging() && parent.getFocused() == this && parent.pos != null;
                ElementList<?, I> list = parent;
                MousePosItem<I> pos = list.pos;

                if(isMouseOver(mouseX, mouseY) && parent.dragHandler != null || draggingUs)
                {
                    getWorkspace().cursorState = Workspace.CURSOR_CROSSHAIR;
                }

                if(draggingUs)
                {
                    stack.pushPose();
                    double x = (mouseX - pos.x);
                    double y = (mouseY - pos.y);
                    stack.translate(x, y, 0D);
                }

                if(renderMinecraftStyle() > 0)
                {
                    boolean canRearrange = false;

                    if(draggingUs && list.rearrangeHandler != null)
                    {
                        Item<I> item = list.getItemAt(mouseX, mouseY);
                        Item<I> draggedItem = pos.item;
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
                        cropAndStitch(resourceHorse(), stack, getLeft(), getTop(), width, height, 2, 79, 17, 90, 54, 256, 256);
                    }
                    else
                    {
                        cropAndStitch(resourceHorse(), stack, getLeft(), getTop(), width, height, 2, 43, 141, 18, 18, 256, 256);
                    }
                }
                else
                {
                    int[] borderColour = getTheme().elementListItemBorder;

                    if(draggingUs && list.rearrangeHandler != null)
                    {
                        Item<I> item = list.getItemAt(mouseX, mouseY);
                        Item<I> draggedItem = pos.item;
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
                                        borderColour = getTheme().elementListItemBackgroundHover;
                                    }
                                }
                            }
                            else
                            {
                                borderColour = getTheme().elementListItemBackgroundHover;
                            }
                        }
                    }

                    //RENDER
                    fill(graphics, borderColour, 0);
                    fill(graphics, parent.isDragging() && parent.getFocused() == this ? getTheme().elementButtonClick : (isMouseOver(mouseX, mouseY) && !(parent.isDragging() && parent.getFocused() != this)) ? getTheme().elementListItemBackgroundHover : selected ? getTheme().elementListItemBackgroundSelect : getTheme().elementListItemBackground, getBorderSize());
                }

                elements.forEach(element -> element.render(graphics, mouseX, mouseY, partialTick));

                if(draggingUs)
                {
                    stack.popPose();
                }
            }
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button)
        {
            if(parent.getFocused() == this && isMouseOver(mouseX, mouseY))
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
                            clickTimeout = iChunUtil.configClient.bnsDoubleClickSpeed;
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
                if(oldSelected != selected)
                {
                    triggerSelectionHandler();
                }
            }
            return super.mouseReleased(mouseX, mouseY, button);
        }

        public void triggerSelectionHandler()
        {
            if(selectionHandler != null)
            {
                selectionHandler.accept(this);
            }
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
            return getRight() > parent.getLeft() && getLeft() < parent.getRight() && getBottom() > parent.getTop() && getTop() < parent.getBottom() ||
                parent.isDragging() && parent.getFocused() == this && parent.pos != null;
        }

        @Override
        public List<? extends Fragment<?>> children()
        {
            return elements;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers)
        {
            if(parent.getFocused() == this)
            {
                boolean flag = super.keyPressed(keyCode, scanCode, modifiers);
                if(!flag)
                {
                    if(keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_LEFT)
                    {
                        for(int i = 0; i < parent.items.size(); i++)
                        {
                            Item<I> item = parent.items.get(i);
                            if(item == this)
                            {
                                if(i > 0)
                                {
                                    Item item1 = parent.items.get(i - 1);
                                    parent.setFocused(item1);
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
                        for(int i = 0; i < parent.items.size(); i++)
                        {
                            Item<I> item = parent.items.get(i);
                            if(item == this)
                            {
                                if(i < parent.items.size() - 1)
                                {
                                    Item<I> item1 = parent.items.get(i + 1);
                                    parent.setFocused(item1);
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
                return flag;
            }
            else
            {
                if((keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) && enterResponder != null)
                {
                    return enterResponder.apply(this);
                }
                return super.keyPressed(keyCode, scanCode, modifiers);
            }
        }

        @Override
        public void unfocus(@Nullable GuiEventListener guiReplacing)
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

    public static class MousePosItem<I> extends MousePos
    {
        Item<I> item;

        public MousePosItem(int x, int y, Item<I> item)
        {
            super(x, y);
            this.item = item;
        }
    }
}
