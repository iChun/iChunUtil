package me.ichun.mods.ichunutil.client.entity.head;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;

public class HeadBiped extends HeadBase<EntityLivingBase>
{
    @Override
    public float[] getHeadJointOffset(EntityLivingBase living, float partialTick, int eye)
    {
        if(living.isSneaking())
        {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
        }
        return super.getHeadJointOffset(living, partialTick, eye);
    }
}
