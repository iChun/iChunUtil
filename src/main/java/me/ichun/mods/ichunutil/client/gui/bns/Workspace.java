package me.ichun.mods.ichunutil.client.gui.bns;

import com.google.common.base.Splitter;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import me.ichun.mods.ichunutil.client.gui.bns.window.*;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.Constraint;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.IConstrainable;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.util.IOUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.*;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.glfw.GLFW;
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
    public static final long CURSOR_ARROW = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
    public static final long CURSOR_IBEAM = GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR);
    public static final long CURSOR_CROSSHAIR = GLFW.glfwCreateStandardCursor(GLFW.GLFW_CROSSHAIR_CURSOR);
    public static final long CURSOR_HAND = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR);
    public static final long CURSOR_HRESIZE = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HRESIZE_CURSOR);
    public static final long CURSOR_VRESIZE = GLFW.glfwCreateStandardCursor(GLFW.GLFW_VRESIZE_CURSOR);

    public static final String ELLIPSIS = "\u2026";//"…";

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

    public static void registerObjectInterpreter(Class<?> clz, Function<Object, List<String>> function) //TODO register Entities for .getName()
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

    public long cursorState;

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
    public void closeScreen()
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
        this.minecraft.keyboardListener.enableRepeatEvents(true);
    }

    public boolean hasInit()
    {
        return hasInit;
    }

    @Override
    public void onClose()
    {
        this.minecraft.keyboardListener.enableRepeatEvents(false);

        GLFW.glfwSetCursor(this.minecraft.getMainWindow().getHandle(), 0);
    }

    @Override
    public List<Window<?>> getEventListeners()
    {
        if(canDockWindows())
        {
            ArrayList<Window<?>> winds = new ArrayList<>();
            for(int i = 0; i < windows.size(); i++)
            {
                Window<?> window = windows.get(i);
                if(window instanceof WindowDock)
                {
                    ((WindowDock<?>)window).docked.keySet().forEach(h -> winds.addAll(h.windows));
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
    public Window<?> addWindow(Window<?> window)
    {
        if(window.isUnique()) // aw how cute
        {
            List<Window<?>> allWindows = getEventListeners();
            for(int i = allWindows.size() - 1; i >= 0; i--)
            {
                Window<?> window1 = allWindows.get(i);
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
        windows.add(0, window); //MC's iterator starts from first element of list
        return window;
    }

    @Override
    public void removeWindow(Window<?> window)
    {
        if(getListener() == window)
        {
            setListener(null);
        }
        window.onClose(); //TODO this might bite me in the ass. how can we tell if the window was removed or destroyed????? dock???
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

    public void openWindowInCenter(Window<?> window, double widthRatio, double heightRatio, boolean greyout)
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
            addWindowWithGreyout(window);
        }
        else
        {
            addWindow(window);
        }
        putInCenter(window);
        setListener(window);

        window.init();
    }

    public void openWindowInCenter(Window<?> window, double widthRatio, double heightRatio)
    {
        openWindowInCenter(window, widthRatio, heightRatio, false);
    }

    public void openWindowInCenter(Window<?> window, boolean greyout)
    {
        openWindowInCenter(window, 0.5D, 0.5D, greyout);
    }

    public void openWindowInCenter(Window<?> window)
    {
        openWindowInCenter(window, false);
    }

    public void addWindowWithGreyout(Window<?> window)
    {
        WindowGreyout<?> greyout = new WindowGreyout<>(this, window);
        addWindow(greyout);
        greyout.init();

        addWindow(window);
    }

    @Override
    public void tick()
    {
        getEventListeners().forEach(Fragment::tick);
        tooltipCooldown--;
    }

    public @Nullable <T extends Fragment<?>> T getById(@Nonnull String id)
    {
        Fragment<?> o = null;
        for(IGuiEventListener child : getEventListeners())
        {
            if(o == null && child instanceof Fragment)
            {
                o = ((Fragment<?>)child).getById(id);
            }
        }
        return (T)o;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTick)
    {
        cursorState = CURSOR_ARROW;

        stack.push();
        RenderSystem.enableAlphaTest();
        renderBackground(stack);

        renderWindows(stack, mouseX, mouseY, partialTick);

        renderTooltip(stack, mouseX, mouseY, partialTick);

        resetBackground();
        RenderSystem.enableAlphaTest();
        stack.pop();

        GLFW.glfwSetCursor(this.minecraft.getMainWindow().getHandle(), cursorState);
    }

    public void renderWindows(MatrixStack stack, int mouseX, int mouseY, float partialTick)
    {
        for(int i = windows.size() - 1; i >= 0; i--)
        {
            Window<?> window = windows.get(i);
            stack.translate(0D, 0D, 10D);
            window.render(stack, mouseX, mouseY, partialTick);
        }
    }

    public void renderTooltip(MatrixStack stack, int mouseX, int mouseY, float partialTick)
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
            renderTooltip(stack, lastTooltip, mouseX, mouseY);
        }
    }

    public void renderTooltip(MatrixStack stack, @Nonnull String tooltip, int mouseX, int mouseY)
    {
        List<String> textStrings = Splitter.on("\n").splitToList(tooltip);
        if(renderMinecraftStyle)
        {
            List<ITextComponent> textLines = new ArrayList<>();
            for(String s : textStrings)
            {
                textLines.add(new StringTextComponent(s));
            }
            super.func_243308_b(stack, textLines, mouseX, mouseY);
        }
        else //Mostly taken from GuiUtils
        {
            List<ITextProperties> textLines = new ArrayList<>();
            for(String s : textStrings)
            {
                textLines.add(new StringTextComponent(s));
            }

            ItemStack itemstack = ItemStack.EMPTY;
            int screenWidth = width;
            int screenHeight = height;
            int maxTextWidth = -1;
            FontRenderer font = getFontRenderer();

            RenderTooltipEvent.Pre event = new RenderTooltipEvent.Pre(itemstack, textLines, stack, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, font);
            if (MinecraftForge.EVENT_BUS.post(event)) {
                return;
            }
            mouseX = event.getX();
            mouseY = event.getY();
            screenWidth = event.getScreenWidth();
            screenHeight = event.getScreenHeight();
            maxTextWidth = event.getMaxWidth();
            font = event.getFontRenderer();

            RenderSystem.disableDepthTest();
            int tooltipTextWidth = 0;

            for (ITextProperties textLine : textLines)
            {
                int textLineWidth = font.getStringPropertyWidth(textLine);

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
                List<ITextProperties> wrappedTextLines = new ArrayList<>();
                for (int i = 0; i < textLines.size(); i++)
                {
                    ITextProperties textLine = textLines.get(i);
                    List<ITextProperties> wrappedLine = font.getCharacterManager().func_238362_b_(textLine, tooltipTextWidth, Style.EMPTY);
                    if (i == 0)
                    {
                        titleLinesCount = wrappedLine.size();
                    }

                    for (ITextProperties line : wrappedLine)
                    {
                        int lineWidth = font.getStringPropertyWidth(line);
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

            final int zLevel = 400;
            stack.push();
            Matrix4f mat = stack.getLast().getMatrix();

            RenderHelper.drawColour(stack, getTheme().windowBorder[0], getTheme().windowBorder[1], getTheme().windowBorder[2], 255, tooltipX - 3, tooltipY - 3, tooltipTextWidth + 6, tooltipHeight + 6, zLevel);
            RenderHelper.drawColour(stack, getTheme().windowBackground[0], getTheme().windowBackground[1], getTheme().windowBackground[2], 255, tooltipX - 2, tooltipY - 2, tooltipTextWidth + 4, tooltipHeight + 4, zLevel);

            MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostBackground(itemstack, textLines, stack, tooltipX, tooltipY, font, tooltipTextWidth, tooltipHeight));

            IRenderTypeBuffer.Impl renderType = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
            stack.translate(0.0D, 0.0D, zLevel);

            int tooltipTop = tooltipY;

            for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber)
            {
                ITextProperties line = textLines.get(lineNumber);
                if(line != null)
                {
                    font.func_238416_a_(LanguageMap.getInstance().func_241870_a(line), (float)tooltipX, (float)tooltipY, -1, true, mat, renderType, false, 0, 15728880);
                }

                if (lineNumber + 1 == titleLinesCount)
                {
                    tooltipY += 2;
                }

                tooltipY += 10;
            }

            renderType.finish();
            stack.pop();

            MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostText(itemstack, textLines, stack, tooltipX, tooltipTop, font, tooltipTextWidth, tooltipHeight));

            RenderSystem.enableDepthTest();
        }
    }

    public @Nullable Fragment<?> getTopMostFragment(double mouseX, double mouseY)
    {
        Fragment<?> o = null;
        List<Window<?>> children = getEventListeners();
        for(int i = children.size() - 1; i >= 0; i--) //furthest back to front
        {
            Fragment<?> o1 = children.get(i).getTopMostFragment(mouseX, mouseY);
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
        this.minecraft = mc;
        this.itemRenderer = mc.getItemRenderer();
        this.font = mc.fontRenderer;
        this.width = width;
        this.height = height;
        this.setListener(null);

        //resize windows
        windows.forEach(window -> window.resize(mc, width, height));
    }

    @Override
    public void renderBackground(MatrixStack stack)
    {
        if(renderMinecraftStyle)
        {
            super.renderBackground(stack);
        }
        else
        {
            RenderSystem.matrixMode(GL11.GL_PROJECTION);
            RenderSystem.loadIdentity();
            RenderSystem.ortho(0.0D, minecraft.getMainWindow().getFramebufferWidth() / minecraft.getMainWindow().getGuiScaleFactor(), minecraft.getMainWindow().getFramebufferHeight() / minecraft.getMainWindow().getGuiScaleFactor(), 0.0D, -5000.0D, 5000.0D);
            RenderSystem.matrixMode(GL11.GL_MODELVIEW);
            RenderSystem.loadIdentity();

            RenderSystem.clearColor((float)getTheme().workspaceBackground[0] / 255F, (float)getTheme().workspaceBackground[1] / 255F, (float)getTheme().workspaceBackground[2] / 255F, 255F);
            RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);
        }

        RenderSystem.pushMatrix();
    }

    public void resetBackground()
    {
        RenderSystem.popMatrix();

        if(!renderMinecraftStyle)
        {
            RenderSystem.matrixMode(GL11.GL_PROJECTION);
            RenderSystem.loadIdentity();
            RenderSystem.ortho(0.0D, minecraft.getMainWindow().getFramebufferWidth() / minecraft.getMainWindow().getGuiScaleFactor(), minecraft.getMainWindow().getFramebufferHeight() / minecraft.getMainWindow().getGuiScaleFactor(), 0.0D, 1000.0D, 3000.0D);
            RenderSystem.matrixMode(GL11.GL_MODELVIEW);
            RenderSystem.loadIdentity();
            RenderSystem.translatef(0.0F, 0.0F, -2000.0F);
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
        return getListener() != null && getListener().mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean isObstructed(Window<?> window, double mouseX, double mouseY)
    {
        for(Window<?> window1 : getEventListeners())
        {
            if(Fragment.isMouseBetween(mouseX, window1.getLeft(), window1.getLeft() + window1.width) && Fragment.isMouseBetween(mouseY, window1.getTop(), window1.getTop() + window1.height))
            {
                return window != window1;
            }
        }
        return true; //our window isn't even here! pretend we're obstructed
    }

    public <T extends Window<?>> T getByWindowType(Class<T> clz)
    {
        List<Window<?>> windows = getEventListeners();
        for(Window<?> window : windows)
        {
            if(clz.isAssignableFrom(window.getClass()))
            {
                return (T)window;
            }
        }
        return null;
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
        if(canDockWindows() && getDock().addToDocked(docked, window))
        {
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
    public boolean sameDockStack(IConstrainable window, IConstrainable window1)
    {
        if(canDockWindows())
        {
            return getDock().sameDockStack(window, window1);
        }
        return false;
    }

    @Override
    public void setListener(@Nullable IGuiEventListener gui)
    {
        IGuiEventListener lastFocused = getListener();
        if(lastFocused instanceof Fragment && gui != lastFocused)
        {
            ((Fragment<?>)lastFocused).unfocus(gui);
        }
        if(gui instanceof Window)
        {
            bringToFront((Window<?>)gui);
        }
        super.setListener(gui);
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


    //Convenience method
    public static void bindTexture(ResourceLocation rl)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(rl);
    }
}
