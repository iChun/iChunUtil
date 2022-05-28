package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import net.minecraft.client.resources.language.I18n;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ElementButton<T extends ElementButton> extends ElementClickable<T>
{
    public @Nonnull String text;
    public boolean renderBackground = true;

    public ElementButton(@Nonnull Fragment parent, String s, Consumer<T> callback)
    {
        super(parent, callback);
        text = I18n.get(s);
    }

    public ElementButton<T> disableBackground()
    {
        renderBackground = false;
        return this;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTick)
    {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        super.render(stack, mouseX, mouseY, partialTick);
        if(renderBackground)
        {
            if(renderMinecraftStyle() > 0)
            {
                renderMinecraftStyleButton(stack, getLeft(), getTop(), width, height, disabled || parentFragment.isDragging() && parentFragment.getFocused() == this ? ButtonState.CLICK : hover ? ButtonState.HOVER : ButtonState.IDLE, renderMinecraftStyle());
            }
            else
            {
                fill(stack, getTheme().elementButtonBorder, 0);
                int[] colour = disabled ? getTheme().elementButtonBackgroundInactive : parentFragment.isDragging() && parentFragment.getFocused() == this ? getTheme().elementButtonClick : hover ? getTheme().elementButtonBackgroundHover : getTheme().elementButtonBackgroundInactive;
                fill(stack, colour, 1);
            }
        }
        renderText(stack);
    }

    public void renderText(PoseStack stack)
    {
        if(!text.isEmpty())
        {
            String s = reString(text, width - 4);
            drawString(stack, s, getLeft() + (this.width - getFontRenderer().width(s)) / 2F, getTop() + (height - getFontRenderer().lineHeight) / 2F + 1);
        }
    }

    @Nullable
    @Override
    public String tooltip(double mouseX, double mouseY)
    {
        if(!text.isEmpty())
        {
            String s = reString(text, width - 4);
            if(!s.equals(text))
            {
                String tooltip = super.tooltip(mouseX, mouseY);
                if(tooltip != null)
                {
                    return text + " - " + tooltip;
                }
                return text;
            }
        }
        return super.tooltip(mouseX, mouseY);
    }

    @Override
    public void onClickRelease() {} //we don't do anything, we're a static button

    @Override
    public int getMinWidth()
    {
        return 14;
    }

    @Override
    public int getMinHeight()
    {
        return 14;
    }
}
