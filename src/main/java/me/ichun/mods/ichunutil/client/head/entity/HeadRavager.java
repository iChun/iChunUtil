package me.ichun.mods.ichunutil.client.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.head.HeadBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RavagerModel;
import net.minecraft.entity.monster.RavagerEntity;

public class HeadRavager extends HeadBase<RavagerEntity>
{
    public HeadRavager()
    {
        pupilColour = new float[] { 50F / 255F, 102F / 255F, 60F / 255F };
        eyeOffset = new float[] { 0F, 6.5F/16F, 14F/16F };
        halfInterpupillaryDistance = 4F/16F;
        eyeScale = 1.1F;
    }

    @Override
    public float[] getEyeOffsetFromJoint(RavagerEntity living, MatrixStack stack, float partialTick, int eye)
    {
        EntityModel model = ((LivingRenderer)Minecraft.getInstance().getRenderManager().getRenderer(living)).getEntityModel();
        if(model instanceof RavagerModel)
        {
            RavagerModel phantomModel = (RavagerModel)model;
            translateRotateToChild(stack, phantomModel.head); //this is the real head
        }

        return eyeOffset;
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void setHeadModelFromRenderer(LivingRenderer renderer)
    {
        EntityModel model = renderer.getEntityModel();
        if(model instanceof RavagerModel)
        {
            this.headModel[0] = ((RavagerModel)model).neck;
        }
    }
}
