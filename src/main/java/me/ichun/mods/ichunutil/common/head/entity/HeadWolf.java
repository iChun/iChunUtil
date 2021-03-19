package me.ichun.mods.ichunutil.common.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.common.head.HeadInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.WolfModel;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeadWolf extends HeadInfo<WolfEntity>
{
    public float[] eyeOffsetTame = new float[] { -1F/16F, 1F/16F, 2F/16F };
    public float[] irisColourAngry = new float[] { 182F / 255F, 15F / 255F, 15F / 255F };
    public float[] pupilColourAngry = new float[] { 228F / 255F, 46F / 255F, 46F / 255F };

    @Override
    public float getEyeScale(WolfEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(living.isTamed())
        {
            return 0.75F;
        }
        return eyeScale;
    }

    @Override
    public void postHeadTranslation(WolfEntity living, MatrixStack stack, float partialTick)
    {
        super.postHeadTranslation(living, stack, partialTick);

        EntityModel model = ((LivingRenderer)Minecraft.getInstance().getRenderManager().getRenderer(living)).getEntityModel();
        if(model instanceof WolfModel)
        {
            stack.rotate(Vector3f.ZP.rotation(((WolfModel)model).headChild.rotateAngleZ)); //silly workaround
        }
    }

    @Override
    public float[] getEyeOffsetFromJoint(WolfEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(living.isTamed())
        {
            return eyeOffsetTame;
        }
        return super.getEyeOffsetFromJoint(living, stack, partialTick, eye);
    }

    @Override
    public float[] getIrisColours(WolfEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(living.isAngry()) //func_233678_J__
        {
            return irisColourAngry;
        }
        return irisColour;
    }

    @Override
    public float[] getPupilColours(WolfEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(living.isAngry()) //func_233678_J__
        {
            return pupilColourAngry;
        }
        return pupilColour;
    }

    @Override
    public float getHeadRoll(WolfEntity living, float partialTick, int eye, int head)
    {
        return living.getInterestedAngle(partialTick) + living.getShakeAngle(partialTick, 0.0F);
    }
}
