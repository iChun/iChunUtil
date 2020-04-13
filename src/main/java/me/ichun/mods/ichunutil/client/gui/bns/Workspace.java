package me.ichun.mods.ichunutil.client.gui.bns;

import com.google.common.base.Splitter;
import com.mojang.blaze3d.systems.RenderSystem;
import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.window.IWindows;
import me.ichun.mods.ichunutil.client.gui.bns.window.Window;
import me.ichun.mods.ichunutil.client.gui.bns.window.WindowDock;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.IConstrainable;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.util.IOUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public abstract class Workspace extends Screen //boxes and stuff!
        implements IConstrainable, IWindows
{
    public static final String ELLIPSIS = "…";

    private static HashMap<Class<?>, Function<Object, List<String>>> OBJECT_INTERPRETER = new HashMap<>();
    {
        OBJECT_INTERPRETER.put(File.class, (o) -> {
            File file = (File)o;
            List<String> info = new ArrayList<>();
            info.add(file.getName());
            info.add((new SimpleDateFormat()).format(new Date(file.lastModified())));
            info.add(IOUtil.readableFileSize(file.length()));
            return info;
        });
        OBJECT_INTERPRETER.put(Theme.class, (o) -> Collections.singletonList(((Theme)o).name + " - " + ((Theme)o).author));
    }

    public static @Nonnull List<String> getInterpretedInfo(Object o)
    {
        Map.Entry<Class<?>, Function<Object, List<String>>> lastEntryUsed = null;
        List<String> infos = null;
        for(Map.Entry<Class<?>, Function<Object, List<String>>> e : OBJECT_INTERPRETER.entrySet())
        {
            if(e.getKey().isInstance(o))
            {
                if(!(lastEntryUsed != null && e.getKey().isAssignableFrom(lastEntryUsed.getKey()))) // !(the last entry extends our current class)
                {
                    lastEntryUsed = e;
                    infos = e.getValue().apply(o);
                }
            }
        }
        if(infos == null)
        {
            infos = new ArrayList<>();
            infos.add(o.toString());
        }
        return infos;
    }

    public static void registerObjectInterpreter(Class<?> clz, Function<Object, List<String>> function)
    {
        OBJECT_INTERPRETER.put(clz, function);
    }

    public int ellipsisLength = 0;

    private Theme theme = Theme.getInstance();
    public ArrayList<Window<?>> windows = new ArrayList<>(); //0 = newest
    private boolean renderMinecraftStyle;
    private boolean hasInit;

    private Screen lastScreen;

    public String lastTooltip;
    public int tooltipCooldown;

    public Workspace(Screen lastScreen, ITextComponent title, boolean mcStyle)
    {
        super(title);
        this.lastScreen = lastScreen;
        renderMinecraftStyle = mcStyle;

        if(canDockWindows())
        {
            windows.add(new WindowDock<>(this));
        }
    }

    public <T extends Workspace> T setTheme(Theme theme)
    {
        this.theme = theme;
        return (T)this;
    }

    public <T extends Workspace> T setMinecraftStyle()
    {
        this.renderMinecraftStyle = true;
        return (T)this;
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
    public Theme getTheme()
    {
        return theme;
    }

    @Override
    public void onClose()
    {
        this.minecraft.displayGuiScreen(lastScreen);
    }

    @Override
    protected void init()
    {
        if(!hasInit)
        {
            hasInit = true;
            ellipsisLength = getFontRenderer().getStringWidth(ELLIPSIS);

            windows.forEach(Fragment::init);
        }
    }

    public boolean hasInit()
    {
        return hasInit;
    }

    @Override
    public List<Window<?>> children()
    {
        if(canDockWindows())
        {
            ArrayList<Window<?>> winds = new ArrayList<>(windows);
            getDock().docked.keySet().forEach(winds::addAll);
            winds.remove(getDock());
            return winds;
        }
        return windows;
    }

    @Override
    public Window<?> addWindow(Window<?> window)
    {
        windows.add(0, window); //MC's iterator starts from first element of list
        return window;
    }

    @Override
    public void removeWindow(Window<?> window)
    {
        windows.remove(window);
    }

    public void bringToFront(Window<?> window)
    {
        if(window.canBringToFront() && windows.remove(window))
        {
            addWindow(window);
        }
    }

    public void putInCenter(Window<?> window)
    {
        if(!isDocked(window))
        {
            window.pos((int)((getWidth() - window.getWidth()) / 2D), (int)((getHeight() - window.getHeight()) / 2D));
        }
    }

    @Override
    public void tick()
    {
        children().forEach(Fragment::tick);
        tooltipCooldown--;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick)
    {
        RenderSystem.enableAlphaTest();
        renderBackground();

        renderWindows(mouseX, mouseY, partialTick);

        renderTooltip(mouseX, mouseY, partialTick);

        resetBackground();
        RenderSystem.enableAlphaTest();
    }

    public void renderWindows(int mouseX, int mouseY, float partialTick)
    {
        for(int i = windows.size() - 1; i >= 0; i--)
        {
            Window<?> window = windows.get(i);
            window.render(mouseX, mouseY, partialTick);
        }
    }

    public void renderTooltip(int mouseX, int mouseY, float partialTick)
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
                    tooltipCooldown = iChunUtil.configClient.guiTooltipCooldown;
                }
            }
            else
            {
                lastTooltip = null;
            }
        }

        if(lastTooltip != null && tooltipCooldown < 0)
        {
            renderTooltip(lastTooltip, mouseX, mouseY);
        }
    }

    @Override
    public void renderTooltip(@Nonnull String tooltip, int mouseX, int mouseY)
    {
        if(renderMinecraftStyle)
        {
            super.renderTooltip(Splitter.on("\n").splitToList(tooltip), mouseX, mouseY);
        }
        else
        {
            ItemStack stack = ItemStack.EMPTY;
            List<String> textLines = Splitter.on("\n").splitToList(tooltip);
            int screenWidth = width;
            int screenHeight = height;
            int maxTextWidth = -1;
            FontRenderer font = getFontRenderer();

            RenderTooltipEvent.Pre event = new RenderTooltipEvent.Pre(stack, textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, font);
            if (MinecraftForge.EVENT_BUS.post(event)) {
                return;
            }
            mouseX = event.getX();
            mouseY = event.getY();
            screenWidth = event.getScreenWidth();
            screenHeight = event.getScreenHeight();
            maxTextWidth = event.getMaxWidth();
            font = event.getFontRenderer();

            //TODO hmmmmmmmmm do we need these calls?
            RenderSystem.disableRescaleNormal();
            net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
            RenderSystem.disableLighting();
            RenderSystem.disableDepthTest();
            int tooltipTextWidth = 0;

            for (String textLine : textLines)
            {
                int textLineWidth = font.getStringWidth(textLine);

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
                List<String> wrappedTextLines = new ArrayList<String>();
                for (int i = 0; i < textLines.size(); i++)
                {
                    String textLine = textLines.get(i);
                    List<String> wrappedLine = font.listFormattedStringToWidth(textLine, tooltipTextWidth);
                    if (i == 0)
                    {
                        titleLinesCount = wrappedLine.size();
                    }

                    for (String line : wrappedLine)
                    {
                        int lineWidth = font.getStringWidth(line);
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

            RenderHelper.drawColour(getTheme().windowBorder[0], getTheme().windowBorder[1], getTheme().windowBorder[2], 255, tooltipX - 3, tooltipY - 3, tooltipTextWidth + 6, tooltipHeight + 6, 300);
            RenderHelper.drawColour(getTheme().windowBackground[0], getTheme().windowBackground[1], getTheme().windowBackground[2], 255, tooltipX - 2, tooltipY - 2, tooltipTextWidth + 4, tooltipHeight + 4, 300);

            MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostBackground(stack, textLines, tooltipX, tooltipY, font, tooltipTextWidth, tooltipHeight));
            int tooltipTop = tooltipY;

            for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber)
            {
                String line = textLines.get(lineNumber);
                font.drawStringWithShadow(line, (float)tooltipX, (float)tooltipY, -1);

                if (lineNumber + 1 == titleLinesCount)
                {
                    tooltipY += 2;
                }

                tooltipY += 10;
            }

            MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostText(stack, textLines, tooltipX, tooltipTop, font, tooltipTextWidth, tooltipHeight));

            RenderSystem.enableLighting();
            RenderSystem.enableDepthTest();
            net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
            RenderSystem.enableRescaleNormal();
        }
    }

    public @Nullable Fragment<?> getTopMostFragment(double mouseX, double mouseY)
    {
        Fragment<?> o = null;
        for(int i = windows.size() - 1; i >= 0; i--) //furthest back to front
        {
            Fragment<?> o1 = windows.get(i).getTopMostFragment(mouseX, mouseY);
            if(o1 != null)
            {
                o = o1;
            }
        }
        return o;
    }

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        super.resize(mc, width, height);

        //resize windows
        windows.forEach(window -> window.resize(mc, width, height));
    }

    @Override
    public void renderBackground()
    {
        if(renderMinecraftStyle)
        {
            super.renderBackground();
        }
        else
        {
            RenderSystem.matrixMode(GL11.GL_PROJECTION);
            RenderSystem.loadIdentity();
            RenderSystem.ortho(0.0D, minecraft.getMainWindow().getFramebufferWidth() / minecraft.getMainWindow().getGuiScaleFactor(), minecraft.getMainWindow().getFramebufferHeight() / minecraft.getMainWindow().getGuiScaleFactor(), 0.0D, -5000.0D, 5000.0D);
            RenderSystem.matrixMode(GL11.GL_MODELVIEW);
            RenderSystem.loadIdentity();

            RenderSystem.pushMatrix();

            RenderSystem.clearColor((float)getTheme().workspaceBackground[0] / 255F, (float)getTheme().workspaceBackground[1] / 255F, (float)getTheme().workspaceBackground[2] / 255F, 255F);
            RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);
        }
    }

    public void resetBackground()
    {
        if(!renderMinecraftStyle)
        {
            RenderSystem.popMatrix();

            RenderSystem.matrixMode(GL11.GL_PROJECTION);
            RenderSystem.loadIdentity();
            RenderSystem.ortho(0.0D, minecraft.getMainWindow().getFramebufferWidth(), minecraft.getMainWindow().getFramebufferHeight(), 0.0D, 1000.0D, 3000.0D);
            RenderSystem.matrixMode(GL11.GL_MODELVIEW);
            RenderSystem.loadIdentity();
        }
    }

    @Override
    public FontRenderer getFontRenderer()
    {
        return font;
    }

    @Override
    public boolean renderMinecraftStyle()
    {
        return renderMinecraftStyle;
    }

    //TODO do we want to pass in escape??


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

    @Override
    public boolean isObstructed(Window<?> window, double mouseX, double mouseY)
    {
        if(isDocked(window))
        {
            return isObstructed(getDock(), mouseX, mouseY);
        }
        for(Window<?> window1 : windows) //TODO exclude the dock
        {
            if(Fragment.isMouseBetween(mouseX, window1.getLeft(), window1.getLeft() + window1.width) && Fragment.isMouseBetween(mouseY, window1.getTop(), window1.getTop() + window1.height))
            {
                return window != window1;
            }
        }
        return true; //our window isn't even here! pretend we're obstructed
    }

    @Override
    public boolean canDockWindows()
    {
        return true;
    }

    public WindowDock<? extends Workspace> getDock()
    {
        return (WindowDock<? extends Workspace>)windows.get(windows.size() - 1);
    }

    @Override
    public DockInfo getDockInfo(double mouseX, double mouseY, boolean dockStack)
    {
        if(canDockWindows())
        {
            return getDock().getDockInfo(mouseX, mouseY, dockStack);
        }
        return null;
    }

    @Override
    public void addToDocked(Window<?> docked, Window<?> window)
    {
        if(true) return; //TODO disable this for now
        if(canDockWindows())
        {
            getDock().addToDocked(docked, window);
            removeWindow(window);
        }
    }

    @Override
    public void addToDock(Window<?> window, Constraint.Property.Type type)
    {
        if(canDockWindows())
        {
            getDock().addToDock(window, type);
            removeWindow(window);
        }
    }

    @Override
    public void removeFromDock(Window<?> window)
    {
        if(canDockWindows())
        {
            getDock().removeFromDock(window);
            addWindow(window);
        }
    }

    @Override
    public boolean isDocked(Window<?> window)
    {
        if(canDockWindows())
        {
            return getDock().isDocked(window);
        }
        return false;
    }

    @Override
    public void setFocused(@Nullable IGuiEventListener gui)
    {
        IGuiEventListener lastFocused = getFocused();
        if(lastFocused instanceof Fragment && gui != lastFocused)
        {
            ((Fragment<?>)lastFocused).unfocus(gui);
        }
        if(gui instanceof Window)
        {
            bringToFront((Window<?>)gui);
        }
        super.setFocused(gui);
    }

    //IConstrainable
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
}
