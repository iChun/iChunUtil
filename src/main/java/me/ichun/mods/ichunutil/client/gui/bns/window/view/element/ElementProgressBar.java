package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import net.minecraft.util.Mth;

import javax.annotation.Nonnull;

public class ElementProgressBar extends Element
{
    public float progress;

    public ElementProgressBar(@Nonnull Fragment parent)
    {
        super(parent);
    }

    public ElementProgressBar setProgress(float prog)
    {
        progress = Mth.clamp(prog, 0F, 1F);
        return this;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTick)
    {
        if(renderMinecraftStyle() > 0)
        {
            bindTexture(resourceHorse());

            cropAndStitch(stack, getLeft(), getTop(), width, height, 2, 43, 141, 18, 18, 256, 256); //taken from ElementList$Item
            cropAndStitch(stack, getLeft(), getTop(), (int)Math.floor(width * progress), height, 2, 79, 17, 90, 54, 256, 256);
        }
        else
        {
            int[] borderColour = getTheme().elementTreeItemBorder;

            fill(stack, borderColour, 0);
            fill(stack, getTheme().elementTreeItemBg, 1);

            int oriWidth = width;
            width = (int)Math.floor(width * progress);
            fill(stack, getTheme().elementTreeItemBgSelect, 1);
            width = oriWidth;
        }
    }

    @Override
    public int getMinHeight()
    {
        return 10;
    }
}
