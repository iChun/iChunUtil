package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.minecraft.entity.monster.EntityEndermite;

public class HeadEndermite extends HeadBase<EntityEndermite>
{
    public HeadEndermite()
    {
        eyeOffset = new float[]{ 0F, 0F, 1F/16F };
        irisColour = new float[] { 87F/255F, 23F/255F, 50F/255F };
        halfInterpupillaryDistance = 0F;
        eyeScale = 0.6F;
    }

    @Override
    public int getEyeCount(EntityEndermite living)
    {
        return 1;
    }
}
