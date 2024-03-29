package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.SoundEvents;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.function.Predicate;

public class ElementNumberInput extends ElementTextField
{
    public static final int BUTTON_WIDTH = 10;

    public boolean isDouble;
    public Predicate<String> min = s -> true;
    public Predicate<String> max = s -> true;
    public Predicate<String> maxDec = s -> true;
    public int decimals;
    public Predicate<String> finalValidator = s -> true;

    public boolean clickUp;
    public boolean clickDown;

    public ElementNumberInput(@Nonnull Fragment parent, boolean isDouble)
    {
        super(parent);
        this.isDouble = isDouble;
    }

    public <T extends ElementNumberInput> T setMin(double d)
    {
        min = s -> {
            if(s.isEmpty() || s.equals("-"))
            {
                return true;
            }
            try
            {
                double d1 = Double.parseDouble(s);
                return d1 >= d;
            }
            catch(NumberFormatException e)
            {
                return false;
            }
        };
        return (T)this;
    }

    public <T extends ElementNumberInput> T setMax(double d)
    {
        max = s -> {
            if(s.isEmpty() || s.equals("-"))
            {
                return true;
            }
            try
            {
                double d1 = Double.parseDouble(s);
                return d1 <= d;
            }
            catch(NumberFormatException e)
            {
                return false;
            }
        };
        return (T)this;
    }

    public <T extends ElementNumberInput> T setMaxDec(int i)
    {
        decimals = Math.max(0, i); //keep it above 0
        if(i >= 0)
        {
            maxDec = s -> {
                int dot = s.indexOf(".");
                return dot == -1 || (dot + 1) + i >= s.length();
            };
        }
        return (T)this;
    }

    @Override
    public void init()
    {
        super.init();

        finalValidator = (isDouble ? NUMBERS : INTEGERS).and(min).and(max).and(maxDec);
        widget.setValidator(finalValidator);
    }

    @Override
    public void drawTextBox(MatrixStack stack, int mouseX, int mouseY, float partialTick)
    {
        if(renderMinecraftStyle() > 0)
        {
            widget.setEnableBackgroundDrawing(true);
            widget.render(stack, mouseX, mouseY, partialTick);
            RenderSystem.color4f(1F, 1F, 1F, 1F);
            RenderSystem.enableAlphaTest();

            renderMinecraftStyleButton(stack, getRight() - BUTTON_WIDTH, getTop(), BUTTON_WIDTH, (int)(height / 2d), clickUp ? ButtonState.CLICK : (isMouseBetween(mouseX, getRight() - BUTTON_WIDTH, getRight()) && isMouseBetween(mouseY, getTop(), getTop() + (height / 2D))) ? ButtonState.HOVER : ButtonState.IDLE, renderMinecraftStyle()); //top half
            renderMinecraftStyleButton(stack, getRight() - BUTTON_WIDTH, getTop() + (int)(height / 2d), BUTTON_WIDTH, (int)(height / 2d), clickDown ? ButtonState.CLICK : (isMouseBetween(mouseX, getRight() - BUTTON_WIDTH, getRight()) && isMouseBetween(mouseY, getTop() + (height / 2D), getBottom())) ? ButtonState.HOVER : ButtonState.IDLE, renderMinecraftStyle()); //top half

            int size = 4;
            bindTexture(resourceStatsIcon());
            RenderHelper.draw(stack, getRight() - size - 3, getTop() + ((height / 2d) / 2) - size / 2d, size, size, 0, 40D/128D, 51D/128D, 3D/128D, 14D/128D); //up icon
            RenderHelper.draw(stack, getRight() - size - 3, getTop() + (((height - 0.5D) / 2d) / 2 * 3) - size / 2d, size, size, 0, 22D/128D, 33D/128D, 3D/128D, 14D/128D); //down icon
        }
        else
        {
            int[] colour;
            if(isMouseBetween(mouseX, getLeft(), getLeft() + width - BUTTON_WIDTH) && isMouseBetween(mouseY, getTop(), getTop() + height))
            {
                colour = getTheme().elementInputBackgroundHover;
            }
            else
            {
                colour = getTheme().elementInputBackgroundInactive;
            }
            fill(stack, getTheme().elementInputBorder, 0);
            fill(stack, colour, 1);
            widget.setEnableBackgroundDrawing(false);
            widget.render(stack, mouseX, mouseY, partialTick);
            RenderSystem.color4f(1F, 1F, 1F, 1F);

            //handle top half
            if(clickUp)
            {
                colour = getTheme().elementInputUpDownClick;
            }
            else if(isMouseBetween(mouseX, getRight() - BUTTON_WIDTH, getRight()) && isMouseBetween(mouseY, getTop(), getTop() + (height / 2D)))
            {
                colour = getTheme().elementInputUpDownHover;
            }
            else
            {
                colour = getTheme().elementInputBorder;
            }
            RenderHelper.drawColour(stack, colour[0], colour[1], colour[2], 255, getRight() - BUTTON_WIDTH, getTop(), BUTTON_WIDTH, (height / 2d), 0); //top half


            //handle top half
            if(clickDown)
            {
                colour = getTheme().elementInputUpDownClick;
            }
            else if(isMouseBetween(mouseX, getRight() - BUTTON_WIDTH, getRight()) && isMouseBetween(mouseY, getTop() + (height / 2D), getBottom()))
            {
                colour = getTheme().elementInputUpDownHover;
            }
            else
            {
                colour = getTheme().elementInputBorder;
            }
            RenderHelper.drawColour(stack, colour[0], colour[1], colour[2], 255, getRight() - BUTTON_WIDTH, getTop() + (height / 2d), BUTTON_WIDTH, (height / 2d), 0); //bottom half
            stack.push();
            float scale = 0.5F;
            stack.scale(scale, scale, scale);
            drawString(stack, "\u25B2", (getRight() - BUTTON_WIDTH + 4) / scale, (getTop() + 2.5F + (float)(((height / 2d) / 2) - getFontRenderer().FONT_HEIGHT / 2d)) / scale);
            drawString(stack, "\u25BC", (getRight() - BUTTON_WIDTH + 4) / scale, (getTop() + 2.5F + (float)((((height - 0.5D) / 2d) / 2 * 3) - getFontRenderer().FONT_HEIGHT / 2d)) / scale);
            stack.pop();
        }
    }

    @Override
    public void adjustWidget()
    {
        if(widget != null)
        {
            if(renderMinecraftStyle() > 0) //5 px to draw the button
            {
                widget.x = getLeft() + 1;
                widget.y = getTop() + 1;
                widget.setWidth(this.width - 2 - BUTTON_WIDTH);
                widget.setHeight(this.height - 2);
            }
            else
            {
                widget.x = getLeft() + 5;
                widget.y = getTop() + 1 + ((this.height - getFontRenderer().FONT_HEIGHT) / 2);
                widget.setWidth(this.width - 6 - BUTTON_WIDTH);
                widget.setHeight(this.height - 2);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(isMouseOver(mouseX, mouseY))
        {
            setListener(widget);
            widget.setFocused2(true);
            if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT)
            {
                widget.setText("");
            }
            else if(button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE)
            {
                widget.writeText(Minecraft.getInstance().keyboardListener.getClipboardString());
            }
            widget.mouseClicked(mouseX, mouseY, button);

            if(isMouseBetween(mouseX, getRight() - BUTTON_WIDTH, getRight()) && isMouseBetween(mouseY, getTop(), getTop() + (height / 2D)))
            {
                clickUp = true;
            }
            if(isMouseBetween(mouseX, getRight() - BUTTON_WIDTH, getRight()) && isMouseBetween(mouseY, getTop() + (height / 2D), getBottom()))
            {
                clickDown = true;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if(clickUp)
        {
            clickUp = false;
            if(isMouseBetween(mouseX, getRight() - BUTTON_WIDTH, getRight()) && isMouseBetween(mouseY, getTop(), getTop() + (height / 2D)))
            {
                changeValue(true, Screen.hasShiftDown(), Screen.hasControlDown());
                if(renderMinecraftStyle() > 0)
                {
                    Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                }
            }
        }
        if(clickDown)
        {
            clickDown = false;
            if(isMouseBetween(mouseX, getRight() - BUTTON_WIDTH, getRight()) && isMouseBetween(mouseY, getTop() + (height / 2D), getBottom()))
            {
                changeValue(false, Screen.hasShiftDown(), Screen.hasControlDown());
                if(renderMinecraftStyle() > 0)
                {
                    Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                }
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double dist)
    {
        if(isMouseBetween(mouseX, getLeft(), getLeft() + width - BUTTON_WIDTH) && isMouseBetween(mouseY, getTop(), getTop() + height))
        {
            changeValue(dist > 0, Screen.hasShiftDown(), Screen.hasControlDown());
            return true;
        }
        return false;
    }

    public void changeValue(boolean up, boolean hasShiftDown, boolean hasControlDown)
    {
        String s = widget.getText();
        if(s.isEmpty())
        {
            s = "0";
        }
        double amp = isDouble ? Math.pow(0.1, Math.max(0, decimals - 1)) : 1;
        if(hasShiftDown && hasControlDown)
        {
            amp *= 100D;
        }
        else if(hasShiftDown)
        {
            amp *= 10D;
        }
        else if(hasControlDown)
        {
            amp *= 0.1D;
        }

        if(isDouble)
        {
            try
            {
                double d = Double.parseDouble(s);
                d += (up ? 1 : -1) * amp;
                String newVal = String.format(Locale.ENGLISH, "%." + Integer.toString(decimals) +"f", d);
                if(finalValidator.test(newVal))
                {
                    widget.setText(newVal);
                }
            }
            catch(NumberFormatException ignored){}
        }
        else
        {
            amp = Math.max(1, amp);
            int i = Integer.parseInt(s);
            i += (up ? 1 : -1) * amp;
            String newVal = Integer.toString(i);
            if(finalValidator.test(newVal))
            {
                widget.setText(newVal);
            }
        }
    }

    public int getInt()
    {
        String s = widget.getText();

        if(s.contains("."))
        {
            try
            {
                return (int)Double.parseDouble(s);
            }
            catch(NumberFormatException ignored){}
        }
        else if(!(s.isEmpty() || s.equals("-")))
        {
            try
            {
                return Integer.parseInt(s);
            }
            catch(NumberFormatException ignored){}
        }
        return 0;
    }

    public double getDouble()
    {
        String s = widget.getText();
        if(!(s.isEmpty() || s.equals("-")))
        {
            try
            {
                return Double.parseDouble(s);
            }
            catch(NumberFormatException ignored){}
        }
        return 0D;
    }

    @Override
    public int getMinWidth()
    {
        return 20;
    }
}
