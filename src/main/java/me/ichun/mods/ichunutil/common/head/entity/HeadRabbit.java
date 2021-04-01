package me.ichun.mods.ichunutil.common.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.common.head.HeadInfo;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HeadRabbit extends HeadInfo<RabbitEntity>
{
    @OnlyIn(Dist.CLIENT)
    @Override
    public void preChildEntHeadRenderCalls(RabbitEntity living, MatrixStack stack, LivingRenderer<RabbitEntity, ?> render)
    {
        float scale = 0.0625F;
        if(living.isChild())
        {
            stack.scale(0.56666666F, 0.56666666F, 0.56666666F);
            stack.translate(0.0F, 22.0F * scale, 2.0F * scale);
        }
        else
        {
            stack.scale(0.6F, 0.6F, 0.6F);
            stack.translate(0.0F, 16.0F * scale, 0.0F);
        }
    }
}
