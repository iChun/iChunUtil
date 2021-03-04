package me.ichun.mods.ichunutil.common.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.common.head.HeadInfo;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeadWither extends HeadInfo<WitherEntity>
{
    public float[] headJointSideHeadLeft = new float[] { -10F/16F, -4F/16F, 0F };
    public float[] headJointSideHeadRight = new float[] { 8F/16F, -4F/16F, 0F };
    public float[] eyeOffsetSideHead = new float[] { 1F/16F, 2F/16F, 4F/16F };
    public float halfInterpupillaryDistanceSideHead = 2.5F / 16F;
    public float eyeScaleSideHead = 0.75F;

    @Override
    public float getEyeScale(WitherEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(eye > 1)
        {
            return eyeScaleSideHead;
        }
        return eyeScale;
    }

    @Override
    public float getHeadYaw(WitherEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(eye <= 1)
        {
            return super.getHeadYaw(living, stack, partialTick, eye);
        }
        else
        {
            return (float)Math.toDegrees(headModel[eye <= 3 ? 2 : 1].rotateAngleY);
        }
    }

    @Override
    public float getHeadYaw(WitherEntity living, float partialTick, int eye)
    {
        if(eye <= 1)
        {
            return super.getHeadYaw(living, partialTick, eye);
        }
        else
        {
            return living.getHeadYRotation(eye <= 3 ? 2 : 1); //Bear in mind the function is only in the client.
        }
    }

    @Override
    public float getHeadPitch(WitherEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(eye <= 1)
        {
            return super.getHeadPitch(living, stack, partialTick, eye);
        }
        else
        {
            return (float)Math.toDegrees(headModel[eye <= 3 ? 2 : 1].rotateAngleX);
        }
    }

    @Override
    public float getHeadPitch(WitherEntity living, float partialTick, int eye)
    {
        if(eye <= 1)
        {
            return super.getHeadPitch(living, partialTick, eye);
        }
        else
        {
            return living.getHeadXRotation(eye <= 3 ? 2 : 1); //Bear in mind the function is only in the client.
        }
    }

    @Override
    public float getHeadRoll(WitherEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(eye <= 1)
        {
            return super.getHeadRoll(living, stack, partialTick, eye);
        }
        else
        {
            return (float)Math.toDegrees(headModel[eye <= 3 ? 2 : 1].rotateAngleZ);
        }
    }

    @Override
    public float getEyeSideOffset(WitherEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(eye <= 1)
        {
            return eye % 2 == 0 ? halfInterpupillaryDistance : -halfInterpupillaryDistance;
        }
        else
        {
            return eye % 2 == 0 ? halfInterpupillaryDistanceSideHead : -halfInterpupillaryDistanceSideHead;
        }
    }

    @Override
    public float[] getEyeOffsetFromJoint(WitherEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(eye <= 1)
        {
            return eyeOffset;
        }
        else
        {
            return eyeOffsetSideHead;
        }
    }

    @Override
    public float[] getHeadJointOffset(WitherEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(eye <= 1)
        {
            return super.getHeadJointOffset(living, stack, partialTick, eye);
        }
        else if(eye <= 3)
        {
            return headJointSideHeadLeft;
        }
        else
        {
            return headJointSideHeadRight;
        }
    }
}
