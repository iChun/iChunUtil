package me.ichun.mods.ichunutil.client.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.head.HeadBase;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.WitherModel;
import net.minecraft.entity.boss.WitherEntity;

public class HeadWither extends HeadBase<WitherEntity>
{
    public float[] headJointSideHeadLeft = new float[] { -10F/16F, -4F/16F, 0F };
    public float[] headJointSideHeadRight = new float[] { 8F/16F, -4F/16F, 0F };
    public float[] eyeOffsetSideHead = new float[] { 1F/16F, 2F/16F, 4F/16F };
    public float halfInterpupillaryDistanceSideHead = 2.5F / 16F;
    public float eyeScaleSideHead = 0.75F;

    public HeadWither()
    {
        headJoint = new float[] { 0F, 0F, 0F };
        eyeOffset = new float[] { 0F, 0F, 4F / 16F };
        halfInterpupillaryDistance = 2F / 16F;
        eyeScale = 1F;
    }

    @Override
    public int getEyeCount(WitherEntity living)
    {
        return 6;
    }

    @Override
    public float getEyeScale(WitherEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(eye > 1)
        {
            return eyeScaleSideHead;
        }
        return eyeScale;
    }

    @Override
    public float getHeadYaw(WitherEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(eye <= 1)
        {
            return super.getHeadYaw(living, stack, partialTick, eye);
        }
        else
        {
            return living.getHeadYRotation(eye <= 3 ? 1 : 0) - (living.prevRenderYawOffset + ((living.renderYawOffset - living.prevRenderYawOffset) * partialTick));
        }
    }

    @Override
    public float getHeadPitch(WitherEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(eye <= 1)
        {
            return super.getHeadPitch(living, stack, partialTick, eye);
        }
        else
        {
            return living.getHeadXRotation(eye <= 3 ? 1 : 0);
        }
    }

    @Override
    public float getEyeSideOffset(WitherEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(eye <= 1)
        {
            return eye % 2 == 0 ? halfInterpupillaryDistance : -halfInterpupillaryDistance;
        }
        else
        {
            return eye % 2 == 0 ? halfInterpupillaryDistanceSideHead : -halfInterpupillaryDistanceSideHead;
        }
    }

    @Override
    public float[] getEyeOffsetFromJoint(WitherEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(eye <= 1)
        {
            return eyeOffset;
        }
        else
        {
            return eyeOffsetSideHead;
        }
    }

    @Override
    public float[] getHeadJointOffset(WitherEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(eye <= 1)
        {
            return super.getHeadJointOffset(living, stack, partialTick, eye);
        }
        else if(eye <= 3)
        {
            return headJointSideHeadLeft;
        }
        else
        {
            return headJointSideHeadRight;
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void setHeadModelFromRenderer(LivingRenderer renderer)
    {
        EntityModel model = renderer.getEntityModel();
        if(model instanceof WitherModel)
        {
            this.headModel = ((WitherModel)model).heads;
        }
    }
}
