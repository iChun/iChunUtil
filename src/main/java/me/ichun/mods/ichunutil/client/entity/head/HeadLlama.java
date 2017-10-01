package me.ichun.mods.ichunutil.client.entity.head;

import net.minecraft.entity.passive.AbstractHorse;

public class HeadLlama extends HeadHorse
{
    public HeadLlama()
    {
        headJoint = new float[] { 0F, -5F/16F, -2F/16F };
        eyeOffset = new float[] { 0F, 2F/16F, 8F/16F };
        halfInterpupillaryDistance = 3F/16F;
        eyeScale = 0.9F;
    }

    @Override
    public float[] getHeadJointOffset(AbstractHorse living, float partialTick, int eye)
    {
        return headJoint;
    }

    @Override
    public float getHeadYaw(AbstractHorse living, float partialTick, int eye)
    {
        return 180F;
    }

    @Override
    public float getHeadPitch(AbstractHorse living, float partialTick, int eye)
    {
        return 0F;
    }

    @Override
    public float getHeadYawForTracker(AbstractHorse living)
    {
        return living.renderYawOffset;
    }

    @Override
    public float getHeadPitchForTracker(AbstractHorse living)
    {
        return 0F;
    }
}
