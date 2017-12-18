package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.minecraft.entity.monster.EntityGhast;

public class HeadGhast extends HeadBase<EntityGhast>
{
    public HeadGhast()
    {
        eyeOffset = new float[]{ 0F, -6.5F/16F, 8F/16F };
        halfInterpupillaryDistance = 3.5F/16F;
        eyeScale = 1.5F;
    }

    @Override
    public float getEyeScale(EntityGhast living, float partialTick, int eye)
    {
        if(living.isAttacking())
        {
            return eyeScale;
        }
        return 0F;
    }
}
