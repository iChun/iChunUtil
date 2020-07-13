package me.ichun.mods.ichunutil.client.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.head.HeadBase;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.PhantomModel;
import net.minecraft.client.renderer.entity.model.WolfModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.entity.passive.WolfEntity;

public class HeadPhantom extends HeadBase<PhantomEntity>
{
    public HeadPhantom()
    {
        pupilColour = new float[] { 10F / 255F, 180F / 255F, 5F / 255F };
        eyeOffset = new float[] { 0.5F/16F, 0.5F/16F, 5F/16F };
        halfInterpupillaryDistance = 2.5F / 16F;
        eyeScale = 0.65F;
    }

    @Override
    public float[] getEyeOffsetFromJoint(PhantomEntity living, MatrixStack stack, float partialTick, int eye)
    {
        EntityModel model = ((LivingRenderer)Minecraft.getInstance().getRenderManager().getRenderer(living)).getEntityModel();
        if(model instanceof PhantomModel)
        {
            PhantomModel phantomModel = (PhantomModel)model;
            if(phantomModel.body.childModels.size() > 1)
            {
                translateRotateToChild(stack, phantomModel.body.childModels.get(1)); //this is the real head
            }
        }

        return eyeOffset;
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void setHeadModelFromRenderer(LivingRenderer renderer)
    {
        EntityModel model = renderer.getEntityModel();
        if(model instanceof PhantomModel)
        {
            this.headModel[0] = ((PhantomModel)model).body;
        }
    }
}
