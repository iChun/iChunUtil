package me.ichun.mods.ichunutil.common.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.common.head.HeadInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RavagerModel;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeadRavager extends HeadInfo<RavagerEntity>
{
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
}
