package me.ichun.mods.ichunutil.client.gui.bns.window;

import me.ichun.mods.ichunutil.client.gui.bns.Theme;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.IConstrainable;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.IConstrained;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.INestedGuiEventHandler;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public abstract class Fragment<M extends Fragment> implements IConstrainable, IConstrained, INestedGuiEventHandler
{
    public static final ResourceLocation VANILLA_TABS = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
    public static final ResourceLocation VANILLA_WIDGETS = new ResourceLocation("textures/gui/widgets.png");

    public M parentFragment;
    public @Nonnull Constraint constraint = Constraint.NONE;
    public Fragment(M parentFragment)
    {
        this.parentFragment = parentFragment;
    }

    public <T extends Fragment> T setConstraint(Constraint constraint)
    {
        this.constraint = constraint;
        return (T)this;
    }

    public Theme getTheme()
    {
        return parentFragment.getTheme();
    }

    public boolean renderMinecraftStyle()
    {
        return parentFragment.renderMinecraftStyle();
    }

    public abstract void init();

    public boolean isInBounds(double mouseX, double mouseY)
    {
        return isMouseBetween(mouseX, getLeft(), getLeft() + width) && isMouseBetween(mouseY, getTop(), getTop() + height);
    }

    public boolean isMouseBetween(double mousePos, double p1, double p2)
    {
        return mousePos >= p1 && mousePos < p2;
    }

    public FontRenderer getFontRenderer()
    {
        return parentFragment.getFontRenderer();
    }

    public void drawString(String s, float posX, float posY, int clr)
    {
        if(renderMinecraftStyle())
        {
            getFontRenderer().drawStringWithShadow(s, posX, posY, 16777215);
        }
        else
        {
            getFontRenderer().drawString(s, posX, posY, Theme.getAsHex(getTheme().font));
        }
    }

    public void unfocus(@Nullable IGuiEventListener gui){}

    //INestedGuiEventHandler
    @Nullable
    private IGuiEventListener focused;
    private boolean isDragging;

    @Override
    public boolean isDragging()
    {
        return this.isDragging;
    }

    @Override
    public void setDragging(boolean b)
    {
        this.isDragging = true;
    }

    @Nullable
    @Override
    public IGuiEventListener getFocused()
    {
        return focused;
    }

    @Override
    public void setFocused(@Nullable IGuiEventListener iGuiEventListener)
    {
        focused = iGuiEventListener;
    }

    //IConstrainable
    public int posX;
    public int posY;
    public int width;
    public int height;

    @Override
    public int getLeft() //gets true position on screen.
    {
        return parentFragment.getLeft() + posX;
    }

    @Override
    public int getRight() //gets true position on screen.
    {
        return parentFragment.getLeft() + posX + width;
    }

    @Override
    public int getTop() //gets true position on screen.
    {
        return parentFragment.getTop() + posY;
    }

    @Override
    public int getBottom() //gets true position on screen.
    {
        return parentFragment.getTop() + posY + height;
    }

    //IConstrained
    @Override
    public void setPosX(int x)
    {
        this.posX = x;
    }

    @Override
    public void setPosY(int y)
    {
        this.posY = y;
    }

    @Override
    public void setLeft(int x) // this will be a the new left
    {
        this.posX = x - parentFragment.getLeft();
    }

    @Override
    public void setRight(int x)
    {
        this.width = x - getLeft();
    }

    @Override
    public void setTop(int y)
    {
        this.posY = y - parentFragment.getTop();
    }

    @Override
    public void setBottom(int y)
    {
        this.height = y - getTop();
    }

    @Override
    public void setWidth(int width)
    {
        this.width = width;
    }

    @Override
    public void setHeight(int height)
    {
        this.height = height;
    }

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public int getParentWidth()
    {
        return parentFragment.getWidth();
    }

    @Override
    public int getParentHeight()
    {
        return parentFragment.getHeight();
    }
}
