package me.ichun.mods.ichunutil.client.entity.head;

import net.minecraft.client.model.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Random;
import java.util.function.BooleanSupplier;

public class HeadBase<E extends EntityLivingBase>
{
    public static BooleanSupplier acidEyesBooleanSupplier = () -> false;
    public static BooleanSupplier horseHeadIsBody = () -> true;

    public ModelRenderer[] headModel = null;

    //Defaults. Works on Bipeds
    public float[] headJoint = new float[3];
    public float[] eyeOffset = new float[] { 0F, 4F/16F, 4F/16F }; //I love that I can use Tabula for this.
    public float[] irisColour = new float[] { 0.9F, 0.9F, 0.9F };
    public float[] pupilColour = new float[] { 0.0F, 0.0F, 0.0F };
    public float halfInterpupillaryDistance = 2F/16F;
    public float eyeScale = 0.75F;

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
        return 1F;
    }

    public float[] getIrisColours(E living, float partialTick, int eye)
    {
        return irisColour;
    }

    public float[] getPupilColours(E living, float partialTick, int eye)
    {
        return pupilColour;
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

    public float getHeadYawForTracker(E living, int eye)
    {
        return getHeadYawForTracker(living);
    }

    public float getHeadPitchForTracker(E living, int eye)
    {
        return getHeadPitchForTracker(living);
    }

    public float getHeadRollForTracker(E living, int eye)
    {
        return getHeadRollForTracker(living);
    }

    public float getHeadYawForTracker(E living)
    {
        return living.rotationYawHead;
    }

    public float getHeadPitchForTracker(E living)
    {
        return living.rotationPitch;
    }

    public float getHeadRollForTracker(E living)
    {
        return 0F;
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

    public static HashMap<Class<? extends EntityLivingBase>, HeadBase> modelOffsetHelpers = new HashMap<Class<? extends EntityLivingBase>, HeadBase>() {{
        put(AbstractHorse.class, new HeadHorse());
        put(AbstractIllager.class, new HeadBase().setEyeOffset(0F, 3.2F/16F, 4F/16F).setHalfInterpupillaryDistance(1.9F / 16F).setEyeScale(0.7F));
        put(AbstractSkeleton.class, new HeadBiped());
        put(EntityPlayer.class, new HeadPlayer());
        put(EntityBat.class, new HeadBat());
        put(EntityBlaze.class, new HeadBase().setEyeOffset(0F, 0F, 4F/16F));
        put(EntityChicken.class, new HeadBase().setEyeOffset(0F, 4.5F/16F, 2F/16F).setHalfInterpupillaryDistance(1.5F / 16F).setEyeScale(0.375F));
        put(EntityCow.class, new HeadBase().setEyeOffset(0F, 1F/16F, 6F/16F).setHalfInterpupillaryDistance(3F / 16F));
        put(EntityCreeper.class, new HeadBase().setEyeOffset(0F, 5F/16F, 4F/16F)); //make creeper maaaaaaad with narrowing pupils
        put(EntityDragon.class, new HeadDragon());
        put(EntityElderGuardian.class, new HeadElderGuardian());
        put(EntityEnderman.class, new HeadEnderman());
        put(EntityEndermite.class, new HeadEndermite());
        put(EntityGhast.class, new HeadGhast());
        put(EntityGuardian.class, new HeadGuardian());
        put(EntityIronGolem.class, new HeadBase().setEyeOffset(0F, 6F/16F, 5.5F/16F));
        put(EntityLlama.class, new HeadLlama());
        put(EntityMagmaCube.class, new HeadMagmaCube());
        put(EntityMooshroom.class, new HeadBase().setEyeOffset(0F, 1F/16F, 6F/16F).setHalfInterpupillaryDistance(3F / 16F));
        put(EntityOcelot.class, new HeadOcelot());
        put(EntityParrot.class, new HeadParrot());
        put(EntityPig.class, new HeadBase().setEyeOffset(0F, 0.5F/16F, 8F/16F).setHalfInterpupillaryDistance(3F/16F));
        put(EntityPigZombie.class, new HeadPigZombie());
        put(EntityPolarBear.class, new HeadBase().setEyeOffset(0F, -0.5F/16F, 3F/16F).setEyeScale(0.4F));
        put(EntityRabbit.class, new HeadRabbit());
        put(EntitySheep.class, new HeadSheep());
        put(EntityShulker.class, new HeadShulker());
        put(EntitySilverfish.class, new HeadSilverfish());
        put(EntitySlime.class, new HeadSlime());
        put(EntitySnowman.class, new HeadSnowman());
        put(EntitySpider.class, new HeadSpider());
        put(EntitySquid.class, new HeadSquid());
        put(EntityVex.class, new HeadBiped());
        put(EntityVillager.class, new HeadBase().setEyeOffset(0F, 3.2F/16F, 4F/16F).setHalfInterpupillaryDistance(1.9F / 16F).setEyeScale(0.7F));
        put(EntityWitch.class, new HeadBase().setEyeOffset(0F, 3.2F/16F, 4F/16F).setHalfInterpupillaryDistance(1.9F / 16F).setEyeScale(0.7F));
        put(EntityWither.class, new HeadWither());
        put(EntityWolf.class, new HeadWolf());
        put(EntityZombie.class, new HeadBase());
        put(EntityZombieVillager.class, new HeadBase().setEyeOffset(0F, 3.2F/16F, 4F/16F).setHalfInterpupillaryDistance(1.9F / 16F).setEyeScale(0.7F));
    }};

    @Nullable
    public static HeadBase getHelperBase(Class<? extends EntityLivingBase> clz)
    {
        HeadBase helper = modelOffsetHelpers.get(clz);
        Class clzz = clz.getSuperclass();
        while(helper == null && clzz != EntityLivingBase.class)
        {
            helper = getHelperBase(clzz);
            if(helper != null)
            {
                helper = helper.clone();
                modelOffsetHelpers.put(clz, helper);
                break;
            }
            clzz = clzz.getSuperclass();
        }
        return helper;
    }

    public static void setHeadModel(HeadBase helper, RenderLivingBase renderer)
    {
        if(helper.headModel == null)
        {
            ModelBase model = renderer.getMainModel();
            helper.headModel = new ModelRenderer[1];
            if(model instanceof ModelHorse)
            {
                helper.headModel[0] = ((ModelHorse)model).body;
            }
            else if(model instanceof ModelLlama)
            {
                helper.headModel[0] = ((ModelLlama)model).body;
            }
            else if(model instanceof ModelIllager)
            {
                helper.headModel[0] = ((ModelIllager)model).head;
            }
            else if(model instanceof ModelBiped)
            {
                helper.headModel[0] = ((ModelBiped)model).bipedHead;
            }
            else if(model instanceof ModelBat)
            {
                helper.headModel[0] = ((ModelBat)model).batHead;
            }
            else if(model instanceof ModelBlaze)
            {
                helper.headModel[0] = ((ModelBlaze)model).blazeHead;
            }
            else if(model instanceof ModelChicken)
            {
                helper.headModel[0] = ((ModelChicken)model).head;
            }
            else if(model instanceof ModelQuadruped)
            {
                helper.headModel[0] = ((ModelQuadruped)model).head;
            }
            else if(model instanceof ModelCreeper)
            {
                helper.headModel[0] = ((ModelCreeper)model).head;
            }
            else if(model instanceof ModelDragon)
            {
                helper.headModel[0] = ((ModelDragon)model).head;
            }
            else if(model instanceof ModelEnderMite)
            {
                helper.headModel[0] = ((ModelEnderMite)model).bodyParts[0];
            }
            else if(model instanceof ModelGhast)
            {
                helper.headModel[0] = ((ModelGhast)model).body;
            }
            else if(model instanceof ModelGuardian)
            {
                helper.headModel[0] = ((ModelGuardian)model).guardianBody;
            }
            else if(model instanceof ModelIronGolem)
            {
                helper.headModel[0] = ((ModelIronGolem)model).ironGolemHead;
            }
            else if(model instanceof ModelMagmaCube)
            {
                helper.headModel[0] = ((ModelMagmaCube)model).core;
            }
            else if(model instanceof ModelOcelot)
            {
                helper.headModel[0] = ((ModelOcelot)model).ocelotHead;
            }
            else if(model instanceof ModelParrot)
            {
                helper.headModel[0] = ((ModelParrot)model).head;
            }
            else if(model instanceof ModelRabbit)
            {
                helper.headModel[0] = ((ModelRabbit)model).rabbitHead;
            }
            else if(model instanceof ModelShulker)
            {
                helper.headModel[0] = ((ModelShulker)model).head;
            }
            else if(model instanceof ModelSilverfish)
            {
                helper.headModel[0] = ((ModelSilverfish)model).silverfishBodyParts[0];
            }
            else if(model instanceof ModelSlime)
            {
                helper.headModel[0] = ((ModelSlime)model).slimeBodies;
            }
            else if(model instanceof ModelSnowMan)
            {
                helper.headModel[0] = ((ModelSnowMan)model).head;
            }
            else if(model instanceof ModelSpider)
            {
                helper.headModel[0] = ((ModelSpider)model).spiderHead;
            }
            else if(model instanceof ModelSquid)
            {
                helper.headModel[0] = ((ModelSquid)model).squidBody;
            }
            else if(model instanceof ModelVillager)
            {
                helper.headModel[0] = ((ModelVillager)model).villagerHead;
            }
            else if(model instanceof ModelWither)
            {
                helper.headModel = ((ModelWither)model).heads;
            }
            else if(model instanceof ModelWolf)
            {
                helper.headModel[0] = ((ModelWolf)model).wolfHeadMain;
            }
        }
    }

    public static void preChildHeadRenderCalls(EntityLivingBase living, RenderLivingBase render)
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
            else if(model instanceof ModelLlama)
            {
                GlStateManager.scale(0.625F, 0.45454544F, 0.45454544F);
                GlStateManager.translate(0.0F, 33.0F * modelScale, 0.0F);
            }
            else if(model instanceof ModelQuadruped)
            {
                if(model instanceof ModelPolarBear)
                {
                    GlStateManager.scale(0.6666667F, 0.6666667F, 0.6666667F);
                }
                GlStateManager.translate(0.0F, ((ModelQuadruped)model).childYOffset * modelScale, ((ModelQuadruped)model).childZOffset * modelScale);
            }
            else if(model instanceof ModelChicken || model instanceof ModelWolf)
            {
                GlStateManager.translate(0.0F, 5.0F * modelScale, 2.0F * modelScale);
            }
            else if(model instanceof ModelOcelot)
            {
                GlStateManager.scale(0.75F, 0.75F, 0.75F);
                GlStateManager.translate(0.0F, 10.0F * modelScale, 4.0F * modelScale);
            }
            else if(model instanceof ModelHorse && living instanceof AbstractHorse)
            {
                float f1 = ((AbstractHorse)living).getHorseSize();

                GlStateManager.scale(f1, f1, f1);
                GlStateManager.translate(0.0F, 1.35F * (1.0F - f1), 0.0F);
            }
        }
    }
}
