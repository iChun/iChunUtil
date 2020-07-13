package me.ichun.mods.ichunutil.client.head;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.*;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;
import java.util.function.BooleanSupplier;

/**
 * This is the class I use to track/monitor entity head positions. Feel free to extend this and register your own entities for support.
 * Purely clientside.
 * @param <E>
 */
@OnlyIn(Dist.CLIENT)
public class
HeadBase<E extends LivingEntity> implements Cloneable
{
    public static BooleanSupplier acidEyesBooleanSupplier = () -> false;

    public ModelRenderer[] headModel = null;

    //Defaults. Works on Bipeds
    public float[] headJoint = new float[3];
    public float[] eyeOffset = new float[] { 0F, 4F/16F, 4F/16F }; //I love that I can use Tabula for this. (I still do -iChun 2020)
    public float[] irisColour = new float[] { 0.9F, 0.9F, 0.9F };
    public float[] pupilColour = new float[] { 0.0F, 0.0F, 0.0F };
    public float halfInterpupillaryDistance = 2F/16F;
    public float eyeScale = 0.75F;
    public boolean sideEyed = false;

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

    public HeadBase setIrisColour(float r, float g, float b)
    {
        irisColour = new float[] { r, g, b };
        return this;
    }

    public HeadBase setPupilColour(float r, float g, float b)
    {
        pupilColour = new float[] { r, g, b };
        return this;
    }

    public HeadBase setSideEyed()
    {
        sideEyed = true;
        return this;
    }

    public float[] getHeadJointOffset(E living, MatrixStack stack, float partialTick, int eye)
    {
        headJoint[0] = -(headModel[0].rotationPointX / 16F);
        headJoint[1] = -(headModel[0].rotationPointY / 16F);
        headJoint[2] = -(headModel[0].rotationPointZ / 16F);
        return headJoint;
    }

    public float[] getEyeOffsetFromJoint(E living, MatrixStack stack, float partialTick, int eye)
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

    public float getEyeSideOffset(E living, MatrixStack stack, float partialTick, int eye)
    {
        return eye == 0 ? halfInterpupillaryDistance : -halfInterpupillaryDistance;
    }

    public float getEyeScale(E living, MatrixStack stack, float partialTick, int eye)
    {
        return eyeScale;
    }

    public float getEyeRotation(E living, MatrixStack stack, float partialTick, int eye)
    {
        return sideEyed ? (eye == 0 ? 90F : -90F) : 0F;
    }

    public float getPupilScale(E living, MatrixStack stack, float partialTick, int eye)
    {
        if(acidEyesBooleanSupplier.getAsBoolean() || living.getDataManager().get(LivingEntity.POTION_EFFECTS) > 0)
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

    public float[] getIrisColours(E living, MatrixStack stack, float partialTick, int eye)
    {
        return irisColour;
    }

    public float[] getPupilColours(E living, MatrixStack stack, float partialTick, int eye)
    {
        return pupilColour;
    }

    public float getHeadTopOffsetFromJoint(E living, MatrixStack stack, float partialTick, int eye)
    {
        return headTop;
    }

    public float getHeadFrontOffsetFromJoint(E living, MatrixStack stack, float partialTick, int eye)
    {
        return headFront;
    }

    public float getHeadYaw(E living, MatrixStack stack, float partialTick, int eye)
    {
        return (float)Math.toDegrees(headModel[0].rotateAngleY);
    }

    public float getHeadPitch(E living, MatrixStack stack, float partialTick, int eye)
    {
        return (float)Math.toDegrees(headModel[0].rotateAngleX);
    }

    public float getHeadRoll(E living, MatrixStack stack, float partialTick, int eye)
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

    public void preChildEntHeadRenderCalls(E living, MatrixStack stack, LivingRenderer<E, ?> render)
    {
        if(living.isChild()) //I don't like this if statement any more than you do.
        {
            float modelScale = 0.0625F;
            Model model = render.getEntityModel();
            if(model instanceof BipedModel)
            {
                stack.scale(0.75F, 0.75F, 0.75F);
                stack.translate(0.0F, 16.0F * modelScale, 0.0F);
            }
            else if(model instanceof AgeableModel)
            {
                AgeableModel<?> ageableModel = (AgeableModel<?>)model;
                if(ageableModel.isChildHeadScaled)
                {
                    float f = 1.5F / ageableModel.childHeadScale;
                    stack.scale(f, f, f);
                }
                stack.translate(0.0F, ageableModel.childHeadOffsetY * modelScale, ageableModel.childHeadOffsetZ * modelScale);
            }
        }
    }

    public void translateRotateToChild(MatrixStack stack, ModelRenderer renderer)
    {
        stack.translate(renderer.rotationPointX / 16F, renderer.rotationPointY / 16F, renderer.rotationPointZ / 16F);

        stack.rotate(Vector3f.ZP.rotation(renderer.rotateAngleZ));
        stack.rotate(Vector3f.YP.rotation(renderer.rotateAngleY));
        stack.rotate(Vector3f.XP.rotation(renderer.rotateAngleX));
    }

    @Override
    public HeadBase<?> clone()
    {
        try
        {
            HeadBase<?> clone = (HeadBase<?>)super.clone();
            clone.headModel = null;
            clone.livingRand = new Random();

            for(int i = 0; i < 3; i++)
            {
                clone.headJoint[i] = this.headJoint[i];
                clone.eyeOffset[i] = this.eyeOffset[i];
                clone.irisColour[i] = this.irisColour[i];
                clone.pupilColour[i] = this.pupilColour[i];
            }
            clone.halfInterpupillaryDistance = this.halfInterpupillaryDistance;
            clone.eyeScale = this.eyeScale;
            clone.sideEyed = this.sideEyed;

            clone.headTop = this.headTop;
            clone.headFront = this.headFront;
            return clone;
        }
        catch(Exception e)
        {
            iChunUtil.LOGGER.error("Error cloning class: {}", this.getClass());
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    public void setHeadModel(LivingRenderer renderer) //actually gets the most parent ModelRenderer. we translate to the head in the functions if necessary.
    {
        if(this.headModel == null || iChunUtil.configClient.aggressiveHeadTracking == 1 || iChunUtil.configClient.aggressiveHeadTracking == 2 && renderer instanceof PlayerRenderer)
        {
            this.headModel = new ModelRenderer[1];
            setHeadModelFromRenderer(renderer);
            for(ModelRenderer head : this.headModel)
            {
                if(head == null)
                {
                    this.headModel = null;
                    break;
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    protected void setHeadModelFromRenderer(LivingRenderer renderer)
    {
        EntityModel model = renderer.getEntityModel();
        //general models
        if(model instanceof BipedModel)
        {
            this.headModel[0] = ((BipedModel)model).bipedHead;
        }
        else if(model instanceof QuadrupedModel)
        {
            this.headModel[0] = ((QuadrupedModel)model).headModel;
        }
        //specific models
        else if(model instanceof BlazeModel)
        {
            this.headModel[0] = ((BlazeModel)model).blazeHead;
        }
        else if(model instanceof ChickenModel)
        {
            this.headModel[0] = ((ChickenModel)model).head;
        }
        else if(model instanceof CodModel)
        {
            this.headModel[0] = ((CodModel)model).head;
        }
        else if(model instanceof CreeperModel)
        {
            this.headModel[0] = ((CreeperModel)model).head;
        }
        else if(model instanceof DolphinModel)
        {
            this.headModel[0] = ((DolphinModel)model).body;
        }
        else if(model instanceof FoxModel)
        {
            this.headModel[0] = ((FoxModel)model).head;
        }
        else if(model instanceof IllagerModel)
        {
            this.headModel[0] = ((IllagerModel)model).head;
        }
        else if(model instanceof IronGolemModel)
        {
            this.headModel[0] = ((IronGolemModel)model).ironGolemHead;
        }
        else if(model instanceof OcelotModel)
        {
            this.headModel[0] = ((OcelotModel)model).ocelotHead;
        }
        else if(model instanceof SalmonModel)
        {
            this.headModel[0] = ((SalmonModel)model).head;
        }
        else if(model instanceof SilverfishModel)
        {
            this.headModel[0] = ((SilverfishModel)model).silverfishBodyParts[0];
        }
        else if(model instanceof SlimeModel)
        {
            this.headModel[0] = ((SlimeModel)model).slimeBodies;
        }
        else if(model instanceof SquidModel)
        {
            this.headModel[0] = ((SquidModel)model).body;
        }
        else if(model instanceof VillagerModel)
        {
            this.headModel[0] = ((VillagerModel)model).villagerHead;
        }
    }
}
