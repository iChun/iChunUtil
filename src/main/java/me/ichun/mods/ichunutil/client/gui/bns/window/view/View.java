package me.ichun.mods.ichunutil.client.gui.bns.window.view;

import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.window.Window;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.Element;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class View<M extends Window<?>> extends Fragment
{
    public ArrayList<Element<?>> elements = new ArrayList<>();
    public @Nonnull String title; // we localise when this is set

    public View(@Nonnull M parent, @Nonnull String s)
    {
        super(parent);
        title = I18n.format(s);
        constraint = Constraint.matchParent(this, parent, (Integer)parent.borderSize.get()); //TODO might have to redo
        if(parent.canShowTitle() && !s.isEmpty())
        {
            constraint.top(parent, Constraint.Property.Type.TOP, (Integer)parent.titleSize.get());
        }
    }

    public <T extends View<?>> T setPos(int x, int y)
    {
        posX = x;
        posY = y;
        return (T)this;
    }

    public <T extends View<?>> T setSize(int width, int height)
    {
        this.width = width;
        this.height = height;
        return (T)this;
    }

    @Override
    public void init()
    {
        constraint.apply();
        elements.forEach(Fragment::init);
    }

    @Override
    public List<Element<?>> children()
    {
        return elements;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick)
    {
        setScissor();
        //render our background
        if(!renderMinecraftStyle())
        {
            int width = getRight() - getLeft();
            int height = getBottom() - getTop();
            fill(getTheme().windowBackground, 0);
        }
        //render attached elements
        for(Element<?> element : elements)
        {
            element.render(mouseX, mouseY, partialTick);
        }

        resetScissorToParent();
    }

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        constraint.apply();
        elements.forEach(element -> element.resize(mc, this.width, this.height));
    }

    @Override
    public boolean changeFocus(boolean direction)
    {
        if(parentFragment.getFocused() == this)
        {
            boolean flag = super.changeFocus(direction);
            if(!flag)
            {
                flag = super.changeFocus(direction);
            }
            return flag;
        }
        return false; //we're not focused anyway, so, nah
    }

    @Override
    public boolean requireScissor()
    {
        return true;
    }
}
