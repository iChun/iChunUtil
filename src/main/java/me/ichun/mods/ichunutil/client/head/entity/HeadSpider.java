package me.ichun.mods.ichunutil.client.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.head.HeadBase;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.SpiderModel;
import net.minecraft.entity.monster.SpiderEntity;

public class HeadSpider extends HeadBase<SpiderEntity>
{
    public float halfInterpupillaryDistance2 = 2F / 16F;
    public float halfInterpupillaryDistance3 = 4F / 16F;
    public float[] eyeOffset2 = new float[] { 0F, 3F/16F, 8F / 16F };
    public float[] eyeOffset3 = new float[] { 0F, 1F/16F, 7.5F / 16F };

    public HeadSpider()
    {
        eyeOffset = new float[] { 0F, 0F, 8F / 16F };
        irisColour = new float[] { 0.8F, 0F, 0F };
        halfInterpupillaryDistance = 1F / 16F;
        eyeScale = 0.8F;
    }

    @Override
    public int getEyeCount(SpiderEntity living)
    {
        return 6;
    }

    @Override
    public float getEyeSideOffset(SpiderEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(eye <= 1)
        {
            return eye % 2 == 0 ? halfInterpupillaryDistance : -halfInterpupillaryDistance;
        }
        else if(eye <= 3)
        {
            return eye % 2 == 0 ? halfInterpupillaryDistance2 : -halfInterpupillaryDistance2;
        }
        else
        {
            return eye % 2 == 0 ? halfInterpupillaryDistance3 : -halfInterpupillaryDistance3;
        }
    }

    @Override
    public float getEyeRotation(SpiderEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(eye >= 4)
        {
            return eye % 2 == 0 ? 45F : -45F;
        }
        return 0F;
    }

    @Override
    public float[] getEyeOffsetFromJoint(SpiderEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(eye <= 1)
        {
            return eyeOffset;
        }
        else if(eye <= 3)
        {
            return eyeOffset2;
        }
        else
        {
            return eyeOffset3;
        }
    }

    @Override
    public boolean affectedByInvisibility(SpiderEntity living, int eye)
    {
        return false;
    }

    @Override
    public boolean doesEyeGlow(SpiderEntity living, int eye)
    {
        return true;
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void setHeadModelFromRenderer(LivingRenderer renderer)
    {
        EntityModel model = renderer.getEntityModel();
        if(model instanceof SpiderModel)
        {
            this.headModel[0] = ((SpiderModel)model).spiderHead;
        }
    }
}
