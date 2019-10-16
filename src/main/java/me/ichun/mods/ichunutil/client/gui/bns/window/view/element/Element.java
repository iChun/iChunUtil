package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import net.minecraft.client.Minecraft;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class Element<M extends View> extends Fragment //TODO handle narration?
{
    public final static List<Element> INFERTILE = Collections.emptyList();

    public String tooltip;

    public Element(@Nonnull M parent) //TODO Make aligned element, which counts the gaps between elements and equally distributes widht/height to each
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
    public void render(int mouseX, int mouseY, float partialTick){}

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        constraint.apply();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(isMouseOver(mouseX, mouseY))
        {
            return true;
        }
        return false;
    }

    @Override
    public boolean changeFocus(boolean direction)
    {
        return parentFragment.getFocused() != this; //focus on us if we're not focused
    }

    @Override
    public @Nullable
    String tooltip(double mouseX, double mouseY)
    {
        return tooltip;
    }

    public void setTooltip(String s)
    {
        tooltip = s;
    }
}
