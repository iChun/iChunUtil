package me.ichun.mods.ichunutil.client.gui.bns.window;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.client.gui.bns.Theme;
import me.ichun.mods.ichunutil.client.gui.bns.Workspace;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.IConstrainable;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.IConstrained;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class Fragment<P extends Fragment>
        implements IConstrainable, IConstrained, ContainerEventHandler, Widget
{
    private static final ResourceLocation VANILLA_TABS = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
    private static final ResourceLocation VANILLA_TAB_ITEMS = new ResourceLocation("textures/gui/container/creative_inventory/tab_items.png");
    public static final ResourceLocation VANILLA_WIDGETS = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation VANILLA_STATS_ICON = new ResourceLocation("textures/gui/container/stats_icons.png");
    private static final ResourceLocation VANILLA_HORSE = new ResourceLocation("textures/gui/container/horse.png");

    private static final ResourceLocation TABS = new ResourceLocation("ichunutil", "textures/gui/bns/vanilla/tabs.png");
    private static final ResourceLocation TAB_ITEMS = new ResourceLocation("ichunutil", "textures/gui/bns/vanilla/tab_items.png");
    public static final ResourceLocation WIDGETS = new ResourceLocation("ichunutil", "textures/gui/bns/vanilla/widgets.png");
    private static final ResourceLocation STATS_ICON = new ResourceLocation("ichunutil", "textures/gui/bns/vanilla/stats_icons.png");
    private static final ResourceLocation HORSE = new ResourceLocation("ichunutil", "textures/gui/bns/vanilla/horse.png");

    public ResourceLocation resourceTabs() { return renderMinecraftStyle() == 2 ? VANILLA_TABS : TABS; }
    public ResourceLocation resourceTabItems() { return renderMinecraftStyle() == 2 ? VANILLA_TAB_ITEMS : TAB_ITEMS; }
    public ResourceLocation resourceWidgets() { return renderMinecraftStyle() == 2 ? VANILLA_WIDGETS : WIDGETS; }
    public ResourceLocation resourceStatsIcon() { return renderMinecraftStyle() == 2 ? VANILLA_STATS_ICON : STATS_ICON; }
    public ResourceLocation resourceHorse() { return renderMinecraftStyle() == 2 ? VANILLA_HORSE : HORSE; }

    public P parentFragment;
    public @Nonnull Constraint constraint = Constraint.NONE;
    public @Nullable String id;

    public Fragment(P parentFragment)
    {
        this.parentFragment = parentFragment;
    }

    public <T extends Fragment<?>> T setConstraint(Constraint constraint)
    {
        this.constraint = constraint;
        return (T)this;
    }

    public @Nonnull Constraint constraints()
    {
        if(this.constraint == Constraint.NONE)
        {
            this.constraint = new Constraint(this);
        }
        return this.constraint;
    }

    public <T extends Fragment<?>> T setId(String id)
    {
        this.id = id;
        return (T)this;
    }

    public Theme getTheme()
    {
        return parentFragment.getTheme();
    }

    public int renderMinecraftStyle()
    {
        return parentFragment.renderMinecraftStyle();
    }

    public abstract void init();
    @Override
    public abstract List<? extends Fragment<?>> children();

    public <T extends Workspace> T getWorkspace()
    {
        return (T)parentFragment.getWorkspace();
    }

    public void tick()
    {
        children().forEach(Fragment::tick);
    }

    public void onClose()
    {
        children().forEach(Fragment::onClose);
    }

    public @Nullable <T extends Fragment<?>> T getById(@Nonnull String id)
    {
        if(id.equals(this.id))
        {
            return (T)this;
        }
        Fragment<?> o = null;
        for(GuiEventListener child : children())
        {
            if(o == null && child instanceof Fragment)
            {
                o = ((Fragment<?>)child).getById(id);
            }
        }
        return (T)o;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return parentFragment.isMouseOver(mouseX, mouseY) && isMouseBetween(mouseX, getLeft(), getLeft() + width) && isMouseBetween(mouseY, getTop(), getTop() + height);
    }

    public static boolean isMouseBetween(double mousePos, double p1, double p2)
    {
        return mousePos >= p1 && mousePos < p2;
    }

    public Font getFontRenderer()
    {
        return parentFragment.getFontRenderer();
    }

    public void drawString(PoseStack stack, String s, float posX, float posY)
    {
        drawString(stack, s, posX, posY, renderMinecraftStyle() > 0 ? getMinecraftFontColour() : Theme.getAsHex(getTheme().font));
    }

    public void drawString(PoseStack stack, String s, float posX, float posY, int color)
    {
        if(renderMinecraftStyle() > 0)
        {
            getFontRenderer().drawShadow(stack, s, posX, posY, color);
        }
        else
        {
            getFontRenderer().draw(stack, s, posX, posY, color);
        }
    }

    public int getMinecraftFontColour()
    {
        return 16777215;
    }

    public @Nullable Fragment<?> getTopMostFragment(double mouseX, double mouseY)
    {
        if(isMouseOver(mouseX, mouseY))
        {
            Fragment<?> fragment = this;
            for(GuiEventListener child : this.children())
            {
                if(child instanceof Fragment)
                {
                    Fragment<?> fragment1 = ((Fragment<?>)child).getTopMostFragment(mouseX, mouseY);
                    if(fragment1 != null)
                    {
                        fragment = fragment1;
                    }
                }
            }
            return fragment;
        }
        return null;
    }

    public boolean requireScissor()
    {
        return false;
    }

    public void resetScissorToParent()
    {
        if(!parentFragment.requireScissor())
        {
            parentFragment.resetScissorToParent();
        }
        else
        {
            parentFragment.setScissor();
        }
    }

    public void setScissor()
    {
        RenderHelper.startGlScissor(getLeft(), getTop(), width, height);
    }

    public void endScissor()
    {
        RenderHelper.endGlScissor();
    }

    public void fill(PoseStack stack, int[] colours, int border)
    {
        fill(stack, colours, 255, border);
    }

    public void fill(PoseStack stack, int[] colours, int alpha, int border)
    {
        RenderHelper.drawColour(stack, colours[0], colours[1], colours[2], alpha, getLeft() + border, getTop() + border, width - (border * 2), height - (border * 2), 0);
    }

    public @Nullable String tooltip(double mouseX, double mouseY)
    {
        return null;
    }

    public String reString(String s, int length) //shortens the string and slaps and ellipsis at the end
    {
        if(getFontRenderer().width(s) > length)
        {
            String s1 = s;
            while(getFontRenderer().width(s1 + Workspace.ELLIPSIS) > length)
            {
                s1 = s1.substring(0, s1.length() - 1);
            }
            return s1 + Workspace.ELLIPSIS;
        }
        return s;
    }

    //INestedGuiEventHandler
    @Nullable
    private GuiEventListener focused;
    private boolean isDragging;

    @Override
    public boolean isDragging()
    {
        return this.isDragging;
    }

    @Override
    public void setDragging(boolean b)
    {
        this.isDragging = b;
    }

    @Nullable
    @Override
    public GuiEventListener getFocused()
    {
        return focused;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener iGuiEventListener)
    {
        GuiEventListener lastFocused = getFocused();
        if(lastFocused instanceof Fragment && iGuiEventListener != lastFocused)
        {
            ((Fragment<?>)lastFocused).unfocus(iGuiEventListener);
        }
        focused = iGuiEventListener;
    }

    public void unfocus(@Nullable GuiEventListener guiReplacing) // pass the unfocused event down. Unfocus triggers before focus is set
    {
        GuiEventListener lastFocused = getFocused();
        if(lastFocused instanceof Fragment && guiReplacing != lastFocused)
        {
            ((Fragment<?>)lastFocused).unfocus(guiReplacing);
            setFocused(null); //set focus to nothing. MouseClicked will handle the focus of the new object.
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) //pass down the mouse released to the focused event
    {
        this.setDragging(false);
        return getFocused() != null && getFocused().mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(isMouseOver(mouseX, mouseY)) //only return true if we're clicking on us
        {
            boolean hasElement = ContainerEventHandler.super.mouseClicked(mouseX, mouseY, button); //this calls setDragging();
            if(!hasElement && getFocused() instanceof Fragment)
            {
                setFocused(null);
            }
            return true;
        }
        return false;
    }

    public void resize(Minecraft mc, int width, int height){}

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
    public void expandX(int width) //expands to minimum
    {
        if(this.width < width)
        {
            int lack = width - this.width;
            this.posX -= (lack / 2) + lack % 2;
            this.width = width;
        }
    }

    @Override
    public void expandY(int height)
    {
        if(this.height < height)
        {
            int lack = height - this.height;
            this.posY -= (lack / 2) + lack % 2;
            this.height = height;
        }
    }

    @Override
    public void contractX(int width) //contracts to max
    {
        if(this.width > width)
        {
            int lack = this.width - width;
            this.posX += (lack / 2) + lack % 2;
            this.width = width;
        }
    }

    @Override
    public void contractY(int height)
    {
        if(this.height > height)
        {
            int lack = this.height - height;
            this.posY += (lack / 2) + lack % 2;
            this.height = height;
        }
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

    @Override
    public int getMinWidth()
    {
        return 1;
    }

    @Override
    public int getMinHeight()
    {
        return 1;
    }

    @Override
    public int getMaxWidth()
    {
        return 1000000;
    }

    @Override
    public int getMaxHeight()
    {
        return 1000000;
    }


    //Convenience method
    public static void bindTexture(ResourceLocation rl)
    {
        RenderSystem.setShaderTexture(0, rl);
    }
}
