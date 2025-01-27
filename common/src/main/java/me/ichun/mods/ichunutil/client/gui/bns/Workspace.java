package me.ichun.mods.ichunutil.client.gui.bns;

import com.google.common.base.Splitter;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.client.gui.bns.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.Window;
import me.ichun.mods.ichunutil.client.gui.bns.window.WindowDock;
import me.ichun.mods.ichunutil.client.gui.bns.window.WindowGreyout;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
public abstract class Workspace<W extends Workspace<W>> extends Screen
    implements Rectangle
{
    public static final long CURSOR_ARROW = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
    public static final long CURSOR_IBEAM = GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR);
    public static final long CURSOR_CROSSHAIR = GLFW.glfwCreateStandardCursor(GLFW.GLFW_CROSSHAIR_CURSOR);
    public static final long CURSOR_HAND = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR);
    public static final long CURSOR_HRESIZE = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HRESIZE_CURSOR);
    public static final long CURSOR_VRESIZE = GLFW.glfwCreateStandardCursor(GLFW.GLFW_VRESIZE_CURSOR);

    public static final String ELLIPSIS = "\u2026";//"…";

    public int ellipsisLength = 0;

    public Screen lastScreen;
    private boolean hasInit;

    public String lastTooltip;
    public int tooltipCooldown;

    public Theme theme;
    public int renderMinecraftStyle;

    public long cursorState;

    public ArrayList<Window<W,?>> windows = new ArrayList<>(); //0 = newest

    protected Workspace(Screen lastScreen, Component title)
    {
        super(title);
        this.lastScreen = lastScreen;
        this.theme = Theme.getInstance();
        this.renderMinecraftStyle = iChunUtil.configClient.bnsMinecraftStyle;

        if(canDockWindows())
        {
            windows.add(new WindowDock<>((W)this));
        }
    }

    public W setTheme(Theme theme)
    {
        this.theme = theme;
        return (W)this;
    }

    public W setMinecraftStyle(int i)
    {
        this.renderMinecraftStyle = i;
        return (W)this;
    }

    @Override
    protected void init()
    {
        if(!hasInit)
        {
            hasInit = true;
            ellipsisLength = getFontRenderer().width(ELLIPSIS);

            windows.forEach(Fragment::init);
        }
    }

    public boolean hasInit()
    {
        return hasInit;
    }

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        this.minecraft = mc;
        this.font = mc.font;
        this.width = width;
        this.height = height;
        this.setFocused(null);

        //resize windows
        windows.forEach(window -> window.resize(mc, width, height));
    }

    @Override
    public void onClose()
    {
        //This is closed when the UI is asked to close itself.
        //We do not trigger our children's onClose, that method is for when they themselves get closed. Overriding classes may override that function
        this.minecraft.setScreen(lastScreen);
    }

    @Override
    public void removed()
    {
        //This is called when the screen is removed/unset from MC's main screen
        GLFW.glfwSetCursor(this.minecraft.getWindow().getWindow(), 0);
    }

    @Override
    public List<Window<W,?>> children()
    {
        if(canDockWindows())
        {
            ArrayList<Window<W,?>> winds = new ArrayList<>();
            for(int i = 0; i < windows.size(); i++)
            {
                Window<W,?> window = windows.get(i);
                if(window instanceof WindowDock)
                {
                    ((WindowDock<W>)window).docked.keySet().forEach(h -> winds.addAll(h.windows()));
                }
                else
                {
                    winds.add(window);
                }
            }
            winds.remove(getDock());
            return winds;
        }
        return windows;
    }

    @Override
    public void tick()
    {
        children().forEach(Fragment::tick);
        tooltipCooldown--;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        cursorState = CURSOR_ARROW;

        graphics.pose().pushPose();

        renderBackground(graphics, mouseX, mouseY, partialTick);

        renderWindows(graphics, mouseX, mouseY, partialTick);

        renderTooltip(graphics, mouseX, mouseY, partialTick);

        resetBackground();

        graphics.pose().popPose();

        GLFW.glfwSetCursor(this.minecraft.getWindow().getWindow(), cursorState);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if(renderMinecraftStyle > 0)
        {
            super.renderBackground(graphics, mouseX, mouseY, partialTick);
        }
        else
        {
            RenderSystem.clearColor((float)getTheme().workspaceBackground[0] / 255F, (float)getTheme().workspaceBackground[1] / 255F, (float)getTheme().workspaceBackground[2] / 255F, 255F);
            RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        }
    }

    public void resetBackground()
    {
    }

    public void renderWindows(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        for(int i = windows.size() - 1; i >= 0; i--)
        {
            Window<W,?> window = windows.get(i);
            graphics.pose().translate(0D, 0D, 10D);
            window.render(graphics, mouseX, mouseY, partialTick);
        }
    }

    public void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        //render tooltip
        Fragment<?> topMost = getTopMostFragment(mouseX, mouseY);
        if(topMost != null)
        {
            String tooltip = topMost.tooltip(mouseX, mouseY);
            if(tooltip != null)
            {
                if(!tooltip.equals(lastTooltip))
                {
                    lastTooltip = tooltip;
                    tooltipCooldown = iChunUtil.configClient.bnsTooltipCooldown;
                }
            }
            else
            {
                lastTooltip = null;
            }
        }

        if(lastTooltip != null && tooltipCooldown < 0)
        {
            renderTooltip(graphics, lastTooltip, mouseX, mouseY);
        }
    }

    public void renderTooltip(GuiGraphics graphics, @NotNull String tooltip, int mouseX, int mouseY)
    {
        List<String> textStrings = Splitter.on("\n").splitToList(tooltip);
        if(renderMinecraftStyle > 0)
        {
            List<Component> textLines = new ArrayList<>();
            for(String s : textStrings)
            {
                textLines.add(Component.literal(s));
            }
            graphics.renderTooltip(font, textLines, Optional.empty(), mouseX, mouseY);
        }
        else //Mostly taken from GuiUtils
        {
            List<FormattedText> textLines = new ArrayList<>();
            for(String s : textStrings)
            {
                textLines.add(Component.literal(s));
            }

            int screenWidth = width;
            int screenHeight = height;
            int maxTextWidth = -1;
            Font font = getFontRenderer();

            RenderSystem.disableDepthTest();
            int tooltipTextWidth = 0;

            for (FormattedText textLine : textLines)
            {
                int textLineWidth = font.width(textLine);

                if (textLineWidth > tooltipTextWidth)
                {
                    tooltipTextWidth = textLineWidth;
                }
            }

            boolean needsWrap = false;

            int titleLinesCount = 1;
            int tooltipX = mouseX + 12;
            if (tooltipX + tooltipTextWidth + 4 > screenWidth)
            {
                tooltipX = mouseX - 16 - tooltipTextWidth;
                if (tooltipX < 4) // if the tooltip doesn't fit on the screen
                {
                    if (mouseX > screenWidth / 2)
                    {
                        tooltipTextWidth = mouseX - 12 - 8;
                    }
                    else
                    {
                        tooltipTextWidth = screenWidth - 16 - mouseX;
                    }
                    needsWrap = true;
                }
            }

            if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth)
            {
                tooltipTextWidth = maxTextWidth;
                needsWrap = true;
            }

            if (needsWrap)
            {
                int wrappedTooltipWidth = 0;
                List<FormattedText> wrappedTextLines = new ArrayList<>();
                for (int i = 0; i < textLines.size(); i++)
                {
                    FormattedText textLine = textLines.get(i);
                    List<FormattedText> wrappedLine = font.getSplitter().splitLines(textLine, tooltipTextWidth, Style.EMPTY);
                    if (i == 0)
                    {
                        titleLinesCount = wrappedLine.size();
                    }

                    for (FormattedText line : wrappedLine)
                    {
                        int lineWidth = font.width(line);
                        if (lineWidth > wrappedTooltipWidth)
                        {
                            wrappedTooltipWidth = lineWidth;
                        }
                        wrappedTextLines.add(line);
                    }
                }
                tooltipTextWidth = wrappedTooltipWidth;
                textLines = wrappedTextLines;

                if (mouseX > screenWidth / 2)
                {
                    tooltipX = mouseX - 16 - tooltipTextWidth;
                }
                else
                {
                    tooltipX = mouseX + 12;
                }
            }

            int tooltipY = mouseY - 12;
            int tooltipHeight = 8;

            if (textLines.size() > 1)
            {
                tooltipHeight += (textLines.size() - 1) * 10;
                if (textLines.size() > titleLinesCount) {
                    tooltipHeight += 2; // gap between title lines and next lines
                }
            }

            if (tooltipY < 4)
            {
                tooltipY = 4;
            }
            else if (tooltipY + tooltipHeight + 4 > screenHeight)
            {
                tooltipY = screenHeight - tooltipHeight - 4;
            }

            PoseStack stack = graphics.pose();
            final int zLevel = 400;
            stack.pushPose();
            Matrix4f mat = stack.last().pose();

            RenderHelper.drawColour(graphics, getTheme().windowBorderActive, 255, tooltipX - 3, tooltipY - 3, tooltipTextWidth + 6, tooltipHeight + 6, zLevel);
            RenderHelper.drawColour(graphics, getTheme().windowBackground, 255, tooltipX - 2, tooltipY - 2, tooltipTextWidth + 4, tooltipHeight + 4, zLevel);

            stack.translate(0.0D, 0.0D, zLevel);

            int tooltipTop = tooltipY;

            for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber)
            {
                FormattedText line = textLines.get(lineNumber);
                if(line != null)
                {
                    font.drawInBatch(Language.getInstance().getVisualOrder(line), (float)tooltipX, (float)tooltipY, -1, true, mat, RenderHelper.getBufferSource(), Font.DisplayMode.NORMAL, 0, 15728880);
                }

                if (lineNumber + 1 == titleLinesCount)
                {
                    tooltipY += 2;
                }

                tooltipY += 10;
            }

            stack.popPose();

            graphics.flush();

            RenderSystem.enableDepthTest();
        }
    }

    @Nullable
    public <T extends Fragment<?>> T getById(@NotNull String id)
    {
        Fragment<?> o = null;
        for(GuiEventListener child : children())
        {
            if(o == null && child instanceof Fragment<?> fragment)
            {
                o = fragment.getById(id);
            }
        }
        return (T)o;
    }

    @Nullable
    public <T extends View<?>> T getByViewType(Class<T> clz)
    {
        List<Window<W,?>> windows = children();
        for(Window<W,?> window : windows)
        {
            if(clz.isAssignableFrom(window.getCurrentView().getClass()))
            {
                return (T)window.getCurrentView();
            }
        }
        return null;
    }

    @Nullable
    public <T extends Window<W,?>> T getByWindowType(Class<T> clz)
    {
        List<Window<W,?>> windows = children();
        for(Window<W,?> window : windows)
        {
            if(clz.isAssignableFrom(window.getClass()))
            {
                return (T)window;
            }
        }
        return null;
    }

    @Nullable
    public Fragment<?> getTopMostFragment(double mouseX, double mouseY)
    {
        List<Window<W,?>> children = children();
        for(Window<W,?> child : children)
        {
            Fragment<?> frag = child.getTopMostFragment(mouseX, mouseY);
            if(frag != null)
            {
                return frag;
            }
        }
        return null;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double distX, double distY)
    {
        return super.mouseDragged(mouseX, mouseY, button, distX, distY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        this.setDragging(false);
        return getFocused() != null && getFocused().mouseReleased(mouseX, mouseY, button);
    }

    public boolean isObstructed(Window<W,?> window, double mouseX, double mouseY)
    {
        for(Window<W,?> window1 : children())
        {
            if(Fragment.isMouseBetween(mouseX, window1.getLeft(), window1.getLeft() + window1.width) && Fragment.isMouseBetween(mouseY, window1.getTop(), window1.getTop() + window1.height))
            {
                return window != window1;
            }
        }
        return true; //our window isn't even here! pretend we're obstructed
    }

    //Window management
    public Window<W,?> addWindow(Window<W,?> window)
    {
        if(window.isUnique()) // aw how cute
        {
            List<Window<W,?>> allWindows = children();
            for(int i = allWindows.size() - 1; i >= 0; i--)
            {
                Window<W,?> window1 = allWindows.get(i);
                if(window1.getClass() == window.getClass()) //we're unique. Kill the old one
                {
                    if(isDocked(window1))
                    {
                        window1.onClose();
                        getDock().removeFromDock(window1); //Don't call our own removeFromDock, that readds it back into our list.
                    }
                    else
                    {
                        removeWindow(window1);
                    }
                }
            }
        }
        windows.addFirst(window); //MC's iterator starts from first element of list
        return window;
    }

    public void removeWindow(Window<W,?> window)
    {
        if(getFocused() == window)
        {
            setFocused(null);
        }
        window.onClose();
        windows.remove(window);
    }

    public void bringToFront(Window<W,?> window)
    {
        if(window.canBringToFront() && windows.remove(window))
        {
            addWindow(window);
        }
    }

    public void putInCenter(Window<W,?> window)
    {
        if(!isDocked(window))
        {
            window.pos((int)((getWidth() - window.getWidth()) / 2D), (int)((getHeight() - window.getHeight()) / 2D));
        }
    }

    public void openWindowInCenter(Window<W,?> window, double widthRatio, double heightRatio, boolean greyout)
    {
        if(widthRatio <= 1D)
        {
            window.setWidth((int)(window.getParentWidth() * widthRatio));
        }
        else
        {
            window.setWidth((int)widthRatio);
        }
        if(heightRatio <= 1D)
        {
            window.setHeight((int)(window.getParentHeight() * heightRatio));
        }
        else
        {
            window.setHeight((int)heightRatio);
        }

        if(greyout)
        {
            addGreyout(window);
        }
        addWindow(window);
        putInCenter(window);
        setFocused(window);

        window.init();
    }

    public void openWindowInCenter(Window<W,?> window, double widthRatio, double heightRatio)
    {
        openWindowInCenter(window, widthRatio, heightRatio, false);
    }

    public void openWindowInCenter(Window<W,?> window, boolean greyout)
    {
        openWindowInCenter(window, 0.5D, 0.5D, greyout);
    }

    public void openWindowInCenter(Window<W,?> window)
    {
        openWindowInCenter(window, false);
    }

    public WindowGreyout<?> addGreyout(Window<W, ?> window)
    {
        WindowGreyout<W> greyout = new WindowGreyout<>((W)this, window);
        addWindow(greyout);
        greyout.init();

        return greyout;
    }

    //Dock management
    public boolean canDockWindows()
    {
        return true;
    }

    public WindowDock<W> getDock()
    {
        return (WindowDock<W>)windows.getLast();
    }

    public WindowDock.DockInfo<W> getDockInfo(double mouseX, double mouseY, boolean dockStack)
    {
        if(canDockWindows())
        {
            return getDock().getDockInfo(mouseX, mouseY, dockStack);
        }
        return null;
    }

    public void addToDocked(Window<W,?> docked, Window<W,?> window)
    {
        if(canDockWindows() && getDock().addToDocked(docked, window))
        {
            removeWindow(window);
        }
    }

    public void addToDock(Window<W,?> window, Constraint.Property.Type type)
    {
        if(canDockWindows())
        {
            getDock().addToDock(window, type);
            removeWindow(window);
        }
    }

    public void removeFromDock(Window<W,?> window, boolean addWindow)
    {
        if(canDockWindows())
        {
            getDock().removeFromDock(window);
            if(addWindow)
            {
                addWindow(window);
            }
            else
            {
                removeWindow(window); //calls the close window functions
            }
        }
    }

    public void removeFromDock(Window<W,?> window)
    {
        removeFromDock(window, true);
    }

    public boolean isDocked(Window<W,?> window)
    {
        if(canDockWindows())
        {
            return getDock().isDocked(window);
        }
        return false;
    }

    public boolean sameDockStack(Rectangle window, Rectangle window1)
    {
        if(canDockWindows())
        {
            return getDock().sameDockStack(window, window1);
        }
        return false;
    }

    //Rectangle
    @Override
    public int getLeft()
    {
        return 0;
    }

    @Override
    public int getRight()
    {
        return width;
    }

    @Override
    public int getTop()
    {
        return 0;
    }

    @Override
    public int getBottom()
    {
        return height;
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
    public W getWorkspace()
    {
        return (W)this;
    }

    @Override
    public Minecraft getMinecraft()
    {
        return this.minecraft;
    }

    @Override
    public Theme getTheme()
    {
        return theme;
    }

    @Override
    public Font getFontRenderer()
    {
        return font;
    }

    @Override
    public int renderMinecraftStyle()
    {
        return renderMinecraftStyle;
    }

    //ContainerEventHandler
    @Override
    public void setFocused(@Nullable GuiEventListener gui)
    {
        GuiEventListener lastFocused = getFocused();
        if(lastFocused instanceof Fragment && gui != lastFocused)
        {
            ((Fragment<?>)lastFocused).unfocus(gui);
        }
        if(gui instanceof Window)
        {
            bringToFront((Window<W,?>)gui);
        }
        super.setFocused(gui);
    }
}
