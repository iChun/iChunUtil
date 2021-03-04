package me.ichun.mods.ichunutil.common.head;

import com.google.gson.*;
import com.mojang.blaze3d.matrix.MatrixStack;
import cpw.mods.modlauncher.api.INameMappingService;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Random;

public class HeadInfo<E extends LivingEntity>
{
    //Used during serialisation
    public transient boolean hasStrippedInfo = false;

    //Functional fields
    @OnlyIn(Dist.CLIENT)
    public transient ModelRenderer[] headModel = null;
    public transient Field field;
    public transient int list = -2;

    public transient float[] headJoint = new float[3]; //so that GC doesn't hate us
    public transient Random rand = new Random();
    public transient int[] acidTime;

    //SERIALISABLE STUFF
    public String forClass = null;
    public String customClass = null;

    //Find the field name
    //    public String modelFieldName = null;
    public String modelFieldName = "THIS SHOULD BE FILLED UP";

    //GooglyEyes Stuff
    public Boolean noFaceInfo = false; //Use this to disable googly eye support
    public float[] eyeOffset = new float[] { 0F, 4F/16F, 4F/16F }; //I love that I can use Tabula for this. (I still do -iChun 2020)
    public float[] irisColour = new float[] { 0.9F, 0.9F, 0.9F };
    public float[] pupilColour = new float[] { 0.0F, 0.0F, 0.0F };
    public Float halfInterpupillaryDistance = 2F/16F;
    public Float eyeScale = 0.75F;
    public Boolean sideEyed = false;
    public Boolean topEyed = false; //thanks hoglins
    public Integer eyeCount = 2;
    public Boolean affectedByInvisibility = true;
    public Boolean doesEyeGlow = false;

    //Hats Stuff
    public Boolean noTopInfo = false; //Use this to disable Hats support
    public Boolean useDefinedInfo = false; //Use this to force Hats to use the JSON values rather than calculating the top of the head. //TODO Default to center of the top of the head?
    public float[] headTopCenter = new float[] { 0F, 8F/16F, 0F };
    public Float headScale = 1F; //Compared to the a player head.

    public boolean affectedByInvisibility(E living, int eye)
    {
        return affectedByInvisibility;
    }

    public boolean doesEyeGlow(E living, int eye)
    {
        return doesEyeGlow;
    }

    public int getEyeCount(E living)
    {
        return eyeCount;
    }


    @OnlyIn(Dist.CLIENT)
    public float[] getHeadJointOffset(E living, MatrixStack stack, float partialTick, int eye)
    {
        headJoint[0] = -(headModel[0].rotationPointX / 16F);
        headJoint[1] = -(headModel[0].rotationPointY / 16F);
        headJoint[2] = -(headModel[0].rotationPointZ / 16F);
        return headJoint;
    }

    @OnlyIn(Dist.CLIENT)
    public float[] getEyeOffsetFromJoint(E living, MatrixStack stack, float partialTick, int eye)
    {
        return eyeOffset;
    }

    @OnlyIn(Dist.CLIENT)
    public float getEyeSideOffset(E living, MatrixStack stack, float partialTick, int eye)
    {
        return eye == 0 ? halfInterpupillaryDistance : -halfInterpupillaryDistance;
    }

    @OnlyIn(Dist.CLIENT)
    public float getEyeScale(E living, MatrixStack stack, float partialTick, int eye) //base eye scale size
    {
        return eyeScale;
    }

    @OnlyIn(Dist.CLIENT)
    public float getEyeRotation(E living, MatrixStack stack, float partialTick, int eye)
    {
        return sideEyed ? (eye % 2 == 0 ? 90F : -90F) : 0F;
    }

    @OnlyIn(Dist.CLIENT)
    public float getEyeTopRotation(E living, MatrixStack stack, float partialTick, int eye)
    {
        return topEyed ? -90F : 0F;
    }

    @OnlyIn(Dist.CLIENT)
    public float getPupilScale(E living, MatrixStack stack, float partialTick, int eye)
    {
        if(HeadHandler.acidEyesBooleanSupplier.getAsBoolean() || living.getDataManager().get(LivingEntity.POTION_EFFECTS) > 0)
        {
            rand.setSeed(Math.abs(living.hashCode()) * 1000L);
            int eyeCount = getEyeCount(living);
            if(acidTime == null || acidTime.length < eyeCount)
            {
                acidTime = new int[eyeCount];
            }
            for(int i = 0; i < eyeCount; i++)
            {
                acidTime[i] = 20 + rand.nextInt(20);
            }
            return 0.3F + ((float)Math.sin(Math.toRadians((living.ticksExisted + partialTick) / acidTime[eye] * 360F)) + 1F) / 2F;
        }
        return 1F + (0.35F * (living.deathTime + partialTick) / 20F);
    }

    @OnlyIn(Dist.CLIENT)
    public float[] getIrisColours(E living, MatrixStack stack, float partialTick, int eye)
    {
        return irisColour;
    }

    @OnlyIn(Dist.CLIENT)
    public float[] getPupilColours(E living, MatrixStack stack, float partialTick, int eye)
    {
        return pupilColour;
    }

    @OnlyIn(Dist.CLIENT)
    public float[] getHatOffset(E living, MatrixStack stack, float partialTick)
    {
        return headTopCenter;
    }

    @OnlyIn(Dist.CLIENT)
    public float getHatScale(E living, MatrixStack stack, float partialTick)
    {
        return headScale;
    }

    @OnlyIn(Dist.CLIENT)
    public float getHeadYaw(E living, MatrixStack stack, float partialTick, int eye)
    {
        return (float)Math.toDegrees(headModel[0].rotateAngleY);
    }

    @OnlyIn(Dist.CLIENT)
    public float getHeadPitch(E living, MatrixStack stack, float partialTick, int eye)
    {
        return (float)Math.toDegrees(headModel[0].rotateAngleX);
    }

    @OnlyIn(Dist.CLIENT)
    public float getHeadRoll(E living, MatrixStack stack, float partialTick, int eye)
    {
        return (float)Math.toDegrees(headModel[0].rotateAngleZ);
    }

    //Mathed functions that don't share the model/require matrixstack (during rendering)
    public float getHeadYaw(E living, float partialTick, int eye)
    {
        return living.prevRotationYawHead + (living.rotationYawHead - living.prevRotationYawHead) * partialTick;
    }

    public float getHeadPitch(E living, float partialTick, int eye)
    {
        return living.prevRotationPitch + (living.rotationPitch - living.prevRotationPitch) * partialTick;
    }

    public float getHeadRoll(E living, float partialTick, int eye)
    {
        return 0F;
    }

    @OnlyIn(Dist.CLIENT)
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

    @OnlyIn(Dist.CLIENT)
    public void translateRotateToChild(MatrixStack stack, ModelRenderer renderer)
    {
        stack.translate(renderer.rotationPointX / 16F, renderer.rotationPointY / 16F, renderer.rotationPointZ / 16F);

        stack.rotate(Vector3f.ZP.rotation(renderer.rotateAngleZ));
        stack.rotate(Vector3f.YP.rotation(renderer.rotateAngleY));
        stack.rotate(Vector3f.XP.rotation(renderer.rotateAngleX));
    }

    @SuppressWarnings("rawtypes")
    @OnlyIn(Dist.CLIENT)
    public void setHeadModel(LivingRenderer renderer) //actually gets the most parent ModelRenderer. we translate to the head in the functions if necessary.
    {
        boolean flag = this.headModel == null;
        if(flag)
        {
            this.headModel = new ModelRenderer[1];
        }
        if(flag || iChunUtil.configClient.aggressiveHeadTracking == 1 || iChunUtil.configClient.aggressiveHeadTracking == 2 && renderer instanceof PlayerRenderer)
        {
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
    @OnlyIn(Dist.CLIENT)
    protected void setHeadModelFromRenderer(LivingRenderer renderer)
    {
        EntityModel model = renderer.getEntityModel();
        if(field == null && list == -2) //we haven't looked it up yet?
        {
            String fieldName = modelFieldName;
            list = -1;
            if(fieldName.contains("[")) //it is an array or a list
            {
                fieldName = modelFieldName.substring(0, modelFieldName.indexOf("["));
                try
                {
                    list = Integer.parseInt(modelFieldName.substring(modelFieldName.indexOf("[") + 1, modelFieldName.length() - 1));
                }
                catch(NumberFormatException e)
                {
                    iChunUtil.LOGGER.error("Error parsing modelFieldName of {} for {}", modelFieldName, this.getClass().getSimpleName());
                    list = -3; //we look for -1 and higher to confirm parsing. -2 means we haven't parsed yet.
                }
            }
            if(list >= -1)
            {
                field = findField(model.getClass(), ObfuscationReflectionHelper.remapName(INameMappingService.Domain.FIELD, fieldName));
            }
        }

        if(field != null)
        {
            field.setAccessible(true);
            if(field.getDeclaringClass().isInstance(model)) //model is instance of the class declaring our field
            {
                try
                {
                    Object o = field.get(model);
                    if(o instanceof ModelRenderer)
                    {
                        this.headModel[0] = ((ModelRenderer)o);
                    }
                    else if(o.getClass().isArray() && ModelRenderer.class.isAssignableFrom(o.getClass().getComponentType())) //A ModelRenderer array
                    {
                        if(list >= 0)
                        {
                            this.headModel[0] = ((ModelRenderer[])o)[list];
                        }
                        else
                        {
                            this.headModel = ((ModelRenderer[])o);
                        }
                    }
                    else if(o instanceof List)
                    {
                        Object o2 = ((List<?>)o).get(list);
                        if(o2 instanceof ModelRenderer)
                        {
                            this.headModel[0] = ((ModelRenderer)o2);
                        }
                    }
                }
                catch(IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            iChunUtil.LOGGER.warn("We can't find the field {} for {} from renderer {}", modelFieldName, model.getClass().getSimpleName(), renderer.getClass().getSimpleName());
        }
    }

    @Nullable
    public static Field findField(Class clz, String fieldName)
    {
        Field f = null;
        try
        {
            f = clz.getDeclaredField(fieldName);
        }
        catch(NoSuchFieldException e)
        {
            if(clz.getSuperclass() != EntityModel.class)
            {
                f = findField(clz.getSuperclass(), fieldName);
            }
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }
        return f;
    }

    public static class Serializer implements JsonDeserializer<HeadInfo>, JsonSerializer<HeadInfo>
    {
        @Override
        public JsonElement serialize(HeadInfo src, Type typeOfSrc, JsonSerializationContext context)
        {
            Gson gson = new Gson();
            if(!src.hasStrippedInfo)
            {
                HeadInfo defaultInfo = new HeadInfo();
                HeadInfo clone = gson.fromJson(gson.toJson(src), src.getClass());

                for(Field field : HeadInfo.class.getDeclaredFields())
                {
                    field.setAccessible(true);
                    if(!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers()))
                    {
                        try
                        {
                            if(field.get(clone) != null && isFieldValueEqual(field, clone, defaultInfo))
                            {
                                field.set(clone, null);
                            }
                        }
                        catch(IllegalAccessException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }

                if(clone.getClass() != HeadInfo.class)
                {
                    clone.customClass = clone.getClass().getName();
                }
                else if(clone.modelFieldName == null || clone.modelFieldName.equals(defaultInfo.modelFieldName))
                {
                    iChunUtil.LOGGER.error("HeadInfo is not using a custom class but hasn't set a head model.");
                }
                clone.hasStrippedInfo = true;
                return context.serialize(clone);
            }

            return gson.toJsonTree(src);
        }

        private boolean isFieldValueEqual(Field field, HeadInfo clone, HeadInfo defaultInfo) throws IllegalAccessException //only checks on non-null objects
        {
            Object o1 = field.get(clone);
            Object o2 = field.get(defaultInfo);
            if(o1 == null && o2 != null || o2 == null && o1 != null)
            {
                return false;
            }
            if(o1 == null) //o2 null check unnecessary
            {
                return true;
            }

            try
            {
                if(o1.getClass() == o2.getClass() && o1.getClass().isArray())
                {
                    int l1 = Array.getLength(o1); //int[] and float[] != Object[]. You can't cast to them
                    int l2 = Array.getLength(o2);

                    if(l1 == l2)
                    {
                        for(int i = 0; i < l1; i++)
                        {
                            if(!Array.get(o1, i).equals(Array.get(o2, i))) //if not equal, they're not equal anymore, return false.
                            {
                                return false;
                            }
                        }
                        return true;//we made it here, arrays are equal.
                    }
                }
            }
            catch(ClassCastException e)
            {
                e.printStackTrace();
            }
            return o1.equals(o2);
        }

        @Override
        public HeadInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject jsonObject = (JsonObject)json;
            if(jsonObject.has("customClass"))
            {
                String customClass = jsonObject.get("customClass").getAsString();
                try
                {
                    Class clz = Class.forName(customClass);

                    HeadInfo deserialized = context.deserialize(json, clz);
                    HeadInfo defaultInfo = new HeadInfo();

                    for(Field field : HeadInfo.class.getDeclaredFields())
                    {
                        field.setAccessible(true);
                        if(!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers()))
                        {
                            try
                            {
                                if(field.get(deserialized) == null) //None of the fields should be null, take from our default.
                                {
                                    field.set(deserialized, field.get(defaultInfo));
                                }
                            }
                            catch(IllegalAccessException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }

                    return deserialized;
                }
                catch(ClassNotFoundException e)
                {
                    iChunUtil.LOGGER.error("Cannot find custom head info class: " + customClass);
                    e.printStackTrace();
                }
            }

            return (new Gson()).fromJson(json, HeadInfo.class);
        }
    }
}
