package me.ichun.mods.ichunutil.client.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.head.HeadBase;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;

public class HeadHorse extends HeadBase<AbstractHorseEntity>
{
    public float[] eyeOffsetNormal = new float[] { 0F, 9.5F/16F, -1F/16F }; //I love that I can use Tabula for this.
    public float halfInterpupillaryDistanceNormal = 3F/16F;
    public float eyeScaleNormal = 0.6F;

    public HeadHorse()
    {
        eyeOffset = new float[] { 0F, 6F/16F, 5F/16F };
        halfInterpupillaryDistance = 3F/16F;
        eyeScale = 0.9F;
    }

    @Override
    public float[] getEyeOffsetFromJoint(AbstractHorseEntity living, MatrixStack stack, float partialTick, int eye)
    {
        return iChunUtil.configClient.horseEasterEgg ? eyeOffset : eyeOffsetNormal;
    }

    @Override
    public float getEyeSideOffset(AbstractHorseEntity living, MatrixStack stack, float partialTick, int eye)
    {
        return iChunUtil.configClient.horseEasterEgg ? (eye == 0 ? halfInterpupillaryDistance : -halfInterpupillaryDistance) : (eye == 0 ? halfInterpupillaryDistanceNormal : -halfInterpupillaryDistanceNormal);
    }

    @Override
    public float getEyeScale(AbstractHorseEntity living, MatrixStack stack, float partialTick, int eye)
    {
        return iChunUtil.configClient.horseEasterEgg ? eyeScale : eyeScaleNormal;
    }

    @Override
    public float getEyeRotation(AbstractHorseEntity living, MatrixStack stack, float partialTick, int eye)
    {
        return iChunUtil.configClient.horseEasterEgg ? 0F : eye == 0 ? 90F: -90F;
    }

    @Override
    public float getHeadYaw(AbstractHorseEntity living, MatrixStack stack, float partialTick, int eye)
    {
        return iChunUtil.configClient.horseEasterEgg ? 180F : super.getHeadYaw(living, stack, partialTick, eye);
    }

    @Override
    public float getHeadPitch(AbstractHorseEntity living, MatrixStack stack, float partialTick, int eye)
    {
        return iChunUtil.configClient.horseEasterEgg ? (float)Math.toDegrees(living.getRearingAmount(partialTick) * ((float)Math.PI / 4F)) : super.getHeadPitch(living, stack, partialTick, eye);
    }

    @Override
    public void preChildEntHeadRenderCalls(AbstractHorseEntity living, MatrixStack stack, LivingRenderer render)
    {
        if(iChunUtil.configClient.horseEasterEgg)
        {
            if(living.isChild()) //I don't like this if statement any more than you do.
            {
                float f1 = 0.5F;

                stack.scale(f1, f1, f1);
                stack.translate(0.0F, 20F / 16F, 0.0F);
            }
        }
        else
        {
            super.preChildEntHeadRenderCalls(living, stack, render);
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void setHeadModelFromRenderer(LivingRenderer renderer)
    {
        EntityModel model = renderer.getEntityModel();
        if(model instanceof HorseModel)
        {
            this.headModel[0] = iChunUtil.configClient.horseEasterEgg ? ((HorseModel)model).body : ((HorseModel)model).head;
        }
    }
}
