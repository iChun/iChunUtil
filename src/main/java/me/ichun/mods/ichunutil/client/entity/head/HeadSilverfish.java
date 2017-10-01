package me.ichun.mods.ichunutil.client.entity.head;

import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.util.math.MathHelper;

public class HeadSilverfish extends HeadBase<EntitySilverfish>
{
    public HeadSilverfish()
    {
        headJoint = new float[]{ 0F, -22F/16F, 3.5F/16F };
        eyeOffset = new float[]{ 0F, 0F, 1F/16F };
        halfInterpupillaryDistance = 1F/16F;
        eyeScale = 0.5F;
    }

    @Override
    public float getHeadYaw(EntitySilverfish living, float partialTick, int eye)
    {
        int i = 0;
        float ageInTicks = (float)living.ticksExisted + partialTick;
        return (float)Math.toDegrees(MathHelper.cos(ageInTicks * 0.9F + (float)i * 0.15F * (float)Math.PI) * (float)Math.PI * 0.05F * (float)(1 + Math.abs(i - 2)));
    }

    @Override
    public float getHeadPitch(EntitySilverfish living, float partialTick, int eye)
    {
        return 0F;
    }

    @Override
    public float getHeadYawForTracker(EntitySilverfish living)
    {
        int i = 0;
        float ageInTicks = (float)living.ticksExisted;
        return (float)Math.toDegrees(MathHelper.cos(ageInTicks * 0.9F + (float)i * 0.15F * (float)Math.PI) * (float)Math.PI * 0.05F * (float)(1 + Math.abs(i - 2)));
    }

    @Override
    public float getHeadPitchForTracker(EntitySilverfish living)
    {
        return 0F;
    }

    @Override
    public float[] getHeadJointOffset(EntitySilverfish living, float partialTick, int eye)
    {
        int i = 0;
        float ageInTicks = (float)living.ticksExisted + partialTick;
        return new float[]{ -(MathHelper.sin(ageInTicks * 0.9F + (float)i * 0.15F * (float)Math.PI) * (float)Math.PI * 0.2F * (float)Math.abs(i - 2))/16F, -22F/16F, 3.5F/16F };
    }
}
