package me.ichun.mods.ichunutil.client.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.head.HeadBase;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.GhastModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.GhastEntity;

public class HeadGhast extends HeadBase<GhastEntity>
{
    public HeadGhast()
    {
        eyeOffset = new float[]{ 0F, 2F/16F, 8F/16F };
        halfInterpupillaryDistance = 3.5F/16F;
        eyeScale = 1.5F;
    }

    @Override
    public float getEyeScale(GhastEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(living.isAttacking())
        {
            return eyeScale;
        }
        return 0F;
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void setHeadModelFromRenderer(LivingRenderer renderer)
    {
        EntityModel model = renderer.getEntityModel();
        if(model instanceof GhastModel)
        {
            this.headModel[0] = (ModelRenderer)((GhastModel)model).field_228260_b_.get(0);
        }
    }
}
