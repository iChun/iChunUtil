package me.ichun.mods.ichunutil.api.client.head;

import net.minecraft.client.model.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;
import java.util.Random;
import java.util.function.BooleanSupplier;

/**
 * This is the class I use to track/monitor entity head positions. Feel free to extend this and register your own entities for support.
 * Purely clientside.
 * @param <E>
 */
@SideOnly(Side.CLIENT)
public class HeadBase<E extends EntityLivingBase>
{
    public static BooleanSupplier acidEyesBooleanSupplier = () -> false;

    public ModelRenderer[] headModel = null;
    public Object headInfo = null;

    //Defaults. Works on Bipeds
    public float[] headJoint = new float[3];
    public float[] eyeOffset = new float[] { 0F, 4F/16F, 4F/16F }; //I love that I can use Tabula for this.
    public float[] irisColour = new float[] { 0.9F, 0.9F, 0.9F };
    public float[] pupilColour = new float[] { 0.0F, 0.0F, 0.0F };
    public float halfInterpupillaryDistance = 2F/16F;
    public float eyeScale = 0.75F;

    //Stuff for hats
    public float headTop = 8F/16F;
    public float headFront = 4F/16F;

    public Random livingRand = new Random();
    public int[] acidTime;

    public HeadBase setHeadJoint(float jointX, float jointY, float jointZ)
    {
        headJoint = new float[] { jointX, jointY, jointZ };
        return this;
    }

    public HeadBase setEyeOffset(float offsetX, float offsetY, float offsetZ)
    {
        eyeOffset = new float[] { offsetX, offsetY, offsetZ };
        return this;
    }

    public HeadBase setHalfInterpupillaryDistance(float dist)
    {
        halfInterpupillaryDistance = dist;
        return this;
    }

    public HeadBase setEyeScale(float scale)
    {
        eyeScale = scale;
        return this;
    }

    public float[] getHeadJointOffset(E living, float partialTick, int eye)
    {
        headJoint[0] = -(headModel[0].rotationPointX / 16F);
        headJoint[1] = -(headModel[0].rotationPointY / 16F);
        headJoint[2] = -(headModel[0].rotationPointZ / 16F);
        return headJoint;
    }

    public float[] getEyeOffsetFromJoint(E living, float partialTick, int eye)
    {
        return eyeOffset;
    }

    public int getEyeCount(E living)
    {
        return 2;
    }

    public float maxEyeSizeGrowth(E living, int eye)
    {
        return 0F;
    }

    public float getEyeSideOffset(E living, float partialTick, int eye)
    {
        return eye == 0 ? halfInterpupillaryDistance : -halfInterpupillaryDistance;
    }

    public float getEyeScale(E living, float partialTick, int eye)
    {
        return eyeScale;
    }

    public float getEyeRotation(E living, float partialTick, int eye)
    {
        return 0F;
    }

    public float getPupilScale(E living, float partialTick, int eye)
    {
        if(acidEyesBooleanSupplier.getAsBoolean() || living.getDataManager().get(EntityLivingBase.POTION_EFFECTS) > 0)
        {
            livingRand.setSeed(Math.abs(living.hashCode()) * 1000);
            int eyeCount = getEyeCount(living);
            if(acidTime == null || acidTime.length < eyeCount)
            {
                acidTime = new int[eyeCount];
            }
            for(int i = 0; i < eyeCount; i++)
            {
                acidTime[i] = 20 + livingRand.nextInt(20);
            }
            return 0.3F + ((float)Math.sin(Math.toRadians((living.ticksExisted + partialTick) / acidTime[eye] * 360F)) + 1F) / 2F;
        }
        return 1F + (0.35F * (living.deathTime + partialTick) / 20F);
    }

    public float[] getIrisColours(E living, float partialTick, int eye)
    {
        return irisColour;
    }

    public float[] getPupilColours(E living, float partialTick, int eye)
    {
        return pupilColour;
    }

    public float getHeadTopOffsetFromJoint(E living, float partialTick, int eye)
    {
        return headTop;
    }

    public float getHeadFrontOffsetFromJoint(E living, float partialTick, int eye)
    {
        return headFront;
    }

    public float getHeadYaw(E living, float partialTick, int eye)
    {
        return (float)Math.toDegrees(headModel[0].rotateAngleY);
    }

    public float getHeadPitch(E living, float partialTick, int eye)
    {
        return (float)Math.toDegrees(headModel[0].rotateAngleX);
    }

    public float getHeadRoll(E living, float partialTick, int eye)
    {
        return (float)Math.toDegrees(headModel[0].rotateAngleZ);
    }

    public boolean affectedByInvisibility(E living, int eye)
    {
        return true;
    }

    public boolean doesEyeGlow(E living, int eye)
    {
        return false;
    }

    public void preChildEntHeadRenderCalls(E living, RenderLivingBase render)
    {
        if(living.isChild()) //I don't like this if statement any more than you do.
        {
            float modelScale = 0.0625F;
            ModelBase model = render.getMainModel();
            if(model instanceof ModelBiped)
            {
                GlStateManager.scale(0.75F, 0.75F, 0.75F);
                GlStateManager.translate(0.0F, 16.0F * modelScale, 0.0F);
            }
            else if(model instanceof ModelQuadruped)
            {
                if(model instanceof ModelPolarBear)
                {
                    GlStateManager.scale(0.6666667F, 0.6666667F, 0.6666667F);
                }
                GlStateManager.translate(0.0F, ((ModelQuadruped)model).childYOffset * modelScale, ((ModelQuadruped)model).childZOffset * modelScale);
            }
            else if(model instanceof ModelChicken)
            {
                GlStateManager.translate(0.0F, 5.0F * modelScale, 2.0F * modelScale);
            }
        }
    }

    public HeadBase clone()
    {
        try
        {
            HeadBase clone = this.getClass().newInstance();
            for(int i = 0; i < 3; i++)
            {
                clone.headJoint[i] = this.headJoint[i];
                clone.eyeOffset[i] = this.eyeOffset[i];
            }
            clone.halfInterpupillaryDistance = this.halfInterpupillaryDistance;
            clone.eyeScale = this.eyeScale;
            return clone;
        }
        catch(Exception e){}
        return new HeadBase();
    }

    @SuppressWarnings("unchecked")
    public static void registerHeadHelper(Class<? extends EntityLivingBase> clz, HeadBase base)
    {
        try
        {
            Class clzz = Class.forName("me.ichun.mods.ichunutil.client.entity.head.HeadHandler");
            Map modelOffsetHelpers = (Map)ObfuscationReflectionHelper.getPrivateValue(clzz, null, "modelOffsetHelpers");
            modelOffsetHelpers.put(clz, base);
        }
        catch(Exception e)
        {
            System.out.println("Error registering head helper class: " + clz.getName());
            e.printStackTrace();
        }
    }
}
