package me.ichun.mods.ichunutil.client.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.head.HeadBase;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.MagmaCubeModel;
import net.minecraft.entity.monster.MagmaCubeEntity;

public class HeadMagmaCube extends HeadBase<MagmaCubeEntity>
{
    public HeadMagmaCube()
    {
        eyeOffset = new float[]{ 0F, -19F/16F, 4F/16F };
    }

    @Override
    public float getEyeScale(MagmaCubeEntity living, MatrixStack stack, float partialTick, int eye)
    {
        float squishFactor = living.prevSquishFactor + (living.squishFactor - living.prevSquishFactor) * partialTick;
        if(squishFactor <= 0F)
        {
            return eyeScale;
        }
        else
        {
            return eyeScale + squishFactor * 1.5F;
        }
    }

    @Override
    public float[] getHeadJointOffset(MagmaCubeEntity living, MatrixStack stack, float partialTick, int eye)
    {
        float squishFactor = living.prevSquishFactor + (living.squishFactor - living.prevSquishFactor) * partialTick;
        if(squishFactor <= 0F)
        {
            return super.getHeadJointOffset(living, stack, partialTick, eye);
        }
        else
        {
            return new float[]{ 0F, -(0 - squishFactor * 2.5F)/16F, 0F };
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void setHeadModelFromRenderer(LivingRenderer renderer)
    {
        EntityModel model = renderer.getEntityModel();
        if(model instanceof MagmaCubeModel)
        {
            this.headModel[0] = ((MagmaCubeModel)model).core;
        }
    }
}
