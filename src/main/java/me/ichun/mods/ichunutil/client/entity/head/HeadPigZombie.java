package me.ichun.mods.ichunutil.client.entity.head;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.EntityPigZombie;

public class HeadPigZombie extends HeadBase<EntityPigZombie>
{
    public float[] headJointSneak = new float[] { 0F, -1F/16F, 0F };
    public float[] eyeOffsetSkin = new float[]{ -0.35F/16F, 4.5F/16F, 4.5F/16F };
    public float eyeScaleSkin = 0.65F;

    public HeadPigZombie()
    {
        headJoint = new float[]{ 0F, 0F, 0F };
        eyeOffset = new float[]{ 0F, 4.5F/16F, 4F/16F };
        halfInterpupillaryDistance = 3F/16F;
        eyeScale = 0.8F;
    }

    @Override
    public float getEyeScale(EntityPigZombie living, float partialTick, int eye)
    {
        if(eye == 1 && !living.isChild())
        {
            return eyeScaleSkin;
        }
        return eyeScale;
    }

    @Override
    public float[] getEyeOffsetFromJoint(EntityPigZombie living, float partialTick, int eye)
    {
        if(eye == 1 && !living.isChild())
        {
            return eyeOffsetSkin;
        }
        return eyeOffset;
    }

    @Override
    public float[] getHeadJointOffset(EntityPigZombie living, float partialTick, int eye)
    {
        if(living.isSneaking())
        {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
            return headJointSneak;
        }
        return headJoint;
    }
}
