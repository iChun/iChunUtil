package me.ichun.mods.ichunutil.client.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.head.HeadBase;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.entity.model.BeeModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.passive.BeeEntity;

public class HeadBee extends HeadBase<BeeEntity>
{
    public float[] irisColourAngry = new float[] { 228F / 255F , 0F / 255F, 24F / 255F };
    public float[] pupilColourAngry = new float[] { 241F / 255F , 242F / 255F, 224F / 255F };

    public HeadBee()
    {
        eyeOffset = new float[] { 0F, -0.5F/16F, 5F/16F };
        halfInterpupillaryDistance = 2.5F/16F;
        eyeScale = 1F;
        irisColour = new float[] { 48F / 255F , 43F / 255F, 55F / 255F };
        pupilColour = new float[] { 124F / 255F , 201F / 255F, 209F / 255F };
    }

    @Override
    public void preChildEntHeadRenderCalls(BeeEntity living, MatrixStack stack, LivingRenderer<BeeEntity, ?> render)
    {
        if(living.isChild()) //I don't like this if statement any more than you do.
        {
            float modelScale = 0.0625F;
            Model model = render.getEntityModel();
            if(model instanceof AgeableModel)
            {
                AgeableModel<?> ageableModel = (AgeableModel<?>)model;
                float f = 1.0F / ageableModel.childBodyScale;
                stack.scale(f, f, f);
                stack.translate(0.0F, ageableModel.childBodyOffsetY * modelScale, 0.0F);
            }
        }
    }

    @Override
    public float[] getIrisColours(BeeEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(living.isAngry())
        {
            return irisColourAngry;
        }
        return irisColour;
    }

    @Override
    public float[] getPupilColours(BeeEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(living.isAngry())
        {
            return pupilColourAngry;
        }
        return pupilColour;
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void setHeadModelFromRenderer(LivingRenderer renderer)
    {
        EntityModel model = renderer.getEntityModel();
        if(model instanceof BeeModel)
        {
            this.headModel[0] = ((BeeModel)model).body;
        }
    }
}
