package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.constraint.Constraint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class ElementFertile<P extends Fragment<?>> extends Element<P>
{
    public ElementFertile(@NotNull P parent)
    {
        super(parent);
    }

    public abstract List<? extends Fragment<?>> children();

    @Override
    public void init()
    {
        super.init();
        children().forEach(Fragment::init);
    }

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        super.resize(mc, width, height);
        children().forEach(child -> child.resize(mc, this.width, this.height));
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
    public ComponentPath nextFocusPath(FocusNavigationEvent event)
    {
        return null;
    }

    public abstract int getBorderSize();

    @Override
    public int getMinWidth()
    {
        int min = 0;
        for(GuiEventListener child : children())
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
        for(GuiEventListener child : children())
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

