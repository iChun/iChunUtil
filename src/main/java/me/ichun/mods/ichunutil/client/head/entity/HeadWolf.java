package me.ichun.mods.ichunutil.client.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.head.HeadBase;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.WolfModel;
import net.minecraft.entity.passive.WolfEntity;

public class HeadWolf extends HeadBase<WolfEntity>
{
    public float[] eyeOffsetTame = new float[] { -1F/16F, 1F/16F, 2F/16F };
    public float[] irisColourAngry = new float[] { 182F / 255F, 15F / 255F, 15F / 255F };
    public float[] pupilColourAngry = new float[] { 228F / 255F, 46F / 255F, 46F / 255F };

    public HeadWolf()
    {
        eyeOffset = new float[] { -1F/16F, 0.5F/16F, 2F/16F };
        eyeScale = 0.65F;
    }

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
    public float[] getEyeOffsetFromJoint(WolfEntity living, MatrixStack stack, float partialTick, int eye)
    {
        EntityModel model = ((LivingRenderer)Minecraft.getInstance().getRenderManager().getRenderer(living)).getEntityModel();
        if(model instanceof WolfModel)
        {
            stack.rotate(Vector3f.ZP.rotation(((WolfModel)model).headChild.rotateAngleZ)); //silly workaround
        }

        if(living.isTamed())
        {
            return eyeOffsetTame;
        }
        return eyeOffset;
    }

    @Override
    public float[] getIrisColours(WolfEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(living.func_233678_J__()) //isAngry
        {
            return irisColourAngry;
        }
        return irisColour;
    }

    @Override
    public float[] getPupilColours(WolfEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(living.func_233678_J__()) //isAngry
        {
            return pupilColourAngry;
        }
        return pupilColour;
    }

    @Override
    public float getHeadRoll(WolfEntity living, MatrixStack stack, float partialTick, int eye)
    {
        return 0F;
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void setHeadModelFromRenderer(LivingRenderer renderer)
    {
        EntityModel model = renderer.getEntityModel();
        if(model instanceof WolfModel)
        {
            this.headModel[0] = ((WolfModel)model).head;
        }
    }
}
