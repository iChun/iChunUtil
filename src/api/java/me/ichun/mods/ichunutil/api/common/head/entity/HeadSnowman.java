package me.ichun.mods.ichunutil.api.common.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HeadSnowman extends HeadInfo<SnowGolemEntity>
{
    public float[] eyeOffsetNoPumpkinLeft = new float[] { 0F, 5.5F/16F, 3.5F/16F };
    public float[] eyeOffsetNoPumpkinRight = new float[] { 0F, 6F/16F, 3.5F/16F };
    public float[] headTopCenterNoPumpkin = new float[] { 0.0F, 0.46875F, 0.0F };
    public float headScaleNoPumpkin = 0.875F;

    @OnlyIn(Dist.CLIENT)
    @Override
    public float[] getEyeOffsetFromJoint(SnowGolemEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(living.isPumpkinEquipped())
        {
            return eyeOffset;
        }
        else
        {
            if(eye == 0)
            {
                return eyeOffsetNoPumpkinRight;
            }
            return eyeOffsetNoPumpkinLeft;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getEyeScale(SnowGolemEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(living.isPumpkinEquipped())
        {
            return eyeScale;
        }
        else
        {
            return eye == 0 ? 0.65F : 0.75F;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float[] getHatOffsetFromJoint(SnowGolemEntity living, MatrixStack stack, float partialTick, int head)
    {
        if(living.isPumpkinEquipped())
        {
            return super.getHatOffsetFromJoint(living, stack, partialTick, head);
        }
        else
        {
            return headTopCenterNoPumpkin;
        }

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getHatScale(SnowGolemEntity living, MatrixStack stack, float partialTick, int head)
    {
        if(living.isPumpkinEquipped())
        {
            return super.getHatScale(living, stack, partialTick, head);
        }
        else
        {
            return headScaleNoPumpkin;
        }
    }
}
