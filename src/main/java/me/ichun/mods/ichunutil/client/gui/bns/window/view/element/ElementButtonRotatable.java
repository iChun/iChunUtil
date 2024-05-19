package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import me.ichun.mods.ichunutil.client.gui.bns.Fragment;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ElementButtonRotatable<T extends ElementButtonRotatable> extends ElementButton<T>
{
    public int rotationCount;

    public ElementButtonRotatable(@NotNull Fragment parent, @NotNull String s, int rotCount, Consumer<T> callback)
    {
        super(parent, s, callback);
        this.rotationCount = rotCount;
    }

    @Override
    public ElementButtonRotatable<?> setSize(int width, int height)
    {
        if(rotationCount % 2 != 0)
        {
            return super.setSize(height, width); //flip them
        }
        return super.setSize(width, height);
    }

    @Override
    public void renderText(GuiGraphics graphics)
    {
        if(!text.isEmpty())
        {
            String s = reString(text, (rotationCount % 2 != 0 ? height : width) - 4);

            PoseStack stack = graphics.pose();
            stack.pushPose();
            stack.translate(getLeft() + (width / 2F), getTop() + (height / 2F), 0F);
            stack.mulPose(Axis.ZP.rotationDegrees(90F * rotationCount));
            stack.translate(- getFontRenderer().width(s) / 2F,  - (getFontRenderer().lineHeight) / 2F + 1, 0F);

            //draw the text
            drawString(graphics, s, 0, 0);
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
