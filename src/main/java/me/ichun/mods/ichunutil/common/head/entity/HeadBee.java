package me.ichun.mods.ichunutil.common.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.common.head.HeadInfo;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeadBee extends HeadInfo<BeeEntity>
{
    public float[] irisColourAngry = new float[] { 228F / 255F , 0F / 255F, 24F / 255F };
    public float[] pupilColourAngry = new float[] { 241F / 255F , 242F / 255F, 224F / 255F };

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
        if(living.isAngry()) //func_233678_J__
        {
            return irisColourAngry;
        }
        return irisColour;
    }

    @Override
    public float[] getPupilColours(BeeEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(living.isAngry()) //func_233678_J__
        {
            return pupilColourAngry;
        }
        return pupilColour;
    }
}
