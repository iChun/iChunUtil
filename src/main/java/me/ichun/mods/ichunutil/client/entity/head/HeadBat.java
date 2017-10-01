package me.ichun.mods.ichunutil.client.entity.head;

import net.minecraft.entity.passive.EntityBat;

public class HeadBat extends HeadBase<EntityBat>
{
    public HeadBat()
    {
        eyeOffset = new float[]{ 0F, 0.5F/16F, 3F/16F };
    }

    @Override
    public float getHeadYaw(EntityBat living, float partialTick, int eye)
    {
        if(living.getIsBatHanging())
        {
            return -super.getHeadYaw(living, partialTick, eye);
        }
        else
        {
            return super.getHeadYaw(living, partialTick, eye);
        }
    }
}
