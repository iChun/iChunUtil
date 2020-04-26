package me.ichun.mods.ichunutil.client.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.head.HeadBase;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.monster.ZombiePigmanEntity;

public class HeadPigZombie extends HeadBase<ZombiePigmanEntity>
{
    public float[] eyeOffsetSkin = new float[]{ -0.35F/16F, 4.5F/16F, 4.5F/16F };
    public float eyeScaleSkin = 0.65F;

    public HeadPigZombie()
    {
        headJoint = new float[]{ 0F, 0F, 0F };
        eyeOffset = new float[]{ 0F, 4.5F/16F, 4F/16F };
        halfInterpupillaryDistance = 3F/16F;
        eyeScale = 0.8F;
    }

    @Override
    public float getEyeScale(ZombiePigmanEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(eye == 1 && !living.isChild())
        {
            return eyeScaleSkin;
        }
        return eyeScale;
    }

    @Override
    public float[] getEyeOffsetFromJoint(ZombiePigmanEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(eye == 1 && !living.isChild())
        {
            return eyeOffsetSkin;
        }
        return eyeOffset;
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void setHeadModelFromRenderer(LivingRenderer renderer)
    {
        EntityModel model = renderer.getEntityModel();
        if(model instanceof BipedModel)
        {
            this.headModel[0] = ((BipedModel)model).bipedHead;
        }
    }
}
