package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.systems.RenderSystem;
import me.ichun.mods.ichunutil.client.gui.bns.Theme;
import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;

import javax.annotation.Nonnull;
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
            if(renderMinecraftStyle())
            {
                getFontRenderer().drawStringWithShadow(s, 0, 0, getMinecraftFontColour());
            }
            else
            {
                getFontRenderer().drawString(s, 0, 0, Theme.getAsHex(toggleState ? getTheme().font : getTheme().fontDim));
            }

            RenderSystem.popMatrix();
        }
    }
}
