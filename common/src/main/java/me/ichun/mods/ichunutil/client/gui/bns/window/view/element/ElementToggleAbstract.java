package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.client.gui.bns.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.Theme;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public class ElementToggleAbstract<T extends ElementToggleAbstract<T>> extends ElementClickable<T>
{
    public String text;
    public boolean toggleState;

    public ElementToggleAbstract(@NotNull Fragment<?> parent, @NotNull String s, TriConsumer<T, Double, Double> callback, TriConsumer<T, Double, Double> rightMouseCallback)
    {
        super(parent, callback, rightMouseCallback);
        text = !s.isEmpty() ? I18n.get(s) : "";
    }

    public ElementToggleAbstract(@NotNull Fragment<?> parent, @NotNull String s, TriConsumer<T, Double, Double> callback)
    {
        this(parent, s, callback, null);
    }

    public T setToggled(boolean flag)
    {
        toggleState = flag;
        return (T)this;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        super.render(graphics, mouseX, mouseY, partialTick);
        PoseStack stack = graphics.pose();
        if(renderMinecraftStyle() > 0)
        {
            renderMinecraftStyleButton(stack, getLeft(), getTop(), width, height, disabled || parent.isDragging() && parent.getFocused() == this || toggleState ? ButtonState.CLICK : hover ? ButtonState.HOVER : ButtonState.IDLE);
        }
        else
        {
            fill(graphics, getTheme().elementButtonBorder, 0);
            int[] colour;
            if(disabled)
            {
                colour = getTheme().elementButtonBackgroundInactive;
            }
            else if(parent.isDragging() && parent.getFocused() == this)
            {
                colour = getTheme().elementButtonClick;
            }
            else if(toggleState && hover)
            {
                colour = getTheme().elementButtonToggleHover;
            }
            else if(hover)
            {
                colour = getTheme().elementButtonBackgroundHover;
            }
            else if(toggleState)
            {
                colour = getTheme().elementButtonToggle;
            }
            else
            {
                colour = getTheme().elementButtonBackgroundActive;
            }
            fill(graphics, colour, 1);
        }
        renderText(graphics);
    }

    public void renderText(GuiGraphics graphics)
    {
        if(!text.isEmpty())
        {
            String s = reString(text, width - 4);

            //draw the text
            graphics.drawString(getFontRenderer(), s, (int)(getLeft() + (this.width - getFontRenderer().width(s)) / 2F), (int)(getTop() + (height - getFontRenderer().lineHeight) / 2F + 1), (renderMinecraftStyle() > 0 ? getMinecraftFontColour() : Theme.getAsHex(getTheme().font)), renderMinecraftStyle() > 0);
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
    public void onClickRelease()
    {
        toggleState = !toggleState;
    }

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
