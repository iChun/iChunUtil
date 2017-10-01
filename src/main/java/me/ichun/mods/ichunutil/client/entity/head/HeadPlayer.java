package me.ichun.mods.ichunutil.client.entity.head;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;

public class HeadPlayer extends HeadBase<EntityPlayer>
{
    public float[] headJointSneak = new float[] { 0F, -1F/16F, 0F };

    @Override
    public float[] getHeadJointOffset(EntityPlayer living, float partialTick, int eye)
    {
        if(living.isSneaking())
        {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
            return headJointSneak;
        }
        return headJoint;
    }

    @Override
    public float getHeadPitch(EntityPlayer living, float partialTick, int eye)
    {
        if(living.isElytraFlying())
        {
            return -45F;
        }
        return super.getHeadPitch(living, partialTick, eye);
    }
}
