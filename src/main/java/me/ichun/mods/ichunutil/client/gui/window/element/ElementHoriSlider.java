package me.ichun.mods.ichunutil.client.gui.window.element;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Mouse;
import me.ichun.mods.ichunutil.client.gui.Theme;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.render.RendererHelper;

public class ElementHoriSlider extends Element
{
    public double prevSliderProg;
    public double sliderProg;
    public int spacerL;
    public int spacerR;
    public String tooltip;
    public String label;

    public ElementHoriSlider(Window window, int x, int y, int w, int ID, boolean igMin, String title)
    {
        this(window, x, y, w, ID, igMin, title, "");
    }

    public ElementHoriSlider(Window window, int x, int y, int w, int ID, boolean igMin, String title, String lab)
    {
        super(window, x, y, w, 12, ID, igMin);

        sliderProg = 1.0D;
        spacerL = x;
        spacerR = parent.width - x - width;
        tooltip = title;
        label = lab;
    }

    @Override
    public void draw(int mouseX, int mouseY, boolean hover)
    {
        double x1 = getPosX() + 4;
        double x2 = x1 + width - 8;
        double y1 = getPosY() + 5;
        double y2 = y1 + height - 10;
        RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementTreeBorder[0], parent.workspace.currentTheme.elementTreeBorder[1], parent.workspace.currentTheme.elementTreeBorder[2], 255, x1, y1, (x2 - x1), (y2 - y1), 0);

        RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementTreeBorder[0], parent.workspace.currentTheme.elementTreeBorder[1], parent.workspace.currentTheme.elementTreeBorder[2], 255, getPosX() + ((x2 - parent.workspace.getFontRenderer().getStringWidth(label)) - x1) * sliderProg , getPosY(), (8 + parent.workspace.getFontRenderer().getStringWidth(label)), height, 0);
        RendererHelper.drawColourOnScreen(parent.workspace.currentTheme.elementTreeScrollBar[0], parent.workspace.currentTheme.elementTreeScrollBar[1], parent.workspace.currentTheme.elementTreeScrollBar[2], 255, getPosX() + ((x2 - parent.workspace.getFontRenderer().getStringWidth(label)) - x1) * sliderProg + 1, getPosY() + 1, (6 + parent.workspace.getFontRenderer().getStringWidth(label)), height - 2, 0);

        if(!label.isEmpty())
        {
            parent.workspace.getFontRenderer().drawString(StatCollector.translateToLocal(label), (float)(getPosX() + ((x2 - parent.workspace.getFontRenderer().getStringWidth(label)) - x1) * sliderProg) + 4F, getPosY() + height - (height / 2) - (parent.workspace.getFontRenderer().FONT_HEIGHT / 2), Theme.getAsHex(parent.workspace.currentTheme.font), false);
        }

        if(parent.workspace.elementDragged == this && Mouse.isButtonDown(0) && mouseX >= posX && mouseX <= posX + width && mouseY >= posY && mouseY <= posY + height)
        {
            double sx1 = posX + 4;
            double sx2 = posX + width - 8;
            sliderProg = MathHelper.clamp_double((double)(mouseX - sx1) / (double)(sx2 - sx1), 0.0D, 1.0D);

            if(sliderProg != prevSliderProg)
            {
                parent.elementTriggered(this);
            }
        }
        prevSliderProg = sliderProg;
    }

    @Override
    public boolean mouseScroll(int mouseX, int mouseY, int k)
    {
        sliderProg = MathHelper.clamp_double(sliderProg + (GuiScreen.isShiftKeyDown() ? k * 10 : k) * 0.001D, 0.0D, 1.0D);
        if(sliderProg != prevSliderProg)
        {
            parent.elementTriggered(this);
        }
        return true;
    }

    @Override
    public boolean onClick(int mouseX, int mouseY, int id)
    {
        return id == 0;//return true for elements that has input eg typing
    }

    @Override
    public void resized()
    {
        posX = spacerL;
        width = parent.width - posX - spacerR;
    }

    @Override
    public String tooltip()
    {
        return tooltip; //return null for no tooltip. This is localized.
    }
}
