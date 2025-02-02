package me.ichun.mods.ichunutil.client.gui.bns;

import me.ichun.mods.ichunutil.client.gui.bns.constraint.Constrainable;
import me.ichun.mods.ichunutil.client.gui.bns.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.element.Element;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unchecked")
public abstract class Fragment<P extends Rectangle>
    implements Rectangle, Constrainable, ContainerEventHandler
{
    //TODO convert these two to use TexDef - test with different resource packs
    private static final ResourceLocation VANILLA_HORSE = ResourceLocation.withDefaultNamespace("textures/gui/container/horse.png"); //
    private static final ResourceLocation HORSE = ResourceLocation.fromNamespaceAndPath("ichunutil", "textures/gui/bns/vanilla/horse.png");
    public ResourceLocation resourceHorse() { return renderMinecraftStyle() == 2 ? VANILLA_HORSE : HORSE; }

    private static final ResourceLocation VANILLA_TAB_ITEMS = ResourceLocation.withDefaultNamespace("textures/gui/container/creative_inventory/tab_items.png");
    private static final ResourceLocation TAB_ITEMS = ResourceLocation.fromNamespaceAndPath("ichunutil", "textures/gui/bns/vanilla/tab_items.png");
    public ResourceLocation resourceTabItems() { return renderMinecraftStyle() == 2 ? VANILLA_TAB_ITEMS : TAB_ITEMS; }

    public static final TextureDefinition TEXDEF_SCROLLER = new TextureDefinition(12D, 15D, 0, 12, 0, 15, 2, 2, 2, 10, 2, 13);
    private static final ResourceLocation VANILLA_SCROLLER = ResourceLocation.withDefaultNamespace("textures/gui/sprites/container/creative_inventory/scroller.png");
    private static final ResourceLocation SCROLLER = ResourceLocation.fromNamespaceAndPath("ichunutil", "textures/gui/bns/vanilla/scroller.png");
    public ResourceLocation resourceScroller() { return renderMinecraftStyle() == 2 ? VANILLA_SCROLLER : SCROLLER; }

    public static final TextureDefinition TEXDEF_UP = new TextureDefinition(32D, 32D, 3, 14, 3, 14, 0, 0, 3, 14, 3, 14);
    private static final ResourceLocation VANILLA_UP = ResourceLocation.withDefaultNamespace("textures/gui/sprites/server_list/move_up.png");
    private static final ResourceLocation UP = ResourceLocation.fromNamespaceAndPath("ichunutil", "textures/gui/bns/vanilla/move_up.png");
    public ResourceLocation resourceUp() { return renderMinecraftStyle() == 2 ? VANILLA_UP : UP; }

    public static final TextureDefinition TEXDEF_DOWN = new TextureDefinition(32D, 32D, 3, 14, 18, 29, 0, 0, 3, 14, 18, 29);
    private static final ResourceLocation VANILLA_DOWN = ResourceLocation.withDefaultNamespace("textures/gui/sprites/server_list/move_down.png");
    private static final ResourceLocation DOWN = ResourceLocation.fromNamespaceAndPath("ichunutil", "textures/gui/bns/vanilla/move_down.png");
    public ResourceLocation resourceDown() { return renderMinecraftStyle() == 2 ? VANILLA_DOWN : DOWN; }

    public static final TextureDefinition TEXDEF_BUTTON = new TextureDefinition(200D, 20D, 0, 200, 0, 20, 3, 3, 3, 197, 3, 17);
    private static final ResourceLocation VANILLA_BUTTON = ResourceLocation.withDefaultNamespace("textures/gui/sprites/widget/button.png");
    private static final ResourceLocation VANILLA_BUTTON_DISABLED = ResourceLocation.withDefaultNamespace("textures/gui/sprites/widget/button_disabled.png");
    private static final ResourceLocation VANILLA_BUTTON_HIGHLIGHTED = ResourceLocation.withDefaultNamespace("textures/gui/sprites/widget/button_highlighted.png");
    private static final ResourceLocation BUTTON = ResourceLocation.fromNamespaceAndPath("ichunutil", "textures/gui/bns/vanilla/button.png");
    private static final ResourceLocation BUTTON_DISABLED = ResourceLocation.fromNamespaceAndPath("ichunutil", "textures/gui/bns/vanilla/button_disabled.png");
    private static final ResourceLocation BUTTON_HIGHLIGHTED = ResourceLocation.fromNamespaceAndPath("ichunutil", "textures/gui/bns/vanilla/button_highlighted.png");
    public ResourceLocation resourceButton(Element.ButtonState state)
    {
        if(renderMinecraftStyle() == 1) // Vanilla style
        {
            return switch(state)
            {
                case CLICK -> VANILLA_BUTTON_DISABLED;
                case IDLE -> VANILLA_BUTTON;
                default -> VANILLA_BUTTON_HIGHLIGHTED;
            };
        }
        else //texture pack style
        {
            return switch(state)
            {
                case CLICK -> BUTTON_DISABLED;
                case IDLE -> BUTTON;
                default -> BUTTON_HIGHLIGHTED;
            };
        }
    }

    @NotNull
    public P parent;

    @NotNull
    public Constraint constraint = Constraint.NONE;

    @Nullable
    public String id;

    public Fragment(@NotNull P parent)
    {
        this.parent = parent;
    }

    public <T extends Fragment<?>> T setPos(int x, int y)
    {
        this.posX = x;
        this.posY = y;
        return (T)this;
    }

    public <T extends Fragment<?>> T setSize(int width, int height)
    {
        this.width = width;
        this.height = height;
        return (T)this;
    }

    public <T extends Fragment<?>> T setConstraint(Constraint constraint)
    {
        this.constraint = constraint;
        return (T)this;
    }

    @NotNull
    public Constraint constraints()
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

    @Nullable
    public <T extends Fragment<?>> T getById(@NotNull String id)
    {
        if(id.equals(this.id))
        {
            return (T)this;
        }
        Fragment<?> o = null;
        for(Fragment<?> child : children())
        {
            if(o == null)
            {
                o = child.getById(id);
            }
        }
        return (T)o;
    }

    @Override
    public abstract List<? extends Fragment<?>> children();

    public void init()
    {
        constraint.apply();
        children().forEach(Fragment::init);
    }

    public void onClose()
    {
        children().forEach(Fragment::onClose);
    }

    @Nullable
    public String tooltip(double mouseX, double mouseY)
    {
        return null;
    }

    public void tick()
    {
        children().forEach(Fragment::tick);
    }

    @Nullable
    public Fragment<?> getTopMostFragment(double mouseX, double mouseY)
    {
        if(isMouseOver(mouseX, mouseY))
        {
            Fragment<?> frag = this;
            for(Fragment<?> fragment : this.children())
            {
                Fragment<?> fragment1 = fragment.getTopMostFragment(mouseX, mouseY);
                if(fragment1 != null)
                {
                    frag = fragment1;
                }
            }
            return frag;
        }
        return null;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        if(parent instanceof Fragment<?> fragment)
        {
            return fragment.isMouseOver(mouseX, mouseY) && isMouseBetween(mouseX, getLeft(), getRight()) && isMouseBetween(mouseY, getTop(), getBottom());
        }
        return false;
    }

    public static boolean isMouseBetween(double mousePos, double p1, double p2)
    {
        return mousePos >= p1 && mousePos < p2;
    }

    public boolean requireScissor()
    {
        return false;
    }

    public void resetScissorToParent()
    {
        if(parent instanceof Fragment<?> fragment)
        {
            if(!fragment.requireScissor())
            {
                fragment.resetScissorToParent();
            }
            else
            {
                fragment.setScissor();
            }
        }
        else
        {
            endScissor();
        }
    }

    public void setScissor()
    {
        drawBatch();
        RenderHelper.startGlScissor(getLeft(), getTop(), width, height);
    }

    public void endScissor()
    {
        drawBatch();
        RenderHelper.endGlScissor();
    }

    protected void drawBatch()
    {
        //because we set/change scissor viewports, some stuff may not get rendered. End the batch rendering before we do something else.
        //it's inefficient, but necessary.
        RenderHelper.getBufferSource().endBatch();
    }

    public void fill(GuiGraphics graphics, int[] colours, int border)
    {
        fill(graphics, colours, 255, border);
    }

    public void fill(GuiGraphics graphics, int[] colours, int alpha, int border)
    {
        RenderHelper.drawColour(graphics, colours[0], colours[1], colours[2], alpha, getLeft() + border, getTop() + border, width - (border * 2), height - (border * 2), 0);
    }

    public void drawString(GuiGraphics graphics, String s, float posX, float posY)
    {
        drawString(graphics, s, posX, posY, renderMinecraftStyle() > 0 ? getMinecraftFontColour() : Theme.getAsHex(getTheme().font));
    }

    public void drawString(GuiGraphics graphics, String s, float posX, float posY, int color)
    {
        graphics.drawString(getFontRenderer(), s, (int)posX, (int)posY, color, renderMinecraftStyle() > 0);
    }

    public int getMinecraftFontColour()
    {
        return 0xFFFFFF;
    }

    public String reString(String s, int length) //shortens the string and slaps and ellipsis at the end
    {
        if(getFontRenderer().width(s) > length)
        {
            String s1 = s;
            while(!s1.isEmpty() && getFontRenderer().width(s1 + getWorkspace().ellipsisLength) > length)
            {
                s1 = s1.substring(0, s1.length() - 1);
            }
            return s1 + Workspace.ELLIPSIS;
        }
        return s;
    }

    /**
     * Called when the workspace is resized
     * @param mc Minecraft instance
     * @param width Workspace width
     * @param height Workspace height
     */
    public void resize(Minecraft mc, int width, int height)
    {
        constraint.apply();
        children().forEach(child -> child.resize(mc, width, height));
    }

    //ContainerEventHandler
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

    //Rectangle
    public int posX;
    public int posY;
    public int width;
    public int height;

    @Override
    public int getLeft() //gets true position on screen.
    {
        return parent.getLeft() + posX;
    }

    @Override
    public int getRight() //gets true position on screen.
    {
        return parent.getLeft() + posX + width;
    }

    @Override
    public int getTop() //gets true position on screen.
    {
        return parent.getTop() + posY;
    }

    @Override
    public int getBottom() //gets true position on screen.
    {
        return parent.getTop() + posY + height;
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
    @SuppressWarnings("rawtypes")
    public <W extends Workspace> W getWorkspace()
    {
        return parent.getWorkspace();
    }

    @Override
    public Minecraft getMinecraft()
    {
        return parent.getMinecraft();
    }

    @Override
    public Theme getTheme()
    {
        return parent.getTheme();
    }

    @Override
    public Font getFontRenderer()
    {
        return parent.getFontRenderer();
    }

    @Override
    public int renderMinecraftStyle()
    {
        return parent.renderMinecraftStyle();
    }

    //Constrainable
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
        this.posX = x - parent.getLeft();
    }

    @Override
    public void setRight(int x)
    {
        this.width = x - getLeft();
    }

    @Override
    public void setTop(int y)
    {
        this.posY = y - parent.getTop();
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
    public int getParentWidth()
    {
        return parent.getWidth();
    }

    @Override
    public int getParentHeight()
    {
        return parent.getHeight();
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
}
