package me.ichun.mods.ichunutil.common.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.common.head.HeadInfo;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeadSnowman extends HeadInfo<SnowGolemEntity>
{
    public float[] eyeOffsetNoPumpkinLeft = new float[] { 0F, 5.5F/16F, 3.5F/16F };
    public float[] eyeOffsetNoPumpkinRight = new float[] { 0F, 6F/16F, 3.5F/16F };

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
}
