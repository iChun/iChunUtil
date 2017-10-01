package me.ichun.mods.ichunutil.client.entity.head;

import net.minecraft.entity.passive.EntityWolf;

public class HeadWolf extends HeadBase<EntityWolf>
{
    public float[] eyeOffsetTame = new float[] { -1F/16F, 1F/16F, 2F/16F };
    public float[] irisColourAngry = new float[] { 182F / 255F, 15F / 255F, 15F / 255F };
    public float[] pupilColourAngry = new float[] { 228F / 255F, 46F / 255F, 46F / 255F };

    public HeadWolf()
    {
        headJoint = new float[] { 1F/16F, -13.5F/16F, 7F/16F };
        eyeOffset = new float[] { -1F/16F, 0.5F/16F, 2F/16F };
        eyeScale = 0.65F;
    }

    @Override
    public float getHeadRoll(EntityWolf living, float partialTick, int eye)
    {
        return (float)Math.toDegrees(living.getInterestedAngle(partialTick) + living.getShakeAngle(partialTick, 0.0F));
    }

    @Override
    public float getEyeScale(EntityWolf living, float partialTick, int eye)
    {
        if(living.isTamed())
        {
            return 0.75F;
        }
        return eyeScale;
    }

    @Override
    public float[] getEyeOffsetFromJoint(EntityWolf living, float partialTick, int eye)
    {
        if(living.isTamed())
        {
            return eyeOffsetTame;
        }
        return eyeOffset;
    }

    @Override
    public float[] getIrisColours(EntityWolf living, float partialTick, int eye)
    {
        if(living.isAngry())
        {
            return irisColourAngry;
        }
        return irisColour;
    }

    @Override
    public float[] getPupilColours(EntityWolf living, float partialTick, int eye)
    {
        if(living.isAngry())
        {
            return pupilColourAngry;
        }
        return pupilColour;
    }

}
