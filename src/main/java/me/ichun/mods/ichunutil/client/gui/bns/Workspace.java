package me.ichun.mods.ichunutil.client.gui.bns;

import com.mojang.blaze3d.platform.GlStateManager;
import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.window.Window;
import me.ichun.mods.ichunutil.client.gui.bns.window.constraint.IConstrainable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class Workspace extends Screen //boxes and stuff!
    implements IConstrainable
{

    private Theme theme = Theme.getInstance();
    public ArrayList<Window> windows = new ArrayList<>(); //0 = newest
    public boolean renderMinecraftStyle;
    private boolean hasInit;

    public Workspace(ITextComponent title)
    {
        super(title);
        renderMinecraftStyle = Screen.hasControlDown(); //TODO remove this
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

    public Theme getTheme()
    {
        return theme;
    }

    @Override
    protected void init()
    {
        if(!hasInit)
        {
            hasInit = true;
            windows.forEach(Fragment::init);
        }
    }

    @Override
    public List<Window> children()
    {
        return windows;
    }

    public void addWindow(Window window)
    {
        windows.add(0, window);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick)
    {
        renderMinecraftStyle = Screen.hasControlDown(); //TODO remove this

        GlStateManager.enableAlphaTest();
        renderBackground();

        windows.forEach(window -> window.render(mouseX, mouseY, partialTick));

        resetBackground();
        GlStateManager.enableAlphaTest();
    }

    @Override
    public void resize(Minecraft mc, int width, int height)
    {
        super.resize(mc, width, height);

        //resize windows
        windows.forEach(window -> window.resize(mc, width, height));
    }

    @Override
    public void renderBackground() //TODO handle deselection of selected
    {
        if(renderMinecraftStyle)
        {
            super.renderBackground();
        }
        else
        {
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0D, minecraft.mainWindow.getFramebufferWidth() / minecraft.mainWindow.getGuiScaleFactor(), minecraft.mainWindow.getFramebufferHeight() / minecraft.mainWindow.getGuiScaleFactor(), 0.0D, -5000.0D, 5000.0D);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.loadIdentity();

            GlStateManager.pushMatrix();

            GlStateManager.clearColor((float)getTheme().workspaceBackground[0] / 255F, (float)getTheme().workspaceBackground[1] / 255F, (float)getTheme().workspaceBackground[2] / 255F, 255F);
            GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);
        }
    }

    public void resetBackground()
    {
        if(!renderMinecraftStyle)
        {
            GlStateManager.popMatrix();

            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0D, minecraft.mainWindow.getFramebufferWidth(), minecraft.mainWindow.getFramebufferHeight(), 0.0D, 1000.0D, 3000.0D);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.loadIdentity();
        }
    }

    public FontRenderer getFontRenderer()
    {
        return font;
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

    @Override
    public void setFocused(@Nullable IGuiEventListener gui)
    {
        IGuiEventListener lastFocused = getFocused();
        if(lastFocused instanceof Fragment && gui != lastFocused)
        {
            ((Fragment)lastFocused).unfocus(gui);
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
