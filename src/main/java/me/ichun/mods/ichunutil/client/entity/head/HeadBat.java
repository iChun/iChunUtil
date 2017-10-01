package me.ichun.mods.ichunutil.client.entity.head;

import net.minecraft.entity.passive.EntityBat;

public class HeadBat extends HeadBase<EntityBat>
{
    public float[] headJointFlying = new float[] { 0F, -1F/16F, 0F }; //I love that I can use Tabula for this.

    public HeadBat()
    {
        headJoint = new float[]{ 0F, 2F/16F, 0F };
        eyeOffset = new float[]{ 0F, 0.5F/16F, 3F/16F };
    }

    @Override
    public float getHeadYaw(EntityBat living, float partialTick, int eye)
    {
        if(living.getIsBatHanging())
        {
            return 180F + super.getHeadYaw(living, partialTick, eye);
        }
        else
        {
            return super.getHeadYaw(living, partialTick, eye);
        }
    }

    @Override
    public float[] getHeadJointOffset(EntityBat living, float partialTick, int eye)
    {
        if(living.getIsBatHanging())
        {
            return headJoint;
        }
        else
        {
            return headJointFlying;
        }
    }
}
