package me.ichun.mods.ichunutil.client.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.head.HeadBase;
import net.minecraft.entity.LivingEntity;

public class HeadTesting extends HeadBase<LivingEntity>
{
    @Override
    public float getEyeSideOffset(LivingEntity living, MatrixStack stack, float partialTick, int eye)
    {
        halfInterpupillaryDistance = 2F / 16F;
        return super.getEyeSideOffset(living, stack, partialTick, eye);
    }

    @Override
    public float getEyeScale(LivingEntity living, MatrixStack stack, float partialTick, int eye)
    {
        return 0.4F;
    }

    @Override
    public float[] getHeadJointOffset(LivingEntity living, MatrixStack stack, float partialTick, int eye)
    {
        return new float[] { 0F, -10F/16F, 16F/16F };
    }

    @Override
    public float[] getEyeOffsetFromJoint(LivingEntity living, MatrixStack stack, float partialTick, int eye)
    {
        return new float[] { 0F, -0.5F/16F, 3F/16F };
    }
}
