package me.ichun.mods.ichunutil.common.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.common.head.HeadInfo;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.PufferFishBigModel;
import net.minecraft.client.renderer.entity.model.PufferFishMediumModel;
import net.minecraft.client.renderer.entity.model.PufferFishSmallModel;
import net.minecraft.entity.passive.fish.PufferfishEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeadPufferfish extends HeadInfo<PufferfishEntity>
{
    //parent fields are for the BIG pufferfish.
    public float[] eyeOffsetSmall = new float[] { 0F, 2.5F/16F, 1.5F/16F };
    public float[] eyeOffsetMedium = new float[] { 0F, 3.5F/16F, 2.5F/16F};
    public float eyeScaleSmall = 0.65F;
    public float eyeScaleMedium = 0.4F;
    public float interpSmall = 1.5F / 16F;
    public float interpMedium = 2F / 16F;
    public float[] headTopCenterSmall = new float[] { 0F, 8F/16F, 0F };
    public float[] headTopCenterMedium = new float[] { 0F, 8F/16F, 0F };
    public float headScaleSmall = 1F;
    public float headScaleMedium = 1F;

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
    public float[] getHatOffsetFromJoint(PufferfishEntity living, MatrixStack stack, float partialTick, int head)
    {
        int i = living.getPuffState();
        if(i == 0)
        {
            return headTopCenterSmall;
        }
        else if(i == 1)
        {
            return headTopCenterMedium;
        }
        return headTopCenter;
    }

    @Override
    public float getHatScale(PufferfishEntity living, MatrixStack stack, float partialTick, int head)
    {
        int i = living.getPuffState();
        if(i == 0)
        {
            return headScaleSmall;
        }
        else if(i == 1)
        {
            return headScaleMedium;
        }
        return headScale;
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
