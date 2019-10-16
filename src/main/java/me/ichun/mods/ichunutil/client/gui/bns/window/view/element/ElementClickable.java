package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.SoundEvents;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public abstract class ElementClickable<M extends View> extends Element<M> //we reset our focus when we're clicked.
{
    public @Nonnull Consumer<ElementClickable> callback;
    public boolean hover; //for rendering //TODO changeFocus to this? enter key to press
    public String tooltip;

    public ElementClickable(@Nonnull M parent, Consumer<ElementClickable> callback)
    {
        super(parent);
        this.callback = callback;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick)
    {
        hover = isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) //TODO does this mean you can click with RMB
    {
        parentFragment.setFocused(null); //we're a one time click, stop focusing on us
        if(isMouseOver(mouseX, mouseY))
        {
            if(renderMinecraftStyle())
            {
                Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
            onClickRelease();
            callback.accept(this);
        }
        return getFocused() != null && getFocused().mouseReleased(mouseX, mouseY, button);
    }

    public abstract void onClickRelease();

    @Override
    public @Nullable
    String tooltip(double mouseX, double mouseY)
    {
        return tooltip;
    }

    public void setTooltip(String s)
    {
        tooltip = s;
    }

    @Override
    public int getMinecraftFontColour()
    {
        return parentFragment.isDragging() && parentFragment.getFocused() == this ? 10526880 : hover ? 16777120 : 14737632;
    }
}
