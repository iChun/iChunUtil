package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.window.WindowContextMenu;
import me.ichun.mods.ichunutil.client.render.RenderHelper;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ElementDropdownContextMenu<T extends ElementDropdownContextMenu> extends ElementClickable<T>
        implements WindowContextMenu.IContextMenu
{
    public @Nonnull String text;
    public final @Nonnull List<?> contextMenuObjects;
    public final @Nonnull BiConsumer<WindowContextMenu.IContextMenu, ElementList.Item<?>> contextMenuReceiver;
    public @Nonnull Function<Object, String> nameProvider = Object::toString;

    public ElementDropdownContextMenu(@Nonnull Fragment parent, String text, @Nonnull List<?> contextMenuObjects, @Nonnull BiConsumer<WindowContextMenu.IContextMenu, ElementList.Item<?>> contextMenuReceiver)
    {
        super(parent, e -> {});
        this.text = text;
        this.contextMenuObjects = contextMenuObjects;
        this.contextMenuReceiver = contextMenuReceiver;
    }

    public ElementDropdownContextMenu<T> setNameProvider(Function<Object, String> nameProvider)
    {
        this.nameProvider = nameProvider;
        return this;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTick)
    {
        super.render(stack, mouseX, mouseY, partialTick);
        if(renderMinecraftStyle() > 0)
        {
            RenderHelper.drawColour(stack, -6250336, 255, getLeft(), getTop(), width - ElementNumberInput.BUTTON_WIDTH, height, 0);
            RenderHelper.drawColour(stack, -16777216, 255, getLeft() + 1, getTop() + 1, width - 2 - ElementNumberInput.BUTTON_WIDTH, height - 2, 0);

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

            renderMinecraftStyleButton(stack, getRight() - ElementNumberInput.BUTTON_WIDTH, getTop(), ElementNumberInput.BUTTON_WIDTH, (int)(height), disabled || parentFragment.isDragging() && parentFragment.getFocused() == this ? ButtonState.CLICK : hover ? ButtonState.HOVER : ButtonState.IDLE, renderMinecraftStyle());

            bindTexture(resourceStatsIcon());
            int size = 4;
            RenderHelper.draw(stack, getRight() - size - 3, getTop() + (height / 2d) - size / 2d, size, size, 0, 22D/128D, 33D/128D, 3D/128D, 14D/128D); //down icon
        }
        else
        {
            fill(stack, getTheme().elementButtonBorder, 0);
            int[] colour = disabled ? getTheme().elementButtonBackgroundInactive : parentFragment.isDragging() && parentFragment.getFocused() == this ? getTheme().elementButtonClick : hover ? getTheme().elementButtonBackgroundHover : getTheme().elementButtonBackgroundInactive;
            fill(stack, colour, 1);

            if(parentFragment.isDragging() && parentFragment.getFocused() == this)
            {
                colour = getTheme().elementInputUpDownClick;
            }
            else if(hover)
            {
                colour = getTheme().elementInputUpDownHover;
            }
            else
            {
                colour = getTheme().elementInputBorder;
            }
            RenderHelper.drawColour(stack, colour[0], colour[1], colour[2], 255, getRight() - ElementNumberInput.BUTTON_WIDTH, getTop(), ElementNumberInput.BUTTON_WIDTH, height, 0);
            stack.pushPose();
            float scale = 0.5F;
            stack.scale(scale, scale, scale);
            drawString(stack, "\u25BC", (getRight() - ElementNumberInput.BUTTON_WIDTH + 4) / scale, (getTop() + 2.5F + (float)((height / 2d) - getFontRenderer().lineHeight / 2d)) / scale);
            stack.popPose();
        }
        if(!text.isEmpty())
        {
            String s = reString(text, width - 4);
            if(s.equals(text))
            {
                setTooltip(null);
            }
            else
            {
                setTooltip(text);
            }
            drawString(stack, s, getLeft() + 5, getTop() + (height - getFontRenderer().lineHeight) / 2F + 1);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        return super.mouseReleased(mouseX, mouseY, button == 1 ? 0 : button);
    }

    @Override
    public void onClickRelease()
    {
        WindowContextMenu.create(getWorkspace(), this, getLeft(), getBottom() - 1, width, height - 2);
    }

    @Nonnull
    @Override
    public List<?> getObjects()
    {
        return contextMenuObjects;
    }

    @Nonnull
    @Override
    public BiConsumer<WindowContextMenu.IContextMenu, ElementList.Item<?>> getReceiver()
    {
        return contextMenuReceiver;
    }

    @Nonnull
    @Override
    public Function<Object, String> getNameProvider()
    {
        return nameProvider;
    }
}
