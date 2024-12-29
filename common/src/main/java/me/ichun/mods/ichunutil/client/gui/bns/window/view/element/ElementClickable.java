package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.Fragment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("unchecked")
public abstract class ElementClickable<T extends ElementClickable<T>> extends Element<Fragment<?>> //we reset our focus when we're clicked.
{
    public @NotNull TriConsumer<T, Double, Double> callback;
    public @Nullable TriConsumer<T, Double, Double> rightClickCallback;
    public boolean hover; //for rendering
    public boolean disabled;

    public ElementClickable(@NotNull Fragment<?> parent, @NotNull TriConsumer<T, Double, Double> callback, @Nullable TriConsumer<T, Double, Double> rightClickCallback)
    {
        super(parent);
        this.callback = callback;
        this.rightClickCallback = rightClickCallback;
    }

    public ElementClickable(@NotNull Fragment<?> parent, @NotNull TriConsumer<T, Double, Double> callback)
    {
        this(parent, callback, null);
    }

    public T setDisabled(boolean flag)
    {
        disabled = flag;
        return (T)this;
    }

    public T setRightClickCallback(TriConsumer<T, Double, Double> rightClickCallback)
    {
        this.rightClickCallback = rightClickCallback;
        return (T)this;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        hover = false;
        if(!disabled)
        {
            hover = isMouseOver(mouseX, mouseY) || parent.getFocused() == this;
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        boolean flag = super.mouseReleased(mouseX, mouseY, button); // unsets dragging;
        parent.setFocused(null); //we're a one time click, stop focusing on us
        if(!disabled && isMouseOver(mouseX, mouseY))
        {
            if(button == 0) // lmb
            {
                trigger(mouseX, mouseY);
            }
            else if(rightClickCallback != null && button == 1)
            {
                triggerRMB(mouseX, mouseY);
            }
        }
        return flag;
    }

    public void trigger(double mouseX, double mouseY)
    {
        if(renderMinecraftStyle() > 0)
        {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        onClickRelease();
        callback.accept((T)this, mouseX, mouseY);
    }

    public void triggerRMB(double mouseX, double mouseY)
    {
        if(renderMinecraftStyle() > 0)
        {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        onRightClickRelease();
        rightClickCallback.accept((T)this, mouseX, mouseY);
    }

    public abstract void onClickRelease();

    public void onRightClickRelease() {}

    @Override
    public boolean keyPressed(int key, int scancode, int listener)
    {
        if(!disabled && (key == GLFW.GLFW_KEY_SPACE || key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER))
        {
            if(Screen.hasControlDown())
            {
                triggerRMB(getCenterX(), getCenterY());
            }
            else
            {
                trigger(getCenterX(), getCenterY());
            }
            return true;
        }
        return false;
    }

    @Override
    public int getMinecraftFontColour()
    {
        return parent.isDragging() && parent.getFocused() == this || disabled ? 10526880 : hover ? 16777120 : 14737632;
    }
}
