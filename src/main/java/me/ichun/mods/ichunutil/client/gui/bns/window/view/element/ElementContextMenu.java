package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.systems.RenderSystem;
import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.window.WindowContextMenu;
import me.ichun.mods.ichunutil.client.render.RenderHelper;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ElementContextMenu<P extends Fragment, T extends ElementContextMenu> extends ElementClickable<P, T>
        implements WindowContextMenu.IContextMenu
{
    public @Nonnull String text;
    public final @Nonnull List<Object> contextMenuObjects;
    public final @Nonnull BiConsumer<WindowContextMenu.IContextMenu, ElementList.Item<?>> contextMenuReceiver;

    public Function<Object, String> nameProvider = Object::toString;

    public ElementContextMenu(@Nonnull P parent, String text, @Nonnull List<Object> contextMenuObjects, @Nonnull BiConsumer<WindowContextMenu.IContextMenu, ElementList.Item<?>> contextMenuReceiver)
    {
        super(parent, e -> {});
        this.text = text;
        this.contextMenuObjects = contextMenuObjects;
        this.contextMenuReceiver = contextMenuReceiver;
    }

    public ElementContextMenu<P, T> setNameProvider(Function<Object, String> nameProvider)
    {
        this.nameProvider = nameProvider;
        return this;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick)
    {
        super.render(mouseX, mouseY, partialTick);
        if(renderMinecraftStyle())
        {
            RenderHelper.drawColour(-6250336, 255, getLeft(), getTop(), width - ElementNumberInput.BUTTON_WIDTH, height, 0);
            RenderHelper.drawColour(-16777216, 255, getLeft() + 1, getTop() + 1, width - 2 - ElementNumberInput.BUTTON_WIDTH, height - 2, 0);

            RenderSystem.color4f(1F, 1F, 1F, 1F);
            RenderSystem.enableAlphaTest();

            renderMinecraftStyleButton(getRight() - ElementNumberInput.BUTTON_WIDTH, getTop(), ElementNumberInput.BUTTON_WIDTH, (int)(height), parentFragment.isDragging() && parentFragment.getFocused() == this ? ButtonState.CLICK : hover ? ButtonState.HOVER : ButtonState.IDLE);

            bindTexture(Fragment.VANILLA_STATS_ICON);
            int size = 4;
            RenderHelper.draw(getRight() - size - 3, getTop() + (height / 2d) - size / 2d, size, size, 0, 22D/128D, 33D/128D, 3D/128D, 14D/128D); //down icon
        }
        else
        {
            fill(getTheme().elementButtonBorder, 0);
            int[] colour = parentFragment.isDragging() && parentFragment.getFocused() == this ? getTheme().elementButtonClick : hover ? getTheme().elementButtonBackgroundHover : getTheme().elementButtonBackgroundInactive;
            fill(colour, 1);

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
            RenderHelper.drawColour(colour[0], colour[1], colour[2], 255, getRight() - ElementNumberInput.BUTTON_WIDTH, getTop(), ElementNumberInput.BUTTON_WIDTH, height, 0);
            RenderSystem.pushMatrix();
            float scale = 0.5F;
            RenderSystem.scalef(scale, scale, scale);
            drawString("\u25BC", (getRight() - ElementNumberInput.BUTTON_WIDTH + 4) / scale, (getTop() + 2.5F + (float)((height / 2d) - getFontRenderer().FONT_HEIGHT / 2d)) / scale);
            RenderSystem.popMatrix();
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
            drawString(s, getLeft() + 5, getTop() + (height - getFontRenderer().FONT_HEIGHT) / 2F + 1);
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
        WindowContextMenu.create(getWorkspace(), this, getRight(), getBottom() - 1, width, height - 2);
    }

    @Nonnull
    @Override
    public List<Object> getObjects()
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
