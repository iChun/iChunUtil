package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.minecraft.entity.monster.EntitySpider;

public class HeadSpider extends HeadBase<EntitySpider>
{
    public float halfInterpupillaryDistance2 = 2F / 16F;
    public float halfInterpupillaryDistance3 = 4F / 16F;
    public float[] eyeOffset2 = new float[] { 0F, 3F/16F, 8F / 16F };
    public float[] eyeOffset3 = new float[] { 0F, 1F/16F, 7.5F / 16F };

    public HeadSpider()
    {
        eyeOffset = new float[] { 0F, 0F, 8F / 16F };
        irisColour = new float[] { 0.8F, 0F, 0F };
        halfInterpupillaryDistance = 1F / 16F;
        eyeScale = 0.8F;
    }

    @Override
    public int getEyeCount(EntitySpider living)
    {
        return 6;
    }

    @Override
    public float getEyeSideOffset(EntitySpider living, float partialTick, int eye)
    {
        if(eye <= 1)
        {
            return eye % 2 == 0 ? halfInterpupillaryDistance : -halfInterpupillaryDistance;
        }
        else if(eye <= 3)
        {
            return eye % 2 == 0 ? halfInterpupillaryDistance2 : -halfInterpupillaryDistance2;
        }
        else
        {
            return eye % 2 == 0 ? halfInterpupillaryDistance3 : -halfInterpupillaryDistance3;
        }
    }

    @Override
    public float getEyeRotation(EntitySpider living, float partialTick, int eye)
    {
        if(eye >= 4)
        {
            return eye % 2 == 0 ? 45F : -45F;
        }
        return 0F;
    }

    @Override
    public float[] getEyeOffsetFromJoint(EntitySpider living, float partialTick, int eye)
    {
        if(eye <= 1)
        {
            return eyeOffset;
        }
        else if(eye <= 3)
        {
            return eyeOffset2;
        }
        else
        {
            return eyeOffset3;
        }
    }

    @Override
    public boolean affectedByInvisibility(EntitySpider living, int eye)
    {
        return false;
    }

    @Override
    public boolean doesEyeGlow(EntitySpider living, int eye)
    {
        return true;
    }
}
