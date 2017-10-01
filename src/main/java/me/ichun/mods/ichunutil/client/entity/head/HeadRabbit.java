package me.ichun.mods.ichunutil.client.entity.head;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.passive.EntityRabbit;

public class HeadRabbit extends HeadBase<EntityRabbit>
{
    public HeadRabbit()
    {
        headJoint = new float[]{ 0F, -16F/16F, 1F/16F };
        eyeOffset = new float[]{ 0F, 3F/16F, 5F/16F };
        halfInterpupillaryDistance = 1F / 16F;
        eyeScale = 0.6F;
    }

    @Override
    public float[] getHeadJointOffset(EntityRabbit living, float partialTick, int eye)
    {
        float scale = 0.0625F;
        if(living.isChild())
        {
            GlStateManager.scale(0.56666666F, 0.56666666F, 0.56666666F);
            GlStateManager.translate(0.0F, 22.0F * scale, 2.0F * scale);
        }
        else
        {
            GlStateManager.scale(0.6F, 0.6F, 0.6F);
            GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
        }
        return headJoint;
    }
}
