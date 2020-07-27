package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import me.ichun.mods.ichunutil.client.gui.bns.window.Fragment;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class ElementTexture extends Element
{
    public ResourceLocation textureLocation;
    public boolean warping;

    public ElementTexture(@Nonnull Fragment parent, ResourceLocation rl)
    {
        super(parent);
        this.textureLocation = rl;
    }

    public ElementTexture setWarping()
    {
        warping = true;
        return this;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTick)
    {
        RenderSystem.enableAlphaTest();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture(textureLocation);

        if(warping)
        {
            RenderHelper.draw(stack, getLeft() + 2, getTop() + 2, width - 4, height - 4, 0);
        }
        else
        {
            int length = Math.min(width, height) - 4;
            int x = (int)(getLeft() + (width / 2D) - (length / 2D));
            int y = (int)(getTop() + (height / 2D) - (length / 2D));
            RenderHelper.draw(stack, x , y, length, length, 0);
        }
    }
}
