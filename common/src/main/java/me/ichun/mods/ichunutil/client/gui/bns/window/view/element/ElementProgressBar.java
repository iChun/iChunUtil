package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.client.gui.bns.Fragment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class ElementProgressBar extends Element
{
    public float progress;

    public ElementProgressBar(@NotNull Fragment parent)
    {
        super(parent);
    }

    public ElementProgressBar setProgress(float prog)
    {
        progress = Mth.clamp(prog, 0F, 1F);
        return this;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        PoseStack stack = graphics.pose();
        if(renderMinecraftStyle() > 0)
        {
            cropAndStitch(resourceHorse(), stack, getLeft(), getTop(), width, height, 2, 43, 141, 18, 18, 256, 256); //taken from ElementList$Item
            cropAndStitch(resourceHorse(), stack, getLeft(), getTop(), (int)Math.floor(width * progress), height, 2, 79, 17, 90, 54, 256, 256);
        }
        else
        {
            int[] borderColour = getTheme().elementListItemBorder;

            fill(graphics, borderColour, 0);
            fill(graphics, getTheme().elementListItemBackground, 1);

            int oriWidth = width;
            width = (int)Math.floor(width * progress);
            fill(graphics, getTheme().elementListItemBackgroundSelect, 1);
            width = oriWidth;
        }
    }

    @Override
    public int getMinHeight()
    {
        return 10;
    }
}
