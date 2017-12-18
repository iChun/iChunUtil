package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.passive.AbstractHorse;

public class HeadLlama extends HeadHorse
{
    public HeadLlama()
    {
        eyeOffset = new float[] { 0F, 2F/16F, 8F/16F };
        halfInterpupillaryDistance = 3F/16F;
        eyeScale = 0.9F;
    }

    @Override
    public float getEyeRotation(AbstractHorse living, float partialTick, int eye)
    {
        return 0F;
    }

    @Override
    public float getHeadPitch(AbstractHorse living, float partialTick, int eye)
    {
        eyeOffsetNormal = new float[] { 0F, 14.2F/16F, 6F/16F };
        halfInterpupillaryDistanceNormal = 2.3F/16F;
        eyeScaleNormal = 0.8F;
        return iChunUtil.config.horseEasterEgg == 1 ? 0F : super.getHeadPitch(living, partialTick, eye);
    }

    @Override
    public void preChildEntHeadRenderCalls(AbstractHorse living, RenderLivingBase render)
    {
        if(living.isChild()) //I don't like this if statement any more than you do.
        {
            float modelScale = 0.0625F;
            if(iChunUtil.config.horseEasterEgg == 1)
            {
                GlStateManager.scale(0.625F, 0.45454544F, 0.45454544F);
                GlStateManager.translate(0.0F, 33.0F * modelScale, 0.0F);
            }
            else
            {
                GlStateManager.scale(0.71428573F, 0.64935064F, 0.7936508F);
                GlStateManager.translate(0.0F, 21.0F * modelScale, 0.22F);
            }
        }
    }
}
