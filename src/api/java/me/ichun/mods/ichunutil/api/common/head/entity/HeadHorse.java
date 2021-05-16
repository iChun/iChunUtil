package me.ichun.mods.ichunutil.api.common.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HeadHorse extends HeadInfo<AbstractHorseEntity>
{
    public float[] eyeOffsetNormal = new float[] { 0F, 9.5F/16F, -1F/16F }; //I love that I can use Tabula for this.
    public float halfInterpupillaryDistanceNormal = 3F/16F;
    public float eyeScaleNormal = 0.6F;
    public float[] headTopCenterNormal = new float[] { 0.003125F, 0.6875F, -0.09375F };
    public float headScaleNormal = 0.875F;

    @OnlyIn(Dist.CLIENT)
    @Override
    public float[] getEyeOffsetFromJoint(AbstractHorseEntity living, MatrixStack stack, float partialTick, int eye)
    {
        return HeadInfo.horseEasterEgg.getAsBoolean() ? eyeOffset : eyeOffsetNormal;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getEyeSideOffset(AbstractHorseEntity living, MatrixStack stack, float partialTick, int eye)
    {
        return HeadInfo.horseEasterEgg.getAsBoolean() ? (eye == 0 ? halfInterpupillaryDistance : -halfInterpupillaryDistance) : (eye == 0 ? halfInterpupillaryDistanceNormal : -halfInterpupillaryDistanceNormal);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getEyeScale(AbstractHorseEntity living, MatrixStack stack, float partialTick, int eye)
    {
        return HeadInfo.horseEasterEgg.getAsBoolean() ? eyeScale : eyeScaleNormal;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getEyeRotation(AbstractHorseEntity living, MatrixStack stack, float partialTick, int eye)
    {
        return HeadInfo.horseEasterEgg.getAsBoolean() ? 0F : eye == 0 ? 90F: -90F;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getHeadYaw(AbstractHorseEntity living, MatrixStack stack, float partialTick, int eye, int head)
    {
        return HeadInfo.horseEasterEgg.getAsBoolean() ? 180F : super.getHeadYaw(living, stack, partialTick, eye, head);
    }

    @Override
    public float getHeadYaw(AbstractHorseEntity living, float partialTick, int eye, int head)
    {
        return HeadInfo.horseEasterEgg.getAsBoolean() ? (living.prevRenderYawOffset + (living.renderYawOffset - living.prevRenderYawOffset) * partialTick) - 180F : super.getHeadYaw(living, partialTick, eye, head);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getHeadPitch(AbstractHorseEntity living, MatrixStack stack, float partialTick, int eye, int head)
    {
        return HeadInfo.horseEasterEgg.getAsBoolean() ? (float)Math.toDegrees(living.getRearingAmount(partialTick) * ((float)Math.PI / 4F)) : super.getHeadPitch(living, stack, partialTick, eye, head);
    }

    @Override
    public float getHeadPitch(AbstractHorseEntity living, float partialTick, int eye, int head)
    {
        return HeadInfo.horseEasterEgg.getAsBoolean() ? (float)Math.toDegrees(living.getRearingAmount(partialTick) * ((float)Math.PI / 4F)) : super.getHeadPitch(living, partialTick, eye, head);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getHatScale(AbstractHorseEntity living, MatrixStack stack, float partialTick, int head)
    {
        return HeadInfo.horseEasterEgg.getAsBoolean() ? super.getHatScale(living, stack, partialTick, head) : headScaleNormal;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float[] getHatOffsetFromJoint(AbstractHorseEntity living, MatrixStack stack, float partialTick, int head)
    {
        return HeadInfo.horseEasterEgg.getAsBoolean() ? super.getHatOffsetFromJoint(living, stack, partialTick, head) : headTopCenterNormal;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float[] getHeadArmorOffset(AbstractHorseEntity living, MatrixStack stack, float partialTick, int head)
    {
        if(living instanceof HorseEntity)
        {
            return null;
        }

        return super.getHeadArmorOffset(living, stack, partialTick, head);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getHeadArmorScale(AbstractHorseEntity living, MatrixStack stack, float partialTick, int head)
    {
        if(living instanceof HorseEntity)
        {
            return 1F;
        }

        return super.getHeadArmorScale(living, stack, partialTick, head);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void preChildEntHeadRenderCalls(AbstractHorseEntity living, MatrixStack stack, LivingRenderer render)
    {
        if(HeadInfo.horseEasterEgg.getAsBoolean())
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

    @OnlyIn(Dist.CLIENT)
    @Override
    @SuppressWarnings("rawtypes")
    protected void setHeadModelFromRenderer(LivingRenderer renderer)
    {
        EntityModel model = renderer.getEntityModel();
        if(model instanceof HorseModel)
        {
            this.headModel = HeadInfo.horseEasterEgg.getAsBoolean() ? ((HorseModel)model).body : ((HorseModel)model).head;
        }
    }
}
