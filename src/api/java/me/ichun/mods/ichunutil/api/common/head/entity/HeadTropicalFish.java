package me.ichun.mods.ichunutil.api.common.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.TropicalFishAModel;
import net.minecraft.client.renderer.entity.model.TropicalFishBModel;
import net.minecraft.entity.passive.fish.TropicalFishEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HeadTropicalFish extends HeadInfo<TropicalFishEntity>
{
    public float[] eyeOffsetB = new float[] { 0F, -0.5F/16F, 1.5F/16F };
    public float[] headTopCenterB = new float[] { 0F, 3F/16F, 2F/16F };

    @OnlyIn(Dist.CLIENT)
    @Override
    public float[] getEyeOffsetFromJoint(TropicalFishEntity living, MatrixStack stack, float partialTick, int eye)
    {
        return living.getSize() == 0 ? eyeOffset : eyeOffsetB;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float[] getHatOffsetFromJoint(TropicalFishEntity living, MatrixStack stack, float partialTick, int head)
    {
        return living.getSize() == 0 ? headTopCenter : headTopCenterB;
    }

    @OnlyIn(Dist.CLIENT)
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
