package me.ichun.mods.ichunutil.client.entity.head;

import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.util.math.MathHelper;

public class HeadParrot extends HeadBase<EntityParrot>
{
    public HeadParrot()
    {
        eyeOffset = new float[]{ 0F, 0.95F/16F, 0.5F/16F };
        halfInterpupillaryDistance = 1F / 16F;
        eyeScale = 0.375F;
    }

    @Override
    public float getEyeRotation(EntityParrot living, float partialTick, int eye)
    {
        return eye == 0 ? 90F : - 90F;
    }

    @Override
    public float getHeadRollForTracker(EntityParrot living, int eye)
    {
        if(living.isPartying())
        {
            return (float)Math.toDegrees(MathHelper.sin((float)living.ticksExisted) * 0.4F);
        }
        return super.getHeadRollForTracker(living, eye);
    }

    @Override
    public float getPupilScale(EntityParrot living, float partialTick, int eye)
    {
        return super.getPupilScale(living, partialTick, eye) * (living.isPartying() ? 1.6F : 1F);
    }
}
