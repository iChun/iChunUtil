package me.ichun.mods.ichunutil.client.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.head.HeadBase;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.TropicalFishAModel;
import net.minecraft.client.renderer.entity.model.TropicalFishBModel;
import net.minecraft.entity.passive.fish.TropicalFishEntity;

public class HeadTropicalFish extends HeadBase<TropicalFishEntity>
{
    public float[] eyeOffsetB = new float[] { 0F, -0.5F/16F, 1.5F/16F };

    public HeadTropicalFish() // defaults are for BIG
    {
        eyeOffset = new float[] { 0F/16F, 0F/16F, 2.5F/ 16F };
        halfInterpupillaryDistance = 1F / 16F;
        eyeScale = 0.4F;
        sideEyed = true;
    }

    @Override
    public float[] getEyeOffsetFromJoint(TropicalFishEntity living, MatrixStack stack, float partialTick, int eye)
    {
        return living.getSize() == 0 ? eyeOffset : eyeOffsetB;
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void setHeadModelFromRenderer(LivingRenderer renderer)
    {
        EntityModel model = renderer.getEntityModel();
        if(model instanceof TropicalFishAModel)
        {
            this.headModel[0] = ((TropicalFishAModel)model).body;
        }
        else if(model instanceof TropicalFishBModel)
        {
            this.headModel[0] = ((TropicalFishBModel)model).body;
        }
    }
}
