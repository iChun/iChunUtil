package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.minecraft.entity.passive.EntityParrot;

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
    public float getPupilScale(EntityParrot living, float partialTick, int eye)
    {
        return super.getPupilScale(living, partialTick, eye) * (living.isPartying() ? 1.6F : 1F);
    }
}
