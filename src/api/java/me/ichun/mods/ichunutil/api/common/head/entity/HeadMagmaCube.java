package me.ichun.mods.ichunutil.api.common.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import net.minecraft.entity.monster.MagmaCubeEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HeadMagmaCube extends HeadInfo<MagmaCubeEntity>
{
    @OnlyIn(Dist.CLIENT)
    @Override
    public float getEyeScale(MagmaCubeEntity living, MatrixStack stack, float partialTick, int eye)
    {
        float squishFactor = living.prevSquishFactor + (living.squishFactor - living.prevSquishFactor) * partialTick;
        if(squishFactor <= 0F)
        {
            return eyeScale;
        }
        else
        {
            return eyeScale + squishFactor * 1.5F;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float[] getHeadJointOffset(MagmaCubeEntity living, MatrixStack stack, float partialTick, int head)
    {
        float squishFactor = living.prevSquishFactor + (living.squishFactor - living.prevSquishFactor) * partialTick;
        if(squishFactor <= 0F)
        {
            return super.getHeadJointOffset(living, stack, partialTick, head);
        }
        else
        {
            return new float[]{ 0F, -(0 - squishFactor * 2.5F)/16F, 0F };
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getHeadArmorScale(MagmaCubeEntity living, MatrixStack stack, float partialTick, int head)
    {
        float squishFactor = living.prevSquishFactor + (living.squishFactor - living.prevSquishFactor) * partialTick;
        if(squishFactor > 0F)
        {
            stack.scale(1F, 1F + squishFactor * 4F, 1F);
        }

        return super.getHeadArmorScale(living, stack, partialTick, head);
    }
}
