package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.systems.RenderSystem;
import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ElementButtonRotatable<T extends ElementButtonRotatable> extends ElementButton<T>
{
    public int rotationCount;

    public ElementButtonRotatable(@Nonnull Fragment parent, @Nonnull String s, int rotCount, Consumer<T> callback)
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
    public void renderText()
    {
        if(!text.isEmpty())
        {
            String s = reString(text, (rotationCount % 2 != 0 ? height : width) - 4);

            RenderSystem.pushMatrix();
            RenderSystem.translatef(getLeft() + (width / 2F), getTop() + (height / 2F), 0F);
            RenderSystem.rotatef(90F * rotationCount, 0F, 0F, 1F);
            RenderSystem.translatef(- getFontRenderer().getStringWidth(s) / 2F,  - (getFontRenderer().FONT_HEIGHT) / 2F + 1, 0F);

            //draw the text
            drawString(s, 0, 0);
            RenderSystem.popMatrix();
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
