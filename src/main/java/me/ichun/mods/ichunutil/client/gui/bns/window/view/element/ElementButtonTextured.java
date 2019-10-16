package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.platform.GlStateManager;
import me.ichun.mods.ichunutil.client.gui.bns.window.view.View;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class ElementButtonTextured extends ElementButtonTooltip
{
    public ResourceLocation textureLocation;
    public boolean warping;

    public ElementButtonTextured(@Nonnull View parent, String tooltip, ResourceLocation rl)
    {
        super(parent, "", tooltip);
        this.textureLocation = rl;
    }

    public ElementButtonTextured setWarping()
    {
        warping = true;
        return this;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick)
    {
        super.render(mouseX, mouseY, partialTick);

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture(textureLocation);

        if(warping)
        {
            RenderHelper.draw(getLeft() + 2, getTop() + 2, width - 4, height - 4, 0);
        }
        else
        {
            int length = Math.min(width, height) - 4;
            int x = (int)(getLeft() + (width / 2D) - (length / 2D));
            int y = (int)(getTop() + (height / 2D) - (length / 2D));
            RenderHelper.draw(x , y, length, length, 0);
        }
    }
}
