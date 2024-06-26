package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.client.gui.bns.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.TextureDefinition;
import me.ichun.mods.ichunutil.client.gui.bns.window.WindowContextMenu;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ElementDropdownContextMenu<T extends ElementDropdownContextMenu> extends ElementClickable<T>
        implements WindowContextMenu.IContextMenu
{
    public @NotNull String text;
    public final @NotNull List<?> contextMenuObjects;
    public final @NotNull BiConsumer<WindowContextMenu.IContextMenu, ElementList.Item<?>> contextMenuReceiver;
    public @NotNull Function<Object, String> nameProvider = Object::toString;

    public ElementDropdownContextMenu(@NotNull Fragment parent, String text, @NotNull List<?> contextMenuObjects, @NotNull BiConsumer<WindowContextMenu.IContextMenu, ElementList.Item<?>> contextMenuReceiver)
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
    public void render(PoseStack graphics, int mouseX, int mouseY, float partialTick)
    {
        super.render(graphics, mouseX, mouseY, partialTick);
        PoseStack stack = graphics;
        if(renderMinecraftStyle() > 0)
        {
            RenderHelper.drawColour(graphics, -6250336, 255, getLeft(), getTop(), width - ElementNumberInput.BUTTON_WIDTH, height, 0);
            RenderHelper.drawColour(graphics, -16777216, 255, getLeft() + 1, getTop() + 1, width - 2 - ElementNumberInput.BUTTON_WIDTH, height - 2, 0);

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);


            renderMinecraftStyleButton(stack, getRight() - ElementNumberInput.BUTTON_WIDTH, getTop(), ElementNumberInput.BUTTON_WIDTH, (int)(height), disabled || parent.isDragging() && parent.getFocused() == this ? ButtonState.CLICK : hover ? ButtonState.HOVER : ButtonState.IDLE);

            bindTexture(resourceDown());
            double[] coords = TEXDEF_DOWN.getCoords(TextureDefinition.DrawType.FILL);
            int size = 4;
            RenderHelper.draw(stack, getRight() - size - 3, getTop() + (height / 2d) - size / 2d, size, size, 0, coords[0], coords[1], coords[2], coords[3]); //down icon
        }
        else
        {
            fill(graphics, getTheme().elementButtonBorder, 0);
            int[] colour = disabled ? getTheme().elementButtonBackgroundInactive : parent.isDragging() && parent.getFocused() == this ? getTheme().elementButtonClick : hover ? getTheme().elementButtonBackgroundHover : getTheme().elementButtonBackgroundActive;
            fill(graphics, colour, 1);

            if(parent.isDragging() && parent.getFocused() == this)
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
            RenderHelper.drawColour(graphics, colour[0], colour[1], colour[2], 255, getRight() - ElementNumberInput.BUTTON_WIDTH, getTop(), ElementNumberInput.BUTTON_WIDTH, height, 0);
            stack.pushPose();
            float scale = 0.5F;
            stack.scale(scale, scale, scale);
            drawString(graphics, "\u25BC", (getRight() - ElementNumberInput.BUTTON_WIDTH + 4) / scale, (getTop() + 2.5F + (float)((height / 2d) - getFontRenderer().lineHeight / 2d)) / scale);
            stack.popPose();
        }
        if(!text.isEmpty())
        {
            String s = reString(text, width - 16);
            if(s.equals(text))
            {
                setTooltip(null);
            }
            else
            {
                setTooltip(text);
            }
            drawString(graphics, s, getLeft() + 5, getTop() + (height - getFontRenderer().lineHeight) / 2F + 1);
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

    @NotNull
    @Override
    public List<?> getObjects()
    {
        return contextMenuObjects;
    }

    @NotNull
    @Override
    public BiConsumer<WindowContextMenu.IContextMenu, ElementList.Item<?>> getReceiver()
    {
        return contextMenuReceiver;
    }

    @NotNull
    @Override
    public Function<Object, String> getNameProvider()
    {
        return nameProvider;
    }
}
