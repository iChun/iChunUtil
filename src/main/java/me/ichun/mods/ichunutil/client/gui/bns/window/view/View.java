package me.ichun.mods.ichunutil.client.gui.bns.window.view;

import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.window.Window;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.Element;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IRenderable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class View<M extends Window> extends Fragment
        implements IRenderable
{
    public ArrayList<Element> elements = new ArrayList<>();

    public View(@Nonnull M parent)
    {
        super(parent);
        constraint = Constraint.matchParent(this, parent, parent.borderSize);
        if(parent.hasTitle())
        {
            constraint.top(parent, Constraint.Property.Type.TOP, parent.titleSize);
        }
    }

    public <T extends View> T setPos(int x, int y)
    {
        posX = x;
        posY = y;
        return (T)this;
    }

    public <T extends View> T setSize(int width, int height)
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
    public List<Element> children()
    {
        return elements;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick)
    {
        //render our background
        if(!renderMinecraftStyle())
        {
            int width = getRight() - getLeft();
            int height = getBottom() - getTop();
            RenderHelper.drawColour(getTheme().windowBackground[0], getTheme().windowBackground[1], getTheme().windowBackground[2], 255, getLeft(), getTop(), width, height, 0);
        }
        //render attached elements
        for(Element element : elements)
        {
            element.render(mouseX, mouseY, partialTick);
        }
    }

    public void resize(Minecraft mc, int width, int height)
    {
        constraint.apply();
        elements.forEach(element -> element.resize(mc, this.width, this.height));
    }
}
