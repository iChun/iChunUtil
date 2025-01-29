package me.ichun.mods.ichunutil.client.gui.bns.window.view;

import me.ichun.mods.ichunutil.client.gui.bns.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.Window;
import me.ichun.mods.ichunutil.client.gui.bns.window.WindowGeneric;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.Element;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class View<P extends Window<?,?>> extends Fragment<P>
{
    public ArrayList<Element<?>> elements = new ArrayList<>();

    @NotNull
    public String title; // we localise when this is set

    public View(@NotNull P parent, @NotNull String s)
    {
        super(parent);
        title = I18n.get(s);

        //TODO This Constraint needs to be adjusted
        constraint = Constraint.matchParent(this, parent, parent.borderSize.get());
        if(parent.canShowTitle() && !s.isEmpty())
        {
            constraint.top(parent, Constraint.Property.Type.TOP, parent.titleSize.get());
        }
    }

    public void setWindowGenericProperties(WindowGeneric<?, ?> window) {}

    @Override
    public List<Element<?>> children()
    {
        return elements;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        setScissor();
        //render our background
        renderBackground(graphics);

        //render attached elements
        for(Element<?> element : elements)
        {
            element.render(graphics, mouseX, mouseY, partialTick);
        }

        resetScissorToParent();
    }

    public void renderBackground(GuiGraphics graphics)
    {
        if(renderMinecraftStyle() == 0)
        {
            fill(graphics, getTheme().windowBackground, 0);
        }
    }

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        constraint.apply();
        elements.forEach(element -> element.resize(mc, width, height));
    }

    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent event)
    {
        if(parent.getFocused() == this)
        {
            return super.nextFocusPath(event);
        }
        return null; //we're not focused anyway, so, nah
    }

    @Override
    public boolean requireScissor()
    {
        return true;
    }
}
