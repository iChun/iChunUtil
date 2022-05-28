package me.ichun.mods.ichunutil.common.head.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import net.minecraft.world.entity.LivingEntity;

public class HeadTesting extends HeadInfo<LivingEntity>
{
    @Override
    public float getEyeSideOffset(LivingEntity living, PoseStack stack, float partialTick, int eye)
    {
        return super.getEyeSideOffset(living, stack, partialTick, eye);
    }

    @Override
    public float getEyeScale(LivingEntity living, PoseStack stack, float partialTick, int eye)
    {
        return super.getEyeScale(living, stack, partialTick, eye);
    }

    @Override
    public float[] getHeadJointOffset(LivingEntity living, PoseStack stack, float partialTick, int head)
    {
        return super.getHeadJointOffset(living, stack, partialTick, head);
    }

    @Override
    public float[] getEyeOffsetFromJoint(LivingEntity living, PoseStack stack, float partialTick, int eye)
    {
        return super.getEyeOffsetFromJoint(living, stack, partialTick, eye);
    }

    @Override
    public float getEyeRotation(LivingEntity living, PoseStack stack, float partialTick, int eye)
    {
        return super.getEyeRotation(living, stack, partialTick, eye);
    }

    @Override
    public float getHeadRoll(LivingEntity living, PoseStack stack, float partialTick, int head, int eye)
    {
        return super.getHeadRoll(living, stack, partialTick, head, eye);
    }
}
