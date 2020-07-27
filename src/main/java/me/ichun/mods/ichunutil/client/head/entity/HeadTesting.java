package me.ichun.mods.ichunutil.client.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.head.HeadBase;
import net.minecraft.entity.LivingEntity;

public class HeadTesting extends HeadBase<LivingEntity>
{
    @Override
    public float getEyeSideOffset(LivingEntity living, MatrixStack stack, float partialTick, int eye)
    {
        return super.getEyeSideOffset(living, stack, partialTick, eye);
    }

    @Override
    public float getEyeScale(LivingEntity living, MatrixStack stack, float partialTick, int eye)
    {
        return super.getEyeScale(living, stack, partialTick, eye);
    }

    @Override
    public float[] getHeadJointOffset(LivingEntity living, MatrixStack stack, float partialTick, int eye)
    {
        return super.getHeadJointOffset(living, stack, partialTick, eye);
    }

    @Override
    public float[] getEyeOffsetFromJoint(LivingEntity living, MatrixStack stack, float partialTick, int eye)
    {
        return super.getEyeOffsetFromJoint(living, stack, partialTick, eye);
    }

    @Override
    public float getEyeRotation(LivingEntity living, MatrixStack stack, float partialTick, int eye)
    {
        return super.getEyeRotation(living, stack, partialTick, eye);
    }

    @Override
    public float getHeadRoll(LivingEntity living, MatrixStack stack, float partialTick, int eye)
    {
        return super.getHeadRoll(living, stack, partialTick, eye);
    }
}
