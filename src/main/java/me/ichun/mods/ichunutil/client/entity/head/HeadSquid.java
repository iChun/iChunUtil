package me.ichun.mods.ichunutil.client.entity.head;

import net.minecraft.entity.passive.EntitySquid;

public class HeadSquid extends HeadBase<EntitySquid>
{
    public HeadSquid()
    {
        eyeOffset = new float[]{ 0F, 1F/16F, 6F/16F };
        halfInterpupillaryDistance = 3F/16F;
    }

    @Override
    public float getHeadYawForTracker(EntitySquid living)
    {
        return -living.squidYaw;
    }

    @Override
    public float getHeadPitchForTracker(EntitySquid living)
    {
        return -living.squidPitch;
    }
}
