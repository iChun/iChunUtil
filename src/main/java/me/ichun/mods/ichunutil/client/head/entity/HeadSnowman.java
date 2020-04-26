package me.ichun.mods.ichunutil.client.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.head.HeadBase;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.SnowManModel;
import net.minecraft.entity.passive.SnowGolemEntity;

public class HeadSnowman extends HeadBase<SnowGolemEntity>
{
    public float[] eyeOffsetNoPumpkinLeft = new float[] { 0F, 5.5F/16F, 3.5F/16F };
    public float[] eyeOffsetNoPumpkinRight = new float[] { 0F, 6F/16F, 3.5F/16F };
    public HeadSnowman()
    {
        eyeOffset = new float[] { 0F, 7.5F/16F, 5F/16F };
        halfInterpupillaryDistance = 1.5F / 16F;
        eyeScale = 1F;
    }

    @Override
    public float[] getEyeOffsetFromJoint(SnowGolemEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(living.isPumpkinEquipped())
        {
            return eyeOffset;
        }
        else
        {
            if(eye == 0)
            {
                return eyeOffsetNoPumpkinRight;
            }
            return eyeOffsetNoPumpkinLeft;
        }
    }

    @Override
    public float getEyeScale(SnowGolemEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(living.isPumpkinEquipped())
        {
            return eyeScale;
        }
        else
        {
            return eye == 0 ? 0.65F : 0.75F;
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void setHeadModelFromRenderer(LivingRenderer renderer)
    {
        EntityModel model = renderer.getEntityModel();
        if(model instanceof SnowManModel)
        {
            this.headModel[0] = ((SnowManModel)model).head;
        }
    }
}
