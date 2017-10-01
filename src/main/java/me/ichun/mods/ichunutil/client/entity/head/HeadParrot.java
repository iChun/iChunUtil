package me.ichun.mods.ichunutil.client.entity.head;

import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.util.math.MathHelper;

public class HeadParrot extends HeadBase<EntityParrot>
{
    public final float[] headJointSitting = new float[]{ 0F, -17.59F/16F, 2.76F/16F };

    public HeadParrot()
    {
        headJoint = new float[]{ 0F, -15.69F/16F, 2.76F/16F };
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
    public float[] getHeadJointOffset(EntityParrot living, float partialTick, int eye)
    {
        if(living.isPartying())
        {
            float f1 = MathHelper.cos((float)living.ticksExisted);
            float f2 = MathHelper.sin((float)living.ticksExisted);
            return new float[]{ -f1/ 16F, (-15.69F - f2)/16F, 2.76F/16F };
        }
        else if(living.isFlying())
        {
            float f = living.oFlap + (living.flap - living.oFlap) * partialTick;
            float f1 = living.oFlapSpeed + (living.flapSpeed - living.oFlapSpeed) * partialTick;
            return new float[]{ 0F, (-15.69F - ((MathHelper.sin(f) + 1.0F) * f1  * 0.3F))/16F, 2.76F/16F };
        }
        return living.isSitting() ? headJointSitting : headJoint;
    }

    @Override
    public float getHeadYaw(EntityParrot living, float partialTick, int eye)
    {
        if(living.isPartying())
        {
            return 0F;
        }
        return super.getHeadYaw(living, partialTick, eye);
    }

    @Override
    public float getHeadPitch(EntityParrot living, float partialTick, int eye)
    {
        if(living.isPartying())
        {
            return 0F;
        }
        return super.getHeadPitch(living, partialTick, eye);
    }

    @Override
    public float getHeadRoll(EntityParrot living, float partialTick, int eye)
    {
        if(living.isPartying())
        {
            return (float)Math.toDegrees(MathHelper.sin((float)living.ticksExisted) * 0.4F);
        }
        return super.getHeadRoll(living, partialTick, eye);
    }

    @Override
    public float getPupilScale(EntityParrot living, float partialTick, int eye)
    {
        return super.getPupilScale(living, partialTick, eye) * (living.isPartying() ? 1.6F : 1F);
    }
}
