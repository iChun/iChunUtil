package me.ichun.mods.ichunutil.client.entity.head;

import net.minecraft.entity.monster.EntitySlime;

public class HeadSlime extends HeadBase<EntitySlime>
{
    public HeadSlime()
    {
        eyeOffset = new float[]{ 0F, -19F/16F, 4F/16F };
    }

    @Override
    public float getHeadYawForTracker(EntitySlime living)
    {
        return living.renderYawOffset;
    }

    @Override
    public float getHeadPitchForTracker(EntitySlime living)
    {
        return 0F;
    }
}
