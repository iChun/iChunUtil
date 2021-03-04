package me.ichun.mods.ichunutil.common.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.common.head.HeadInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.PhantomModel;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeadPhantom extends HeadInfo<PhantomEntity>
{
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
}
