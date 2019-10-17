package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.platform.GlStateManager;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;

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

    public ElementNumberInput(@Nonnull View parent, boolean isDouble)
    {
        super(parent);
        this.isDouble = isDouble;
    }

    public ElementNumberInput setMin(double d)
    {
        min = s -> {
            if(s.isEmpty())
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
        return this;
    }

    public ElementNumberInput setMax(double d)
    {
        max = s -> {
            if(s.isEmpty())
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
        return this;
    }

    public ElementNumberInput setMaxDec(int i)
    {
        decimals = Math.max(0, i); //keep it above 0
        if(i >= 0)
        {
            maxDec = s -> {
                int dot = s.indexOf(".");
                return dot == -1 || (dot + 1) + i >= s.length();
            };
        }
        return this;
    }

    @Override
    public void init()
    {
        super.init();

        finalValidator = (isDouble ? NUMBERS : INTEGERS).and(min).and(max).and(maxDec);
        widget.setValidator(finalValidator);
    }

    @Override
    public void drawTextBox(int mouseX, int mouseY, float partialTick)
    {
        if(renderMinecraftStyle())
        {
            widget.setEnableBackgroundDrawing(true);
            widget.render(mouseX, mouseY, partialTick);
            GlStateManager.color4f(1F, 1F, 1F, 1F);

            renderMinecraftStyleButton(getRight() - BUTTON_WIDTH, getTop(), BUTTON_WIDTH, (int)(height / 2d), clickUp ? ButtonState.CLICK : (isMouseBetween(mouseX, getRight() - BUTTON_WIDTH, getRight()) && isMouseBetween(mouseY, getTop(), getTop() + (height / 2D))) ? ButtonState.HOVER : ButtonState.IDLE); //top half
            renderMinecraftStyleButton(getRight() - BUTTON_WIDTH, getTop() + (int)(height / 2d), BUTTON_WIDTH, (int)(height / 2d), clickDown ? ButtonState.CLICK : (isMouseBetween(mouseX, getRight() - BUTTON_WIDTH, getRight()) && isMouseBetween(mouseY, getTop() + (height / 2D), getBottom())) ? ButtonState.HOVER : ButtonState.IDLE); //top half

            int size = 4;
            bindTexture(new ResourceLocation("textures/gui/container/stats_icons.png"));
            RenderHelper.draw(getRight() - size - 3, getTop() + ((height / 2d) / 2) - size / 2d, size, size, 0, 40D/128D, 51D/128D, 3D/128D, 14D/128D); //up icon
            RenderHelper.draw(getRight() - size - 3, getTop() + (((height - 0.5D) / 2d) / 2 * 3) - size / 2d, size, size, 0, 22D/128D, 33D/128D, 3D/128D, 14D/128D); //down icon
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
            fill(getTheme().elementInputBorder, 0);
            fill(colour, 1);
            widget.setEnableBackgroundDrawing(false);
            widget.render(mouseX, mouseY, partialTick);
            GlStateManager.color4f(1F, 1F, 1F, 1F);

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
            RenderHelper.drawColour(colour[0], colour[1], colour[2], 255, getRight() - BUTTON_WIDTH, getTop(), BUTTON_WIDTH, (height / 2d), 0); //top half


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
            RenderHelper.drawColour(colour[0], colour[1], colour[2], 255, getRight() - BUTTON_WIDTH, getTop() + (height / 2d), BUTTON_WIDTH, (height / 2d), 0); //bottom half
            GlStateManager.pushMatrix();
            float scale = 0.5F;
            GlStateManager.scalef(scale, scale, scale);
            drawString("\u25B2", (getRight() - BUTTON_WIDTH + 4) / scale, (getTop() + 2.5F + (float)(((height / 2d) / 2) - getFontRenderer().FONT_HEIGHT / 2d)) / scale);
            drawString("\u25BC", (getRight() - BUTTON_WIDTH + 4) / scale, (getTop() + 2.5F + (float)((((height - 0.5D) / 2d) / 2 * 3) - getFontRenderer().FONT_HEIGHT / 2d)) / scale);
            GlStateManager.popMatrix();

//            parent.workspace.getFontRenderer().drawString("\u25B2", (int)((float)(getPosX() + ((width / textFields.size()) * (i + 1)) - 8) / scale), (int)((float)(getPosY() - 1) / scale), Theme.getAsHex(parent.workspace.currentTheme.font), false);//up
//            parent.workspace.getFontRenderer().drawString("\u25BC", (int)((float)(getPosX() + ((width / textFields.size()) * (i + 1)) - 8) / scale), (int)((float)(getPosY() + 5) / scale), Theme.getAsHex(parent.workspace.currentTheme.font), false);//down
        }
    }

    @Override
    public void adjustWidget()
    {
        if(widget != null)
        {
            if(renderMinecraftStyle()) //5 px to draw the button
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
            setFocused(widget);
            widget.setFocused2(true);
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
                if(renderMinecraftStyle())
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
                if(renderMinecraftStyle())
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
        double amp = Math.pow(0.1, Math.max(0, decimals - 1));
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
            int i = Integer.parseInt(s);
            i += (up ? 1 : -1) * amp;
            String newVal = Integer.toString(i);
            if(finalValidator.test(newVal))
            {
                widget.setText(newVal);
            }
        }
    }

    @Override
    public int getMinWidth()
    {
        return 20;
    }
}
