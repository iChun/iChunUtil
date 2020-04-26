package me.ichun.mods.ichunutil.client.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.head.HeadBase;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.ParrotModel;
import net.minecraft.entity.passive.ParrotEntity;

public class HeadParrot extends HeadBase<ParrotEntity>
{
    public HeadParrot()
    {
        eyeOffset = new float[]{ 0F, 0.95F/16F, 0.5F/16F };
        halfInterpupillaryDistance = 1F / 16F;
        eyeScale = 0.375F;
        setSideEyed();
    }

    @Override
    public float getPupilScale(ParrotEntity living, MatrixStack stack, float partialTick, int eye)
    {
        return super.getPupilScale(living, stack, partialTick, eye) * (living.isPartying() ? 1.6F : 1F);
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void setHeadModelFromRenderer(LivingRenderer renderer)
    {
        EntityModel model = renderer.getEntityModel();
        if(model instanceof ParrotModel)
        {
            this.headModel[0] = ((ParrotModel)model).head;
        }
    }
}
