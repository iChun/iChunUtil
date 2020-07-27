package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.ListIterator;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public abstract class ElementFertile<P extends Fragment> extends Element<P>
{
    public ElementFertile(@Nonnull P parent)
    {
        super(parent);
    }

    public abstract List<? extends Fragment<?>> getEventListeners();

    @Override
    public void init()
    {
        super.init();
        getEventListeners().forEach(Fragment::init);
    }

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        super.resize(mc, width, height);
        getEventListeners().forEach(child -> child.resize(mc, this.width, this.height));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(isMouseOver(mouseX, mouseY))
        {
            boolean hasElement = defaultMouseClicked(mouseX, mouseY, button); //this calls setDragging();
            if(!hasElement && getListener() instanceof Fragment)
            {
                setListener(null);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean changeFocus(boolean direction) // do the default from INestedGuiEventHandler
    {
        IGuiEventListener iguieventlistener = this.getListener();
        boolean flag = iguieventlistener != null;
        if (flag && iguieventlistener.changeFocus(direction)) {
            return true;
        } else {
            List<? extends IGuiEventListener> list = this.getEventListeners();
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
                    this.setListener(iguieventlistener1);
                    return true;
                }
            }

            this.setListener(null);
            return false;
        }
    }

    public abstract int getBorderSize();

    @Override
    public int getMinWidth()
    {
        int min = 0;
        for(IGuiEventListener child : getEventListeners())
        {
            if(child instanceof Fragment<?>)
            {
                Fragment<?> fragment = (Fragment<?>)child;
                int fragWidth = getConstraintSensitiveMinWidth(fragment);
                if(fragWidth > min)
                {
                    min = fragWidth;
                }
            }
        }
        return min > 0 ? min + (getBorderSize() * 2) : 4;
    }

    @Override
    public int getMinHeight()
    {
        int min = 0;
        for(IGuiEventListener child : getEventListeners())
        {
            if(child instanceof Fragment<?>)
            {
                Fragment<?> fragment = (Fragment<?>)child;
                int fragHeight = getConstraintSensitiveMinHeight(fragment);
                if(fragHeight > min)
                {
                    min = fragHeight;
                }
            }
        }
        return min > 0 ? min + (getBorderSize() * 2) : 4;
    }

    public int getConstraintSensitiveMinWidth(Fragment<?> child)
    {
        int ourWidth = child.getMinWidth();
        ourWidth += child.constraint.get(Constraint.Property.Type.LEFT).getDist();
        ourWidth += child.constraint.get(Constraint.Property.Type.RIGHT).getDist();

        Constraint.Property left = child.constraint.get(Constraint.Property.Type.LEFT);
        if(left != Constraint.Property.NONE && left.getReference() != this && left.getReference() instanceof Fragment) //we gotta end somewhere
        {
            ourWidth += getConstraintSensitiveMinWidth((Fragment<?>)left.getReference());
        }

        Constraint.Property right = child.constraint.get(Constraint.Property.Type.RIGHT);
        if(right != Constraint.Property.NONE && right.getReference() != this && right.getReference() instanceof Fragment) //we gotta end somewhere
        {
            ourWidth += getConstraintSensitiveMinWidth((Fragment<?>)right.getReference());
        }
        return ourWidth;
    }

    public int getConstraintSensitiveMinHeight(Fragment<?> child)
    {
        int ourHeight = child.getMinHeight();
        ourHeight += child.constraint.get(Constraint.Property.Type.TOP).getDist();
        ourHeight += child.constraint.get(Constraint.Property.Type.BOTTOM).getDist();

        Constraint.Property top = child.constraint.get(Constraint.Property.Type.TOP);
        if(top != Constraint.Property.NONE && top.getReference() != this && top.getReference() instanceof Fragment) //we gotta end somewhere
        {
            ourHeight += getConstraintSensitiveMinHeight((Fragment<?>)top.getReference());
        }

        Constraint.Property bottom = child.constraint.get(Constraint.Property.Type.BOTTOM);
        if(bottom != Constraint.Property.NONE && bottom.getReference() != this && bottom.getReference() instanceof Fragment) //we gotta end somewhere
        {
            ourHeight += getConstraintSensitiveMinHeight((Fragment<?>)bottom.getReference());
        }
        return ourHeight;
    }
}

