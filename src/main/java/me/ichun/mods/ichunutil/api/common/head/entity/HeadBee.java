package me.ichun.mods.ichunutil.api.common.head.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.animal.Bee;


public class HeadBee extends HeadInfo<Bee>
{
    public float[] irisColourAngry = new float[] { 228F / 255F , 0F / 255F, 24F / 255F };
    public float[] pupilColourAngry = new float[] { 241F / 255F , 242F / 255F, 224F / 255F };

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public void preChildEntHeadRenderCalls(Bee living, PoseStack stack, LivingEntityRenderer<Bee, ?> render)
    {
        if(living.isBaby()) //I don't like this if statement any more than you do.
        {
            float modelScale = 0.0625F;
            Model model = render.getModel();
            if(model instanceof AgeableListModel)
            {
                AgeableListModel<?> ageableModel = (AgeableListModel<?>)model;
                float f = 1.0F / ageableModel.babyBodyScale;
                stack.scale(f, f, f);
                stack.translate(0.0F, ageableModel.bodyYOffset * modelScale, 0.0F);
            }
        }
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float[] getCorneaColours(Bee living, PoseStack stack, float partialTick, int eye)
    {
        if(living.isAngry()) //isAngry
        {
            return irisColourAngry;
        }
        return corneaColour;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float[] getIrisColours(Bee living, PoseStack stack, float partialTick, int eye)
    {
        if(living.isAngry()) //isAngry
        {
            return pupilColourAngry;
        }
        return irisColour;
    }
}
