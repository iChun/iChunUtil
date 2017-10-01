package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Random;
import java.util.function.BooleanSupplier;

public class HeadBase<E extends EntityLivingBase>
{
    public static BooleanSupplier acidEyesBooleanSupplier = () -> false;
    public static BooleanSupplier horseHeadIsBody = () -> true;

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
        return EntityHelper.interpolateRotation(living.prevRotationYawHead, living.rotationYawHead, partialTick) - EntityHelper.interpolateRotation(living.prevRenderYawOffset, living.renderYawOffset, partialTick);
    }

    public float getHeadPitch(E living, float partialTick, int eye)
    {
        return EntityHelper.interpolateRotation(living.prevRotationPitch, living.rotationPitch, partialTick);
    }

    public float getHeadRoll(E living, float partialTick, int eye)
    {
        return 0;
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

    public float getHeadYawForTracker(E living)
    {
        return living.rotationYawHead;
    }

    public float getHeadPitchForTracker(E living)
    {
        return living.rotationPitch;
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
        put(EntityChicken.class, new HeadBase().setHeadJoint(0F, -15F/16F, 4F/16F).setEyeOffset(0F, 4.5F/16F, 2F/16F).setHalfInterpupillaryDistance(1.5F / 16F).setEyeScale(0.375F));
        put(EntityCow.class, new HeadBase().setHeadJoint(0F, -4F/16F, 8F/16F).setEyeOffset(0F, 1F/16F, 6F/16F).setHalfInterpupillaryDistance(3F / 16F));
        put(EntityCreeper.class, new HeadBase().setHeadJoint(0F, -6F/16F, 0F).setEyeOffset(0F, 5F/16F, 4F/16F)); //make creeper maaaaaaad with narrowing pupils
        put(EntityDragon.class, new HeadDragon());
        put(EntityElderGuardian.class, new HeadElderGuardian());
        put(EntityEnderman.class, new HeadEnderman());
        put(EntityEndermite.class, new HeadEndermite());
        put(EntityGhast.class, new HeadGhast());
        put(EntityGuardian.class, new HeadGuardian());
        put(EntityIronGolem.class, new HeadBase().setHeadJoint(0F, 7F/16F, 2F/16F).setEyeOffset(0F, 6F/16F, 5.5F/16F));
        put(EntityLlama.class, new HeadLlama());
        put(EntityMagmaCube.class, new HeadMagmaCube());
        put(EntityMooshroom.class, new HeadBase().setHeadJoint(0F, -4F/16F, 8F/16F).setEyeOffset(0F, 1F/16F, 6F/16F).setHalfInterpupillaryDistance(3F / 16F));
        put(EntityOcelot.class, new HeadOcelot());
        put(EntityParrot.class, new HeadParrot());
        put(EntityPig.class, new HeadBase().setHeadJoint(0F, -12F/16F, 6F/16F).setEyeOffset(0F, 0.5F/16F, 8F/16F).setHalfInterpupillaryDistance(3F/16F));
        put(EntityPigZombie.class, new HeadPigZombie());
        put(EntityPolarBear.class, new HeadBase().setHeadJoint(0F, -10F/16F, 16F/16F).setEyeOffset(0F, -0.5F/16F, 3F/16F).setEyeScale(0.4F));
        put(EntityRabbit.class, new HeadRabbit());
        put(EntitySheep.class, new HeadSheep());
        put(EntityShulker.class, new HeadShulker());
        put(EntitySilverfish.class, new HeadSilverfish());
        put(EntitySlime.class, new HeadSlime());
        put(EntitySnowman.class, new HeadBase().setHeadJoint(0F, -4F/16F, 0F/16F).setEyeOffset(0F, 7.5F/16, 5F/16F).setHalfInterpupillaryDistance(1.5F / 16F).setEyeScale(1F));
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
                modelOffsetHelpers.put(clzz, helper);
                break;
            }
            clzz = clzz.getSuperclass();
        }
        return helper;
    }

    public static void preChildHeadRenderCalls(EntityLivingBase living, Render render)
    {
        if(living.isChild()) //I don't like this if statement any more than you do.
        {
            if(render instanceof RenderLivingBase)
            {
                float modelScale = 0.0625F;
                ModelBase model = ((RenderLivingBase)render).getMainModel();
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
                    float childYOffset = ObfuscationReflectionHelper.getPrivateValue(ModelQuadruped.class, (ModelQuadruped)model, "field_78145_g", "childYOffset");
                    float childZOffset = ObfuscationReflectionHelper.getPrivateValue(ModelQuadruped.class, (ModelQuadruped)model, "field_78151_h", "childZOffset");

                    if(model instanceof ModelPolarBear)
                    {
                        GlStateManager.scale(0.6666667F, 0.6666667F, 0.6666667F);
                    }
                    GlStateManager.translate(0.0F, childYOffset * modelScale, childZOffset * modelScale);
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
                else if(model instanceof ModelHorse && living instanceof EntityHorse)
                {
                    float f1 = ((EntityHorse)living).getHorseSize();

                    GlStateManager.scale(f1, f1, f1);
                    GlStateManager.translate(0.0F, 1.35F * (1.0F - f1), 0.0F);
                }
            }
        }
    }
}
