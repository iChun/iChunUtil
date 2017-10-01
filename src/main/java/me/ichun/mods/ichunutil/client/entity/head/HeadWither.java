package me.ichun.mods.ichunutil.client.entity.head;

import net.minecraft.entity.boss.EntityWither;

public class HeadWither extends HeadBase<EntityWither>
{
    public float[] headJointSideHeadLeft = new float[] { -10F/16F, -4F/16F, 0F };
    public float[] headJointSideHeadRight = new float[] { 8F/16F, -4F/16F, 0F };
    public float[] eyeOffsetSideHead = new float[] { 1F/16F, 2F/16F, 4F/16F };
    public float halfInterpupillaryDistanceSideHead = 2.5F / 16F;
    public float eyeScaleSideHead = 0.75F;

    public HeadWither()
    {
        headJoint = new float[] { 0F, 0F, 0F };
        eyeOffset = new float[] { 0F, 0F, 4F / 16F };
        halfInterpupillaryDistance = 2F / 16F;
        eyeScale = 1F;
    }

    @Override
    public int getEyeCount(EntityWither living)
    {
        return 6;
    }

    @Override
    public float getEyeScale(EntityWither living, float partialTick, int eye)
    {
        if(eye > 1)
        {
            return eyeScaleSideHead;
        }
        return eyeScale;
    }

    @Override
    public float getHeadYaw(EntityWither living, float partialTick, int eye)
    {
        if(eye <= 1)
        {
            return super.getHeadYaw(living, partialTick, eye);
        }
        else
        {
            return living.getHeadYRotation(eye <= 3 ? 1 : 0) - (living.prevRenderYawOffset + ((living.renderYawOffset - living.prevRenderYawOffset) * partialTick));
        }
    }

    @Override
    public float getHeadPitch(EntityWither living, float partialTick, int eye)
    {
        if(eye <= 1)
        {
            return super.getHeadPitch(living, partialTick, eye);
        }
        else
        {
            return living.getHeadXRotation(eye <= 3 ? 1 : 0);
        }
    }

    @Override
    public float getHeadYawForTracker(EntityWither living, int eye)
    {
        if(eye <= 1)
        {
            return super.getHeadYawForTracker(living, eye);
        }
        else
        {
            return living.getHeadYRotation(eye <= 3 ? 1 : 0);
        }
    }

    @Override
    public float getHeadPitchForTracker(EntityWither living, int eye)
    {
        if(eye <= 1)
        {
            return super.getHeadPitchForTracker(living);
        }
        else
        {
            return living.getHeadXRotation(eye <= 3 ? 1 : 0);
        }
    }

    @Override
    public float getEyeSideOffset(EntityWither living, float partialTick, int eye)
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
    public float[] getEyeOffsetFromJoint(EntityWither living, float partialTick, int eye)
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
    public float[] getHeadJointOffset(EntityWither living, float partialTick, int eye)
    {
        if(eye <= 1)
        {
            return super.getHeadJointOffset(living, partialTick, eye);
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
