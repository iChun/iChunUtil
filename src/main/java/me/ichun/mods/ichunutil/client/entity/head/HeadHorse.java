package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.passive.AbstractHorse;

public class HeadHorse extends HeadBase<AbstractHorse>
{
    public float[] eyeOffsetNormal = new float[] { 0F, 8.5F/16F, -1.5F/16F }; //I love that I can use Tabula for this.
    public float halfInterpupillaryDistanceNormal = 2.5F/16F;
    public float eyeScaleNormal = 0.6F;

    public HeadHorse()
    {
        eyeOffset = new float[] { 0F, 6F/16F, 5F/16F };
        halfInterpupillaryDistance = 3F/16F;
        eyeScale = 0.9F;
    }

    @Override
    public float[] getEyeOffsetFromJoint(AbstractHorse living, float partialTick, int eye)
    {
        return iChunUtil.config.horseEasterEgg == 1 ? eyeOffset : eyeOffsetNormal;
    }

    @Override
    public float getEyeSideOffset(AbstractHorse living, float partialTick, int eye)
    {
        return iChunUtil.config.horseEasterEgg == 1 ? (eye == 0 ? halfInterpupillaryDistance : -halfInterpupillaryDistance) : (eye == 0 ? halfInterpupillaryDistanceNormal : -halfInterpupillaryDistanceNormal);
    }

    @Override
    public float getEyeScale(AbstractHorse living, float partialTick, int eye)
    {
        return iChunUtil.config.horseEasterEgg == 1 ? eyeScale : eyeScaleNormal;
    }

    @Override
    public float getEyeRotation(AbstractHorse living, float partialTick, int eye)
    {
        return iChunUtil.config.horseEasterEgg == 1 ? 0F : eye == 0 ? 90F: -90F;
    }

    @Override
    public float getHeadYaw(AbstractHorse living, float partialTick, int eye)
    {
        return iChunUtil.config.horseEasterEgg == 1 ? 180F : super.getHeadYaw(living, partialTick, eye);
    }

    @Override
    public float getHeadPitch(AbstractHorse living, float partialTick, int eye)
    {
        return iChunUtil.config.horseEasterEgg == 1 ? (float)Math.toDegrees(living.getRearingAmount(partialTick) * ((float)Math.PI / 4F)) : super.getHeadPitch(living, partialTick, eye);
    }

    @Override
    public void preChildEntHeadRenderCalls(AbstractHorse living, RenderLivingBase render)
    {
        if(living.isChild()) //I don't like this if statement any more than you do.
        {
            float f1 = living.getHorseSize();

            if(iChunUtil.config.horseEasterEgg == 1)
            {
                GlStateManager.scale(f1, f1, f1);
                GlStateManager.translate(0.0F, 1.35F * (1.0F - f1), 0.0F);
            }
            else
            {
                float f = living.getGrassEatingAmount(0.0F);
                float f2 = 0.5F + f1 * f1 * 0.5F;
                GlStateManager.scale(f2, f2, f2);

                if (f <= 0.0F)
                {
                    GlStateManager.translate(0.0F, 1.35F * (1.0F - f1), 0.0F);
                }
                else
                {
                    GlStateManager.translate(0.0F, 0.9F * (1.0F - f1) * f + 1.35F * (1.0F - f1) * (1.0F - f), 0.15F * (1.0F - f1) * f);
                }
            }
        }
    }
}
