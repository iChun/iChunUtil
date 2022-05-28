package me.ichun.mods.ichunutil.api.common.head.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.animal.Wolf;


public class HeadWolf extends HeadInfo<Wolf>
{
    public float[] eyeOffsetTame = new float[] { -1F/16F, 1F/16F, 2F/16F };
    public float[] irisColourAngry = new float[] { 182F / 255F, 15F / 255F, 15F / 255F };
    public float[] pupilColourAngry = new float[] { 228F / 255F, 46F / 255F, 46F / 255F };

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getEyeScale(Wolf living, PoseStack stack, float partialTick, int eye)
    {
        if(living.isTame())
        {
            return 0.75F;
        }
        return eyeScale;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public void postHeadTranslation(Wolf living, PoseStack stack, float partialTick)
    {
        super.postHeadTranslation(living, stack, partialTick);

        EntityModel model = ((LivingEntityRenderer)Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(living)).getModel();
        if(model instanceof WolfModel)
        {
            stack.mulPose(Vector3f.ZP.rotation(((WolfModel)model).realHead.zRot)); //silly workaround
        }
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float[] getEyeOffsetFromJoint(Wolf living, PoseStack stack, float partialTick, int eye)
    {
        if(living.isTame())
        {
            return eyeOffsetTame;
        }
        return super.getEyeOffsetFromJoint(living, stack, partialTick, eye);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float[] getCorneaColours(Wolf living, PoseStack stack, float partialTick, int eye)
    {
        if(living.isAngry()) //isAngry
        {
            return irisColourAngry;
        }
        return corneaColour;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float[] getIrisColours(Wolf living, PoseStack stack, float partialTick, int eye)
    {
        if(living.isAngry()) //isAngry
        {
            return pupilColourAngry;
        }
        return irisColour;
    }

    @Override
    public float getHeadRoll(Wolf living, float partialTick, int head, int eye)
    {
        return living.getHeadRollAngle(partialTick) + living.getBodyRollAngle(partialTick, 0.0F);
    }
}
