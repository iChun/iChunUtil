package me.ichun.mods.ichunutil.client.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.head.HeadBase;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.monster.EndermanEntity;

public class HeadEnderman extends HeadBase<EndermanEntity>
{
    public HeadEnderman() //test screaming
    {
        eyeOffset = new float[] { 0F, 3.5F/16F, 4F/16F };
        irisColour = new float[] { 0.88F, 0.47F, 0.98F };
        pupilColour = new float[] { 0.8F, 0F, 0.98F };
        halfInterpupillaryDistance = 2.5F/16F;
        eyeScale = 0.85F;
    }

    @Override
    public float getPupilScale(EndermanEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(living.isScreaming())
        {
            return 0.4F;
        }
        return super.getPupilScale(living, stack, partialTick, eye);
    }

    @Override
    public boolean affectedByInvisibility(EndermanEntity living, int eye)
    {
        return false;
    }

    @Override
    public boolean doesEyeGlow(EndermanEntity living, int eye)
    {
        return true;
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
