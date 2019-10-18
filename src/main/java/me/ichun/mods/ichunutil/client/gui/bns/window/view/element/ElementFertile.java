package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.ListIterator;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public abstract class ElementFertile<M extends Fragment> extends Element<M>
{
    public ElementFertile(@Nonnull M parent)
    {
        super(parent);
    }

    @Override
    public void init()
    {
        super.init();
        children().forEach(child -> {
            if(child instanceof Element)
            {
                ((Element)child).init();
            }
        });
    }

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        super.resize(mc, width, height);
        children().forEach(child -> {
            if(child instanceof Element)
            {
                ((Element)child).resize(mc, this.width, this.height);
            }
        });
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(isMouseOver(mouseX, mouseY))
        {
            boolean hasElement = defaultMouseClicked(mouseX, mouseY, button); //this calls setDragging();
            if(!hasElement && getFocused() instanceof Fragment)
            {
                setFocused(null);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean changeFocus(boolean direction) // do the default from INestedGuiEventHandler
    {
        IGuiEventListener iguieventlistener = this.getFocused();
        boolean flag = iguieventlistener != null;
        if (flag && iguieventlistener.changeFocus(direction)) {
            return true;
        } else {
            List<? extends IGuiEventListener> list = this.children();
            int j = list.indexOf(iguieventlistener);
            int i;
            if (flag && j >= 0) {
                i = j + (direction ? 1 : 0);
            } else if (direction) {
                i = 0;
            } else {
                i = list.size();
            }

            ListIterator<? extends IGuiEventListener> listiterator = list.listIterator(i);
            BooleanSupplier booleansupplier = direction ? listiterator::hasNext : listiterator::hasPrevious;
            Supplier<? extends IGuiEventListener> supplier = direction ? listiterator::next : listiterator::previous;

            while(booleansupplier.getAsBoolean()) {
                IGuiEventListener iguieventlistener1 = supplier.get();
                if (iguieventlistener1.changeFocus(direction)) {
                    this.setFocused(iguieventlistener1);
                    return true;
                }
            }

            this.setFocused((IGuiEventListener)null);
            return false;
        }
    }

    @Override
    public int getMinWidth()
    {
        int left = width;
        int right = 0;

        for(IGuiEventListener child : children())
        {
            if(child instanceof Fragment)
            {
                Fragment fragment = (Fragment)child;
                if(fragment.getLeft() < left)
                {
                    left = fragment.getLeft();
                }
                if(fragment.getRight() > right)
                {
                    right = fragment.getRight();
                }
            }
        }

        if(right - left >= 0) // we have elements
        {
            return (right - left) + 4;
        }
        return 6;
    }

    @Override
    public int getMinHeight()
    {
        int top = height;
        int bottom = 0;

        for(IGuiEventListener child : children())
        {
            if(child instanceof Fragment)
            {
                Fragment fragment = (Fragment)child;
                if(fragment.getTop() < top)
                {
                    top = fragment.getTop();
                }
                if(fragment.getBottom() > bottom)
                {
                    bottom = fragment.getBottom();
                }
            }
        }

        if(bottom - top >= 0) // we have elements
        {
            return (bottom - top) + 4;
        }
        return 6;
    }
}

