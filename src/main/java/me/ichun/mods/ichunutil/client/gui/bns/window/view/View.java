package me.ichun.mods.ichunutil.client.gui.bns.window.view;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.window.IWindows;
import me.ichun.mods.ichunutil.client.gui.bns.window.Window;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.Element;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class View<P extends Window<? extends IWindows>> extends Fragment<P>
{
    public ArrayList<Element<?>> elements = new ArrayList<>();
    public @Nonnull String title; // we localise when this is set

    public View(@Nonnull P parent, @Nonnull String s)
    {
        super(parent);
        title = I18n.format(s);
        constraint = Constraint.matchParent(this, parent, parent.borderSize.get());
        if(parent.canShowTitle() && !s.isEmpty())
        {
            constraint.top(parent, Constraint.Property.Type.TOP, parent.titleSize.get());
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
    public List<Element<?>> getEventListeners()
    {
        return elements;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTick)
    {
        setScissor();
        //render our background
        if(!renderMinecraftStyle())
        {
            fill(stack, getTheme().windowBackground, 0);
        }
        //render attached elements
        for(Element<?> element : elements)
        {
            element.render(stack, mouseX, mouseY, partialTick);
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
        if(parentFragment.getListener() == this)
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
