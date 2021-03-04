package me.ichun.mods.ichunutil.common.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.common.head.HeadInfo;
import net.minecraft.entity.monster.MagmaCubeEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeadMagmaCube extends HeadInfo<MagmaCubeEntity>
{
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

    @Override
    public float[] getHeadJointOffset(MagmaCubeEntity living, MatrixStack stack, float partialTick, int eye)
    {
        float squishFactor = living.prevSquishFactor + (living.squishFactor - living.prevSquishFactor) * partialTick;
        if(squishFactor <= 0F)
        {
            return super.getHeadJointOffset(living, stack, partialTick, eye);
        }
        else
        {
            return new float[]{ 0F, -(0 - squishFactor * 2.5F)/16F, 0F };
        }
    }
}
