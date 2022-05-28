package me.ichun.mods.ichunutil.api.common.head.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import net.minecraft.world.entity.animal.SnowGolem;


public class HeadSnowman extends HeadInfo<SnowGolem>
{
    public float[] eyeOffsetNoPumpkinLeft = new float[] { 0F, 5.5F/16F, 3.5F/16F };
    public float[] eyeOffsetNoPumpkinRight = new float[] { 0F, 6F/16F, 3.5F/16F };
    public float[] headTopCenterNoPumpkin = new float[] { 0.0F, 0.46875F, 0.0F };
    public float headScaleNoPumpkin = 0.875F;

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float[] getEyeOffsetFromJoint(SnowGolem living, PoseStack stack, float partialTick, int eye)
    {
        if(living.hasPumpkin())
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

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getEyeScale(SnowGolem living, PoseStack stack, float partialTick, int eye)
    {
        if(living.hasPumpkin())
        {
            return eyeScale;
        }
        else
        {
            return eye == 0 ? 0.65F : 0.75F;
        }
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float[] getHatOffsetFromJoint(SnowGolem living, PoseStack stack, float partialTick, int head)
    {
        if(living.hasPumpkin())
        {
            return super.getHatOffsetFromJoint(living, stack, partialTick, head);
        }
        else
        {
            return headTopCenterNoPumpkin;
        }

    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getHatScale(SnowGolem living, PoseStack stack, float partialTick, int head)
    {
        if(living.hasPumpkin())
        {
            return super.getHatScale(living, stack, partialTick, head);
        }
        else
        {
            return headScaleNoPumpkin;
        }
    }
}
