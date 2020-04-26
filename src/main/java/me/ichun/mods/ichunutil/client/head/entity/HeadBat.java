package me.ichun.mods.ichunutil.client.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.head.HeadBase;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.BatModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.passive.BatEntity;

public class HeadBat extends HeadBase<BatEntity>
{
    public HeadBat()
    {
        eyeOffset = new float[]{ 0F, 0.5F/16F, 3F/16F };
    }

    @Override
    public float getHeadYaw(BatEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(living.getIsBatHanging())
        {
            return -super.getHeadYaw(living, stack, partialTick, eye);
        }
        else
        {
            return super.getHeadYaw(living, stack, partialTick, eye);
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void setHeadModelFromRenderer(LivingRenderer renderer)
    {
        EntityModel model = renderer.getEntityModel();
        if(model instanceof BatModel)
        {
            this.headModel[0] = ((BatModel)model).batHead;
        }
    }
}
