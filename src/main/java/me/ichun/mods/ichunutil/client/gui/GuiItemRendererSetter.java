package me.ichun.mods.ichunutil.client.gui;

import me.ichun.mods.ichunutil.client.gui.window.IWorkspace;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.gui.window.element.Element;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementButton;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementNumberInput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;

public class GuiItemRendererSetter extends IWorkspace
{
    public int oriScale;

    public WindowItemRendererSetter windowItemRendererSetter;

    public static ItemTransformVec3f itemTransformVec3f = ItemTransformVec3f.DEFAULT;

    public GuiItemRendererSetter(int scale)
    {
        VARIABLE_LEVEL = 0;

        oriScale = scale;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        levels.clear();

        ArrayList<Window> level = new ArrayList<>();

        windowItemRendererSetter = new WindowItemRendererSetter(this, 0, 0, 160, 110);

        level.add(windowItemRendererSetter);

        levels.add(level);
        levels.add(new ArrayList<>());
    }

    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);

        Minecraft.getMinecraft().gameSettings.guiScale = oriScale;
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public boolean canClickOnElement(Window window, Element element)
    {
        return true;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float renderTick)
    {
        if(mc == null)
        {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        ScaledResolution resolution = new ScaledResolution(mc);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, resolution.getScaledWidth_double(), resolution.getScaledHeight_double(), 0.0D, -5000.0D, 5000.0D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();

        GlStateManager.pushMatrix();

        boolean onWindow = drawWindows(mouseX, mouseY);

        int scroll = Mouse.getDWheel();

        updateElementHovered(mouseX, mouseY, scroll);

        GlStateManager.popMatrix();

        updateKeyStates();

        updateWindowDragged(mouseX, mouseY);

        updateElementDragged(mouseX, mouseY);

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, resolution.getScaledWidth_double(), resolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
    }

    @Override
    public void keyTyped(char c, int key)
    {
        if (key == 1)
        {
            this.mc.displayGuiScreen(null);

            if (this.mc.currentScreen == null)
            {
                this.mc.setIngameFocus();
            }
        }
        else if(elementSelected != null)
        {
            elementSelected.keyInput(c, key);
        }
    }

    public class WindowItemRendererSetter extends Window
    {
        public ElementNumberInput rotation;
        public ElementNumberInput position;
        public ElementNumberInput scale;
        public ElementButton printout;

        public WindowItemRendererSetter(IWorkspace parent, int x, int y, int w, int h)
        {
            super(parent, x, y, w, h, 40, 40, "Item Renderer", true);
            rotation = new ElementNumberInput(this, 10, 20, 140, 12, 0, "Rotation", 3, true, -360D, 360D, itemTransformVec3f.rotation.getX(), itemTransformVec3f.rotation.getY(), itemTransformVec3f.rotation.getZ());
            position = new ElementNumberInput(this, 10, 40, 140, 12, 1, "Position", 3, true, Integer.MIN_VALUE, Integer.MAX_VALUE, itemTransformVec3f.translation.getX(), itemTransformVec3f.translation.getY(), itemTransformVec3f.translation.getZ());
            scale = new ElementNumberInput(this, 10, 60, 140, 12, 2, "Scale", 3, true, Integer.MIN_VALUE, Integer.MAX_VALUE, itemTransformVec3f.scale.getX(), itemTransformVec3f.scale.getY(), itemTransformVec3f.scale.getZ());
            printout = new ElementButton(this, 10, 80, 80, 16, -1, false, 0, 0, "PrintOut");

            elements.add(rotation);
            elements.add(position);
            elements.add(scale);
            elements.add(printout);
        }

        @Override
        public void elementTriggered(Element element)
        {
            super.elementTriggered(element);
            if(element.id == -1)
            {
                try
                {
                    System.out.println(String.format("new ItemTransformVec3f(new Vector3f(%.2fF, %.2fF, %.2fF), new Vector3f(%.2fF, %.2fF, %.2fF), new Vector3f(%.2fF, %.2fF, %.2fF))",
                            Double.parseDouble(rotation.textFields.get(0).getText()),
                            Double.parseDouble(rotation.textFields.get(1).getText()),
                            Double.parseDouble(rotation.textFields.get(2).getText()),
                            Double.parseDouble(position.textFields.get(0).getText()),
                            Double.parseDouble(position.textFields.get(1).getText()),
                            Double.parseDouble(position.textFields.get(2).getText()),
                            Double.parseDouble(scale.textFields.get(0).getText()),
                            Double.parseDouble(scale.textFields.get(1).getText()),
                            Double.parseDouble(scale.textFields.get(2).getText())
                            ));
                }
                catch(Exception ignored){}
            }
        }

        @Override
        public void update()
        {
            super.update();
            try
            {
                ItemTransformVec3f vec = new ItemTransformVec3f(new Vector3f((float)Double.parseDouble(rotation.textFields.get(0).getText()), (float)Double.parseDouble(rotation.textFields.get(1).getText()), (float)Double.parseDouble(rotation.textFields.get(2).getText())), new Vector3f((float)Double.parseDouble(position.textFields.get(0).getText()), (float)Double.parseDouble(position.textFields.get(1).getText()), (float)Double.parseDouble(position.textFields.get(2).getText())), new Vector3f((float)Double.parseDouble(scale.textFields.get(0).getText()), (float)Double.parseDouble(scale.textFields.get(1).getText()), (float)Double.parseDouble(scale.textFields.get(2).getText())));
                itemTransformVec3f = vec;
            }
            catch(Exception ignored){}
        }
    }
}
