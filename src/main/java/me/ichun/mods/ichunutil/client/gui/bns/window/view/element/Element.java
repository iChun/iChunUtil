package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IRenderable;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class Element<M extends View> extends Fragment
        implements IRenderable
{
    public final static List<Element> INFERTILE = Collections.emptyList();

    public String tooltip;

    public Element(@Nonnull M parent)
    {
        super(parent);
    }

    public <T extends Element> T setPos(int x, int y)
    {
        posX = x;
        posY = y;
        return (T)this;
    }

    public <T extends Element> T setSize(int width, int height)
    {
        this.width = width;
        this.height = height;
        return (T)this;
    }

    public <T extends Element> T setTooltip(String s)
    {
        this.tooltip = s;
        return (T)this;
    }

    @Override
    public void init()
    {
        constraint.apply();
    }

    @Override
    public List<Element> children()
    {
        return INFERTILE;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick)
    {

    }

    public void resize(Minecraft mc, int width, int height)
    {
        constraint.apply();
    }
}
