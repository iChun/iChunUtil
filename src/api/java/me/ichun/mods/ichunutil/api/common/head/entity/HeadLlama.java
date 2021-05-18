package me.ichun.mods.ichunutil.api.common.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HeadLlama extends HeadInfo<LlamaEntity>
{
    @OnlyIn(Dist.CLIENT)
    @Override
    public float getHeadYaw(LlamaEntity living, MatrixStack stack, float partialTick, int head, int eye)
    {
        return HeadInfo.horseEasterEgg.getAsBoolean() ? 180F : super.getHeadYaw(living, stack, partialTick, head, eye);
    }

    @Override
    public float getHeadYaw(LlamaEntity living, float partialTick, int head, int eye)
    {
        return HeadInfo.horseEasterEgg.getAsBoolean() ? (living.prevRenderYawOffset + (living.renderYawOffset - living.prevRenderYawOffset) * partialTick) - 180F : super.getHeadYaw(living, partialTick, head, eye);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getHeadPitch(LlamaEntity living, MatrixStack stack, float partialTick, int head, int eye)
    {
        return HeadInfo.horseEasterEgg.getAsBoolean() ? 0F : super.getHeadPitch(living, stack, partialTick, head, eye);
    }

    @Override
    public float getHeadPitch(LlamaEntity living, float partialTick, int head, int eye)
    {
        return HeadInfo.horseEasterEgg.getAsBoolean() ? 0F : super.getHeadPitch(living, partialTick, head, eye);
    }
}
