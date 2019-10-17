package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import net.minecraft.client.gui.IGuiEventListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ElementList extends ElementFertile<View>
{
    public List<ElementListItem> items = new ArrayList<>();
    public @Nullable ElementScrollBar scrollVert;
    public @Nullable ElementScrollBar scrollHori;

    public ElementList(@Nonnull View parent)
    {
        super(parent);
    }

    public ElementList setScrollVertical(ElementScrollBar scroll)
    {
        scrollVert = scroll;
        return this;
    }

    public ElementList setScrollHorizontal(ElementScrollBar scroll)
    {
        scrollHori = scroll;
        return this;
    }

    public ElementList addItem(ElementListItem item)
    {
        items.add(item);
        return this;
    }

    public boolean removeItemWithObject(Object o)
    {
        for(int i = items.size() - 1; i >= 0; i--)
        {
            ElementListItem item = items.get(i);
            if(item.getObject().equals(o))
            {
                items.remove(item);
                return true;
            }
        }
        return false;
    }

    public @Nullable ElementListItem getItemWithObject(Object o)
    {
        for(ElementListItem item : items)
        {
            if(item.getObject().equals(o))
            {
                return item;
            }
        }
        return null;
    }

    public List<ElementListItem> getSelectedItems()
    {
        List<ElementListItem> listItems = new ArrayList<>();
        items.forEach(item -> {
            if(item.selected)
            {
                listItems.add(item);
            }
        });
        return listItems;
    }


    @Override
    public void render(int mouseX, int mouseY, float partialTick)
    {
        //TODO scissor?
    }

    public void updateScrollBars()
    {
        //TODO this
    }

    @Override
    public List<? extends IGuiEventListener> children()
    {
        return items;
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

    public static class ElementListItem<M> extends ElementFertile<View>
    {
        private final @Nonnull M heldObject; //height 13?
        private List<Element> elements = new ArrayList<>();
        private boolean deselectOnUnfocus = true;
        public boolean selected;

        public ElementListItem(@Nonnull View parent, @Nonnull M heldObject)
        {
            super(parent);
            this.heldObject = heldObject;
        }

        public ElementListItem staySelectedOnDefocus()
        {
            deselectOnUnfocus = false;
            return this;
        }

        public M getObject()
        {
            return heldObject;
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
            }
        }
    }
}
