package me.ichun.mods.ichunutil.client.entity.head;

import net.minecraft.entity.monster.EntityEnderman;

public class HeadEnderman extends HeadBase<EntityEnderman>
{
    public HeadEnderman() //test screaming
    {
        eyeOffset = new float[] { 0F, 3.5F/16F, 4F/16F };
        irisColour = new float[] { 0.88F, 0.47F, 0.98F };
        pupilColour = new float[] { 0.8F, 0F, 0.98F };
        halfInterpupillaryDistance = 2.5F/16F;
        eyeScale = 0.85F;
    }

    @Override
    public float getPupilScale(EntityEnderman living, float partialTick, int eye)
    {
        if(living.isScreaming())
        {
            return 0.4F;
        }
        return super.getPupilScale(living, partialTick, eye);
    }

    @Override
    public boolean affectedByInvisibility(EntityEnderman living, int eye)
    {
        return false;
    }

    @Override
    public boolean doesEyeGlow(EntityEnderman living, int eye)
    {
        return true;
    }
}
