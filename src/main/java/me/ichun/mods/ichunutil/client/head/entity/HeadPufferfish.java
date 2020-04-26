package me.ichun.mods.ichunutil.client.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.head.HeadBase;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.*;
import net.minecraft.entity.passive.fish.PufferfishEntity;

public class HeadPufferfish extends HeadBase<PufferfishEntity>
{
    public float[] eyeOffsetSmall = new float[] { 0F, 2.5F/16F, 1.5F/16F };
    public float[] eyeOffsetMedium = new float[] { 0F, 3.5F/16F, 2.5F/16F};
    public float eyeScaleSmall = 0.65F;
    public float eyeScaleMedium = 0.4F;
    public float interpSmall = 1.5F / 16F;
    public float interpMedium = 2F / 16F;

    public HeadPufferfish() // defaults are for BIG
    {
        pupilColour = new float[] { 29F / 255F, 44F / 255F, 28F / 255F };
        eyeOffset = new float[] { 0F/16F, 6.5F/16F, 4F/16F };
        halfInterpupillaryDistance = 2F / 16F;
        eyeScale = 1.25F;
    }

    @Override
    public float getEyeScale(PufferfishEntity living, MatrixStack stack, float partialTick, int eye)
    {
        int i = living.getPuffState();
        if(i == 0)
        {
            return eyeScaleSmall;
        }
        else if(i == 1)
        {
            return eyeScaleMedium;
        }
        return eyeScale;
    }

    @Override
    public float[] getEyeOffsetFromJoint(PufferfishEntity living, MatrixStack stack, float partialTick, int eye)
    {
        int i = living.getPuffState();
        if(i == 0)
        {
            return eyeOffsetSmall;
        }
        else if(i == 1)
        {
            return eyeOffsetMedium;
        }
        return eyeOffset;
    }

    @Override
    public float getEyeSideOffset(PufferfishEntity living, MatrixStack stack, float partialTick, int eye)
    {
        int i = living.getPuffState();
        if(i == 0)
        {
            return eye == 0 ? interpSmall : -interpSmall;
        }
        else if(i == 1)
        {
            return eye == 0 ? interpMedium : -interpMedium;
        }
        return eye == 0 ? halfInterpupillaryDistance : -halfInterpupillaryDistance;
    }

    @Override
    public float getEyeRotation(PufferfishEntity living, MatrixStack stack, float partialTick, int eye)
    {
        int i = living.getPuffState();
        if(i == 0)
        {
            return (eye == 0 ? 30F : -30F);
        }
        return super.getEyeRotation(living, stack, partialTick, eye);
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void setHeadModelFromRenderer(LivingRenderer renderer)
    {
        EntityModel model = renderer.getEntityModel();
        if(model instanceof PufferFishSmallModel)
        {
            this.headModel[0] = ((PufferFishSmallModel)model).body;
        }
        else if(model instanceof PufferFishMediumModel)
        {
            this.headModel[0] = ((PufferFishMediumModel)model).body;
        }
        else if(model instanceof PufferFishBigModel)
        {
            this.headModel[0] = ((PufferFishBigModel)model).body;
        }
    }
}
