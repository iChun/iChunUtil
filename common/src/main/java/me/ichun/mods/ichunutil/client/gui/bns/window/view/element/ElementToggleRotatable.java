package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import me.ichun.mods.ichunutil.client.gui.bns.Fragment;
import me.ichun.mods.ichunutil.client.gui.bns.Theme;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ElementToggleRotatable<T extends ElementToggleRotatable> extends ElementToggle<T>
{
    public int rotationCount;

    public ElementToggleRotatable(@NotNull Fragment parent, @NotNull String s, int rotCount, Consumer<T> callback)
    {
        super(parent, s, callback);
        this.rotationCount = rotCount;
    }

    @Override
    public ElementToggleRotatable<?> setSize(int width, int height)
    {
        if(rotationCount % 2 != 0)
        {
            return super.setSize(height, width); //flip them
        }
        return super.setSize(width, height);
    }

    @Override
    public void renderText(PoseStack graphics)
    {
        if(!text.isEmpty())
        {
            String s = reString(text, (rotationCount % 2 != 0 ? height : width) - 4);

            PoseStack stack = graphics;
            stack.pushPose();
            stack.translate(getLeft() + (width / 2F), getTop() + (height / 2F), 0F);
            stack.mulPose(Axis.ZP.rotationDegrees(90F * rotationCount));
            stack.translate(- getFontRenderer().width(s) / 2F,  - (getFontRenderer().lineHeight) / 2F + 1, 0F);

            //draw the text
            if(renderMinecraftStyle() > 0)
            {
                getFontRenderer().drawShadow(stack, s, 0, 0, getMinecraftFontColour());
            }
            else
            {
                getFontRenderer().draw(stack, s, 0, 0, Theme.getAsHex(toggleState ? getTheme().font : getTheme().fontDim));
            }

            stack.popPose();
        }
    }

    @Nullable
    @Override
    public String tooltip(double mouseX, double mouseY)
    {
        if(!text.isEmpty())
        {
            String s = reString(text, (rotationCount % 2 != 0 ? height : width) - 4);
            if(!s.equals(text))
            {
                if(tooltip != null)
                {
                    return text + " - " + tooltip;
                }
                return text;
            }
        }
        return tooltip;
    }
}
