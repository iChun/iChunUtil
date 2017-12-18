package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.passive.EntityWolf;

public class HeadWolf extends HeadBase<EntityWolf>
{
    public float[] eyeOffsetTame = new float[] { -1F/16F, 1F/16F, 2F/16F };
    public float[] irisColourAngry = new float[] { 182F / 255F, 15F / 255F, 15F / 255F };
    public float[] pupilColourAngry = new float[] { 228F / 255F, 46F / 255F, 46F / 255F };

    public HeadWolf()
    {
        eyeOffset = new float[] { -1F/16F, 0.5F/16F, 2F/16F };
        eyeScale = 0.65F;
    }

    @Override
    public float getEyeScale(EntityWolf living, float partialTick, int eye)
    {
        if(living.isTamed())
        {
            return 0.75F;
        }
        return eyeScale;
    }

    @Override
    public float[] getEyeOffsetFromJoint(EntityWolf living, float partialTick, int eye)
    {
        if(living.isTamed())
        {
            return eyeOffsetTame;
        }
        return eyeOffset;
    }

    @Override
    public float[] getIrisColours(EntityWolf living, float partialTick, int eye)
    {
        if(living.isAngry())
        {
            return irisColourAngry;
        }
        return irisColour;
    }

    @Override
    public float[] getPupilColours(EntityWolf living, float partialTick, int eye)
    {
        if(living.isAngry())
        {
            return pupilColourAngry;
        }
        return pupilColour;
    }

    @Override
    public void preChildEntHeadRenderCalls(EntityWolf living, RenderLivingBase render)
    {
        if(living.isChild()) //I don't like this if statement any more than you do.
        {
            float modelScale = 0.0625F;
            GlStateManager.translate(0.0F, 5.0F * modelScale, 2.0F * modelScale);
        }
    }
}
