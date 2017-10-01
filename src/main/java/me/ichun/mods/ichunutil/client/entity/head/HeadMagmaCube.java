package me.ichun.mods.ichunutil.client.entity.head;

import net.minecraft.entity.monster.EntityMagmaCube;

public class HeadMagmaCube extends HeadBase<EntityMagmaCube>
{
    public HeadMagmaCube()
    {
        headJoint = new float[]{ 0F, -19F/16F, 0F };
        eyeOffset = new float[]{ 0F, 0F, 4F/16F };
    }

    @Override
    public float getEyeScale(EntityMagmaCube living, float partialTick, int eye)
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
    public float[] getHeadJointOffset(EntityMagmaCube living, float partialTick, int eye)
    {
        float squishFactor = living.prevSquishFactor + (living.squishFactor - living.prevSquishFactor) * partialTick;
        if(squishFactor <= 0F)
        {
            return headJoint;
        }
        else
        {
            return new float[]{ 0F, -(19F - squishFactor * 2.5F)/16F, 0F };
        }
    }

    @Override
    public float getHeadYaw(EntityMagmaCube living, float partialTick, int eye)
    {
        return 0F;
    }

    @Override
    public float getHeadPitch(EntityMagmaCube living, float partialTick, int eye)
    {
        return 0F;
    }

    @Override
    public float getHeadYawForTracker(EntityMagmaCube living)
    {
        return living.renderYawOffset;
    }

    @Override
    public float getHeadPitchForTracker(EntityMagmaCube living)
    {
        return 0F;
    }
}
