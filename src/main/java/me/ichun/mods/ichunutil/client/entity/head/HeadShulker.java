package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.EntityShulker;

public class HeadShulker extends HeadBase<EntityShulker>
{
    public HeadShulker()
    {
        eyeOffset = new float[] { 0F, -3.5F/16F, 3F/16F };
        halfInterpupillaryDistance = 2F/16F;
        eyeScale = 0.6F;
    }

    @Override
    public float getEyeScale(EntityShulker living, float partialTick, int eye)
    {
        if(living.getClientPeekAmount(partialTick) <= 0F)
        {
            return 0F;
        }
        return eyeScale;
    }

    @Override
    public float[] getHeadJointOffset(EntityShulker living, float partialTick, int eye)
    {
        switch (living.getAttachmentFacing())
        {
            case DOWN:
            default:
                break;
            case EAST:
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.translate(1.0F, -1.0F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                break;
            case WEST:
                GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.translate(-1.0F, -1.0F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                break;
            case NORTH:
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.translate(0.0F, -1.0F, -1.0F);
                break;
            case SOUTH:
                GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.translate(0.0F, -1.0F, 1.0F);
                break;
            case UP:
                GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.translate(0.0F, -2.0F, 0.0F);
        }
        return super.getHeadJointOffset(living, partialTick, eye);
    }

    @Override
    public float getHeadPitch(EntityShulker living, float partialTick, int eye)
    {
        return 0F;
    }
}
