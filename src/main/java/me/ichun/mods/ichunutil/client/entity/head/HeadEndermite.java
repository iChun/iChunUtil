package me.ichun.mods.ichunutil.client.entity.head;

import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.util.math.MathHelper;

public class HeadEndermite extends HeadBase<EntityEndermite>
{
    public HeadEndermite()
    {
        headJoint = new float[]{ 0F, -21F/16F, 3.5F/16F };
        eyeOffset = new float[]{ 0F, 0F, 2F/16F };
        irisColour = new float[] { 87F/255F, 23F/255F, 50F/255F };
        halfInterpupillaryDistance = 0F;
    }

    @Override
    public int getEyeCount(EntityEndermite living)
    {
        eyeOffset = new float[]{ 0F, 0.5F/16F, 1F/16F };
        eyeScale = 0.6F;
        return 1;
    }

    @Override
    public float getHeadYaw(EntityEndermite living, float partialTick, int eye)
    {
        int i = 0;
        float ageInTicks = (float)living.ticksExisted + partialTick;
        return (float)Math.toDegrees(MathHelper.cos(ageInTicks * 0.9F + (float)i * 0.15F * (float)Math.PI) * (float)Math.PI * 0.01F * (float)(1 + Math.abs(i - 2)));
    }

    @Override
    public float getHeadPitch(EntityEndermite living, float partialTick, int eye)
    {
        return 0F;
    }

    @Override
    public float getHeadYawForTracker(EntityEndermite living)
    {
        int i = 0;
        float ageInTicks = (float)living.ticksExisted;
        return (float)Math.toDegrees(MathHelper.cos(ageInTicks * 0.9F + (float)i * 0.15F * (float)Math.PI) * (float)Math.PI * 0.01F * (float)(1 + Math.abs(i - 2)));
    }

    @Override
    public float getHeadPitchForTracker(EntityEndermite living)
    {
        return 0F;
    }

    @Override
    public float[] getHeadJointOffset(EntityEndermite living, float partialTick, int eye)
    {
        int i = 0;
        float ageInTicks = (float)living.ticksExisted + partialTick;
        return new float[]{ -(MathHelper.sin(ageInTicks * 0.9F + (float)i * 0.15F * (float)Math.PI) * (float)Math.PI * 0.1F * (float)Math.abs(i - 2)) / 16F, -22F/16F, 3.5F/16F };
    }
}
