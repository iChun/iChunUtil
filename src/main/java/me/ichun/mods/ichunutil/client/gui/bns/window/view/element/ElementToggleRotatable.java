package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.gui.bns.Theme;
import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ElementToggleRotatable<T extends ElementToggleRotatable> extends ElementToggle<T>
{
    public int rotationCount;

    public ElementToggleRotatable(@Nonnull Fragment parent, @Nonnull String s, int rotCount, Consumer<T> callback)
    {
        super(parent, s, callback);
        this.rotationCount = rotCount;
    }

    @Override
    public Element<?> setSize(int width, int height)
    {
        if(rotationCount % 2 != 0)
        {
            return super.setSize(height, width); //flip them
        }
        return super.setSize(width, height);
    }

    @Override
    public void renderText(MatrixStack stack)
    {
        if(!text.isEmpty())
        {
            String s = reString(text, (rotationCount % 2 != 0 ? height : width) - 4);

            stack.push();
            stack.translate(getLeft() + (width / 2F), getTop() + (height / 2F), 0F);
            stack.rotate(Vector3f.ZP.rotationDegrees(90F * rotationCount));
            stack.translate(- getFontRenderer().getStringWidth(s) / 2F,  - (getFontRenderer().FONT_HEIGHT) / 2F + 1, 0F);

            //draw the text
            if(renderMinecraftStyle() > 0)
            {
                getFontRenderer().drawStringWithShadow(stack, s, 0, 0, getMinecraftFontColour());
            }
            else
            {
                getFontRenderer().drawString(stack, s, 0, 0, Theme.getAsHex(toggleState ? getTheme().font : getTheme().fontDim));
            }

            stack.pop();
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
