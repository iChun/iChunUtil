package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.minecraft.entity.monster.EntityGuardian;

public class HeadGuardian extends HeadBase<EntityGuardian>
{
    public HeadGuardian()
    {
        eyeOffset = new float[]{ 0F, -16F/16F, 8F/16F };
        irisColour = new float[] { 214F / 255F, 211F / 255F, 203F / 255F };
        pupilColour = new float[] { 101F / 255F, 35F / 255F, 31F / 255F };
        halfInterpupillaryDistance = 0F;
        eyeScale = 2F;
    }

    @Override
    public int getEyeCount(EntityGuardian living)
    {
        return 1;
    }

    @Override
    public float[] getIrisColours(EntityGuardian living, float partialTick, int eye)
    {
        return irisColour;
    }

    @Override
    public float[] getPupilColours(EntityGuardian living, float partialTick, int eye)
    {
        return pupilColour;
    }
}
