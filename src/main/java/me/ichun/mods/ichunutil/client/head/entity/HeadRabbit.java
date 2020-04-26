package me.ichun.mods.ichunutil.client.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.head.HeadBase;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RabbitModel;
import net.minecraft.entity.passive.RabbitEntity;

public class HeadRabbit extends HeadBase<RabbitEntity>
{
    public HeadRabbit()
    {
        eyeOffset = new float[]{ 0F, 3F/16F, 5F/16F };
        halfInterpupillaryDistance = 1F / 16F;
        eyeScale = 0.6F;
    }

    @Override
    public float[] getHeadJointOffset(RabbitEntity living, MatrixStack stack, float partialTick, int eye)
    {
        float scale = 0.0625F;
        if(living.isChild())
        {
            stack.scale(0.56666666F, 0.56666666F, 0.56666666F);
            stack.translate(0.0F, 22.0F * scale, 2.0F * scale);
        }
        else
        {
            stack.scale(0.6F, 0.6F, 0.6F);
            stack.translate(0.0F, 16.0F * scale, 0.0F);
        }
        return super.getHeadJointOffset(living, stack, partialTick, eye);
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void setHeadModelFromRenderer(LivingRenderer renderer)
    {
        EntityModel model = renderer.getEntityModel();
        if(model instanceof RabbitModel)
        {
            this.headModel[0] = ((RabbitModel)model).rabbitHead;
        }
    }
}
