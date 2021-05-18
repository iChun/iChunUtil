package me.ichun.mods.ichunutil.api.common.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HeadBodyHorse extends HeadInfo<AbstractHorseEntity>
{
    @OnlyIn(Dist.CLIENT)
    @Override
    public float getHeadYaw(AbstractHorseEntity living, MatrixStack stack, float partialTick, int head, int eye)
    {
        return 180F;
    }

    @Override
    public float getHeadYaw(AbstractHorseEntity living, float partialTick, int head, int eye)
    {
        return (living.prevRenderYawOffset + (living.renderYawOffset - living.prevRenderYawOffset) * partialTick) - 180F;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getHeadPitch(AbstractHorseEntity living, MatrixStack stack, float partialTick, int head, int eye)
    {
        return (float)Math.toDegrees(living.getRearingAmount(partialTick) * ((float)Math.PI / 4F));
    }

    @Override
    public float getHeadPitch(AbstractHorseEntity living, float partialTick, int head, int eye)
    {
        return (float)Math.toDegrees(living.getRearingAmount(partialTick) * ((float)Math.PI / 4F));
    }
}
