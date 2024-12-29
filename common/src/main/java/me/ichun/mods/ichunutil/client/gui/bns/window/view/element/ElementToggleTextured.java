package me.ichun.mods.ichunutil.client.gui.bns.window.view.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.client.gui.bns.Fragment;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.NotNull;

public class ElementToggleTextured extends ElementToggleAbstract<ElementToggleTextured>
{
    public ResourceLocation textureLocation;
    public boolean warping;

    public ElementToggleTextured(@NotNull Fragment<?> parent, @NotNull String tooltip, ResourceLocation rl, TriConsumer<ElementToggleTextured, Double, Double> callback, TriConsumer<ElementToggleTextured, Double, Double> rightMouseCallback)
    {
        super(parent, "", callback, rightMouseCallback);
        this.tooltip = tooltip;
        this.textureLocation = rl;
    }

    public ElementToggleTextured(@NotNull Fragment<?> parent, @NotNull String tooltip, ResourceLocation rl, TriConsumer<ElementToggleTextured, Double, Double> callback)
    {
        this(parent, tooltip, rl, callback, null);
    }

    public ElementToggleTextured setWarping()
    {
        warping = true;
        return this;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.render(graphics, mouseX, mouseY, partialTick);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        PoseStack stack = graphics.pose();
        if(warping)
        {
            RenderHelper.draw(textureLocation, stack, getLeft() + 2, getTop() + 2, width - 4, height - 4, 0);
        }
        else
        {
            int length = Math.min(width, height) - 4;
            int x = (int)(getLeft() + (width / 2D) - (length / 2D));
            int y = (int)(getTop() + (height / 2D) - (length / 2D));
            RenderHelper.draw(textureLocation, stack, x , y, length, length, 0);
        }
    }
}
