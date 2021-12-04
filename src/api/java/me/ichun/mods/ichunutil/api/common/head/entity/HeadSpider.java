package me.ichun.mods.ichunutil.api.common.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HeadSpider extends HeadInfo<SpiderEntity> //TODO remove this in 1.17+, use WenXin's workaround with additional heads
{
    public float halfInterpupillaryDistance2 = 2F / 16F;
    public float halfInterpupillaryDistance3 = 4F / 16F;
    public float[] eyeOffset2 = new float[] { 0F, 3F/16F, 8F / 16F };
    public float[] eyeOffset3 = new float[] { 0F, 1F/16F, 7.5F / 16F };

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getEyeSideOffset(SpiderEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(eye <= 1)
        {
            return eye % 2 == 0 ? halfInterpupillaryDistance : -halfInterpupillaryDistance;
        }
        else if(eye <= 3)
        {
            return eye % 2 == 0 ? halfInterpupillaryDistance2 : -halfInterpupillaryDistance2;
        }
        else
        {
            return eye % 2 == 0 ? halfInterpupillaryDistance3 : -halfInterpupillaryDistance3;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getEyeRotation(SpiderEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(eye >= 4)
        {
            return eye % 2 == 0 ? 45F : -45F;
        }
        return 0F;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float[] getEyeOffsetFromJoint(SpiderEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(eye <= 1)
        {
            return eyeOffset;
        }
        else if(eye <= 3)
        {
            return eyeOffset2;
        }
        else
        {
            return eyeOffset3;
        }
    }
}
