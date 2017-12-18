package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.minecraft.entity.monster.EntitySnowman;

public class HeadSnowman extends HeadBase<EntitySnowman>
{
    public float[] eyeOffsetNoPumpkinLeft = new float[] { 0F, 5.5F/16F, 3.5F/16F };
    public float[] eyeOffsetNoPumpkinRight = new float[] { 0F, 6F/16F, 3.5F/16F };
    public HeadSnowman()
    {
        eyeOffset = new float[] { 0F, 7.5F/16F, 5F/16F };
        halfInterpupillaryDistance = 1.5F / 16F;
        eyeScale = 1F;
    }

    @Override
    public float[] getEyeOffsetFromJoint(EntitySnowman living, float partialTick, int eye)
    {
        if(living.isPumpkinEquipped())
        {
            return eyeOffset;
        }
        else
        {
            if(eye == 0)
            {
                return eyeOffsetNoPumpkinRight;
            }
            return eyeOffsetNoPumpkinLeft;
        }
    }

    @Override
    public float getEyeScale(EntitySnowman living, float partialTick, int eye)
    {
        if(living.isPumpkinEquipped())
        {
            return eyeScale;
        }
        else
        {
            return eye == 0 ? 0.65F : 0.75F;
        }
    }

}
