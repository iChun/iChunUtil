package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.SoundEvents;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public abstract class ElementRightClickable<T extends ElementRightClickable> extends ElementClickable<T> //we reset our focus when we're clicked.
{
    public @Nonnull Consumer<T> rightClickCallback;

    public ElementRightClickable(@Nonnull Fragment parent, Consumer<T> callback, Consumer<T> rightClickCallback)
    {
        super(parent, callback);
        this.rightClickCallback = rightClickCallback;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
//        boolean flag = super.mouseReleased(mouseX, mouseY, button); // unsets dragging;
        //copied out mouseReleased so we don't call ElementClickable's
        this.setDragging(false);
        boolean flag = getListener() != null && getListener().mouseReleased(mouseX, mouseY, button);

        parentFragment.setListener(null); //we're a one time click, stop focusing on us
        if(!disabled && isMouseOver(mouseX, mouseY))
        {
            if(button == 0)
            {
                trigger();
            }
            else if(button == 1)
            {
                triggerRMB();
            }
        }
        return flag;
    }

    public void triggerRMB()
    {
        if(renderMinecraftStyle() > 0)
        {
            Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
        onRightClickRelease();
        rightClickCallback.accept((T)this);
    }

    public abstract void onRightClickRelease();

    @Override
    public boolean keyPressed(int key, int scancode, int listener)
    {
        if(!disabled && Screen.hasControlDown() && (key == GLFW.GLFW_KEY_SPACE || key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER))
        {
            triggerRMB();
            return true;
        }
        return super.keyPressed(key, scancode, listener);
    }
}
