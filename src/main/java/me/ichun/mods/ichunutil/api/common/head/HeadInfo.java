package me.ichun.mods.ichunutil.api.common.head;

import com.google.common.base.Splitter;
import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import me.ichun.mods.ichunutil.api.common.PlacementCorrector;
import me.ichun.mods.ichunutil.loader.LoaderHandler;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

@SuppressWarnings("rawtypes")
public class HeadInfo<E extends LivingEntity>
{
    protected static final Logger LOGGER = LogManager.getLogger();

    public static final Splitter DOT_SPLITTER = Splitter.on(".").trimResults().omitEmptyStrings();

    public static BooleanSupplier horseEasterEgg = () -> false;
    public static BooleanSupplier acidEyesBooleanSupplier = () -> false;
    public static IntSupplier aggressiveHeadTracking = () -> 0;

    //Used during serialisation
    public transient boolean hasStrippedInfo = false;

    //Functional fields
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public transient ModelPart headModel;
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public transient ModelPart[] childTranslates;
    public transient Field[] fields = null;
    public transient ArrayList<Integer>[] fieldIndex = null;

    public transient float[] headJoint = new float[3]; //so that GC doesn't hate us
    public transient Random rand = new Random();
    public transient int[] acidTime;

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public transient PoseStack renderCorrectorStack;

    //MultiModel Class instance;
    public transient Class<? extends EntityModel> multiModelClass = null;


    //SERIALISABLE STUFF
    public String author = null;

    public String forClass = null;
    public String customClass = null;

    //Find the field name
    public String modelFieldName = "THIS SHOULD BE FILLED UP";

    public Boolean isBoss = false;
    public Boolean affectedByInvisibility = true;

    //Model render fixes
    public PlacementCorrector[] renderCorrectors = null;

    //Child translates
    public float[] childEntityScale = null;
    public float[] childEntityOffset = null;

    //GooglyEyes Stuff
    public Boolean noFaceInfo = false; //Use this to disable googly eye support
    public float[] eyeOffset = new float[] { 0F, 4F/16F, 4F/16F }; //I love that I can use Tabula for this. (I still do -iChun 2020)
    @SerializedName("irisColour") //legacy support due to bad naming, sorry.
    public float[] corneaColour = new float[] { 0.8980392F, 0.8980392F, 0.8980392F }; //it used to be 0.9F
    @SerializedName("pupilColour") //legacy support due to bad naming, sorry.
    public float[] irisColour = new float[] { 0.0F, 0.0F, 0.0F };
    public Float halfInterpupillaryDistance = 2F/16F;
    public Float eyeScale = 0.75F;
    public Boolean sideEyed = false;
    public Boolean topEyed = false; //thanks hoglins
    public Float eyeYRotation = 0F;
    public Float eyeXRotation = 0F;
    public Integer eyeCount = 2;
    public Boolean doesEyeGlow = false;

    //Hats Stuff
    public Boolean noTopInfo = false; //Use this to disable Hats support
    public float[] headTopCenter = new float[] { 0F, 8F/16F, 0F };
    public Float headScale = 1F; //Top of the head. Compared to the a player head.
    public Float hatTiltPitch = 0F;
    public Float hatTiltYaw = 0F;
    public float[] headArmorOffset = new float[] { 0F, 1F/16F, 0F };
    public Float headArmorScale = 1.27F; //Used to be 10.125F / 8F; //Armor is usually 1.0F expansion, both directions.

    //Oddball Support
    public HeadInfo[] additionalHeads = null;
    public HeadInfo[] multiModel = null;

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

    public int getHeadCount(E living)
    {
        return additionalHeads != null ? additionalHeads.length + 1 : 1;
    }

    public HeadInfo getHeadInfo(E living, int head)
    {
        if(head > 0)
        {
            return additionalHeads[head - 1];
        }
        return this;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public void correctPosition(E living, PoseStack stack, float partialTick)
    {
        if(renderCorrectors != null && renderCorrectors.length > 0)
        {
            if(renderCorrectorStack == null)
            {
                renderCorrectorStack = new PoseStack();

                for(PlacementCorrector renderCorrector : renderCorrectors)
                {
                    renderCorrector.apply(renderCorrectorStack);
                }
            }

            PlacementCorrector.multiplyStackWithStack(stack, renderCorrectorStack);
        }
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public float[] getHeadJointOffset(E living, PoseStack stack, float partialTick, int head)
    {
        headJoint[0] = -(headModel.x / 16F);
        headJoint[1] = -(headModel.y / 16F);
        headJoint[2] = -(headModel.z / 16F);
        return headJoint;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public float[] getEyeOffsetFromJoint(E living, PoseStack stack, float partialTick, int eye)
    {
        return eyeOffset;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public float getEyeSideOffset(E living, PoseStack stack, float partialTick, int eye)
    {
        return eye == 0 ? halfInterpupillaryDistance : -halfInterpupillaryDistance;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public float getEyeScale(E living, PoseStack stack, float partialTick, int eye) //base eye scale size
    {
        return eyeScale;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public float getEyeRotation(E living, PoseStack stack, float partialTick, int eye)
    {
        return sideEyed ? (eye % 2 == 0 ? 90F : -90F) : (eye % 2 == 0 ? eyeYRotation : -eyeYRotation);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public float getEyeTopRotation(E living, PoseStack stack, float partialTick, int eye)
    {
        return topEyed ? -90F : eyeXRotation;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public float getIrisScale(E living, PoseStack stack, float partialTick, int eye)
    {
        if(acidEyesBooleanSupplier.getAsBoolean() || living.getEntityData().get(LivingEntity.DATA_EFFECT_COLOR_ID) > 0)
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
            return 0.3F + ((float)Math.sin(Math.toRadians((living.tickCount + partialTick) / acidTime[eye] * 360F)) + 1F) / 2F;
        }
        return 1F + (0.35F * (living.deathTime + partialTick) / 20F);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public float[] getCorneaColours(E living, PoseStack stack, float partialTick, int eye)
    {
        return corneaColour;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public float[] getIrisColours(E living, PoseStack stack, float partialTick, int eye)
    {
        return irisColour;
    }


    //HEAD FUNCTIONS
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public float[] getHatOffsetFromJoint(E living, PoseStack stack, float partialTick, int head)
    {
        return headTopCenter;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public float getHatScale(E living, PoseStack stack, float partialTick, int head)
    {
        return headScale;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public float getHatYaw(E living, PoseStack stack, float partialTick, int head)
    {
        return hatTiltYaw;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public float getHatPitch(E living, PoseStack stack, float partialTick, int head)
    {
        return hatTiltPitch;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public float getHeadYaw(E living, PoseStack stack, float partialTick, int head, int eye)
    {
        return (float)Math.toDegrees(headModel.yRot);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public float getHeadPitch(E living, PoseStack stack, float partialTick, int head, int eye)
    {
        return (float)Math.toDegrees(headModel.xRot);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public float getHeadRoll(E living, PoseStack stack, float partialTick, int head, int eye)
    {
        return (float)Math.toDegrees(headModel.zRot);
    }

    //Mathed functions that don't share the model/require matrixstack (during rendering)
    public float getHeadYaw(E living, float partialTick, int head, int eye)
    {
        return living.yHeadRotO + (living.yHeadRot - living.yHeadRotO) * partialTick;
    }

    public float getHeadPitch(E living, float partialTick, int head, int eye)
    {
        return living.xRotO + (living.getXRot() - living.xRotO) * partialTick;
    }

    public float getHeadRoll(E living, float partialTick, int head, int eye)
    {
        return 0F;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public float[] getHeadArmorOffset(E living, PoseStack stack, float partialTick, int head)
    {
        return !living.getItemBySlot(EquipmentSlot.HEAD).isEmpty() ? headArmorOffset : null;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public float getHeadArmorScale(E living, PoseStack stack, float partialTick, int head)
    {
        return !living.getItemBySlot(EquipmentSlot.HEAD).isEmpty() ? headArmorScale : 1F;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public void postHeadTranslation(E living, PoseStack stack, float partialTick)
    {
        if(childTranslates != null)
        {
            for(ModelPart child : childTranslates)
            {
                translateRotateToChild(living, stack, child);
            }
        }
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public void preChildEntHeadRenderCalls(E living, PoseStack stack, LivingEntityRenderer<E, ?> render)
    {
        if(living.isBaby()) //I don't like this if statement any more than you do.
        {
            if(childEntityScale != null || childEntityOffset != null) //there is a child override
            {
                if(childEntityScale != null)
                {
                    stack.scale(childEntityScale[0], childEntityScale[1], childEntityScale[2]);
                }
                if(childEntityOffset != null)
                {
                    stack.translate(childEntityOffset[0], childEntityOffset[1], childEntityOffset[2]);
                }
            }
            else //default to MC scaling
            {
                float modelScale = 0.0625F;
                Model model = render.getModel();
                if(model instanceof HumanoidModel)
                {
                    stack.scale(0.75F, 0.75F, 0.75F);
                    stack.translate(0.0F, 16.0F * modelScale, 0.0F);
                }
                else if(model instanceof AgeableListModel)
                {
                    AgeableListModel<?> ageableModel = (AgeableListModel<?>)model;
                    if(ageableModel.scaleHead)
                    {
                        float f = 1.5F / ageableModel.babyHeadScale;
                        stack.scale(f, f, f);
                    }
                    stack.translate(0.0F, ageableModel.babyYHeadOffset * modelScale, ageableModel.babyZHeadOffset * modelScale);
                }
            }
        }
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public void translateRotateToChild(E living, PoseStack stack, ModelPart renderer)
    {
        stack.translate(renderer.x / 16F, renderer.y / 16F, renderer.z / 16F);

        stack.mulPose(Vector3f.ZP.rotation(renderer.zRot));
        stack.mulPose(Vector3f.YP.rotation(renderer.yRot));
        stack.mulPose(Vector3f.XP.rotation(renderer.xRot));
    }

    //Setup functions are to set up the HeadInfoDelegate
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public boolean setup(E living, LivingEntityRenderer renderer)
    {
        return true;
    }

    public boolean setup(E living)
    {
        return true;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public void setHeadModel(E living, LivingEntityRenderer renderer) //actually gets the most parent ModelRenderer. we translate to the head in the functions if necessary.
    {
        if(this.headModel == null || aggressiveHeadTracking.getAsInt() == 1 || aggressiveHeadTracking.getAsInt() == 2 && renderer instanceof PlayerRenderer)
        {
            setHeadModelFromRenderer(living, renderer, renderer.getModel());
        }
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    protected void setHeadModelFromRenderer(E living, LivingEntityRenderer renderer, EntityModel model)
    {
        if(fieldIndex == null) //we haven't looked it up yet?
        {
            List<String> fieldNames = DOT_SPLITTER.splitToList(modelFieldName);
            fields = new Field[fieldNames.size()];
            fieldIndex = new ArrayList[fieldNames.size()];
            boolean flag = false; //true if we errored.
            for(int i = 0; i < fieldNames.size(); i++)
            {
                String fieldNameFull = fieldNames.get(i);
                String fieldName = fieldNameFull;
                ArrayList<Integer> indices = new ArrayList<>();
                if(fieldName.contains("[")) //it is an array, list, or get child, or worse, multiples.
                {
                    fieldName = fieldNameFull.substring(0, fieldNameFull.indexOf("["));

                    String indicesString = fieldNameFull.substring(fieldNameFull.indexOf("["));
                    while(indicesString.startsWith("["))
                    {
                        int closeBracketIndex = indicesString.indexOf("]");
                        //do magic
                        try
                        {
                            indices.add(Integer.parseInt(indicesString.substring(1, closeBracketIndex))); //we look for -1 and higher to confirm parsing.
                        }
                        catch(NumberFormatException | StringIndexOutOfBoundsException e)
                        {
                            LOGGER.error("Error parsing modelFieldName of {} for {} of model {} in {}", modelFieldName, this.getClass().getSimpleName(), model.getClass().getSimpleName(), renderer.getClass().getSimpleName());
                            flag = true;
                            indices.add(-3); //we look for -1 and higher to confirm parsing.
                        }

                        indicesString = indicesString.substring(closeBracketIndex + 1);
                    }
                }
                else
                {
                    indices.add(-1);
                }
                Field field = findField(model.getClass(), LoaderHandler.d().remapField(fieldName));
                if(field != null)
                {
                    fields[i] = field;
                    fieldIndex[i] = indices;
                }
                else
                {
                    flag = true;
                    LOGGER.error("Error finding field of {} from {} for {} of model {} in {}", fieldName, modelFieldName, this.getClass().getSimpleName(), model.getClass().getSimpleName(), renderer.getClass().getSimpleName());
                }
            }
            if(flag)
            {
                fields = null;
            }
            else if(fieldIndex.length > 1)
            {
                childTranslates = new ModelPart[fieldIndex.length - 1];
            }
        }

        if(fields != null)
        {
            for(int i = 0; i < fields.length; i++)
            {
                Field field = fields[i];
                if(field == null)
                {
                    break;
                }

                field.setAccessible(true);

                if(i == 0 && !field.getDeclaringClass().isInstance(model)) //all our fields are from the same class. Check once only. model is instance of the class declaring our field
                {
                    break;
                }

                ArrayList<Integer> indices = fieldIndex[i];
                try
                {
                    Object o = field.get(model);

                    Object modelAtIndex = o;

                    for(Integer index : indices)
                    {
                        modelAtIndex = digForModelRendererWithIndex(modelAtIndex, index);
                    }

                    if(modelAtIndex instanceof ModelPart)
                    {
                        if(i == 0) //we're still looking for the parent heads.
                        {
                            this.headModel = (ModelPart)modelAtIndex;
                        }
                        else
                        {
                            this.childTranslates[i - 1] = (ModelPart)modelAtIndex;
                        }
                    }
                }
                catch(NullPointerException | IllegalAccessException | ArrayIndexOutOfBoundsException e)
                {
                    LOGGER.error("Error getting head info of {} for {} in {}", modelFieldName, this.getClass().getSimpleName(), renderer.getClass().getSimpleName());
                    e.printStackTrace();
                }
            }
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

    @Nullable
    public static ModelPart digForModelRendererWithIndex(Object o, int index)
    {
        if(o instanceof ModelPart)
        {
            if(index >= 0)
            {
                return ((ModelPart)o).children.get(index);
            }
            else
            {
                return ((ModelPart)o);
            }
        }
        else if(o.getClass().isArray() && ModelPart.class.isAssignableFrom(o.getClass().getComponentType())) //A ModelRenderer array
        {
            if(index >= 0)
            {
                return ((ModelPart[])o)[index];
            }
            else
            {
                return ((ModelPart[])o)[0];
            }
        }
        else if(o instanceof List)
        {
            Object o2 = ((List<?>)o).get(index);
            if(o2 instanceof ModelPart)
            {
                return ((ModelPart)o2);
            }
        }

        return null;
    }

    public static class Serializer implements JsonDeserializer<HeadInfo>, JsonSerializer<HeadInfo>
    {
        @Override
        public JsonElement serialize(HeadInfo src, Type typeOfSrc, JsonSerializationContext context)
        {
            if(!src.hasStrippedInfo)
            {
                return context.serialize(createStrippedClone(src));
            }

            return (new Gson()).toJsonTree(src);
        }

        private static HeadInfo createStrippedClone(HeadInfo src)
        {
            Gson gson = new Gson();

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

            if(clone.additionalHeads != null)
            {
                for(int i = 0; i < clone.additionalHeads.length; i++)
                {
                    clone.additionalHeads[i] = createStrippedClone(clone.additionalHeads[i]);
                }
            }

            if(clone.getClass() != HeadInfo.class)
            {
                clone.customClass = clone.getClass().getName();
            }
            else if(clone.modelFieldName == null || clone.modelFieldName.equals(defaultInfo.modelFieldName))
            {
                LOGGER.error("HeadInfo is not using a custom class but hasn't set a head model.");
            }
            clone.hasStrippedInfo = true;

            return clone;
        }

        private static boolean isFieldValueEqual(Field field, HeadInfo clone, HeadInfo defaultInfo) throws IllegalAccessException //only checks on non-null objects
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

        @SuppressWarnings("rawtypes")
        @Override
        public HeadInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject jsonObject = (JsonObject)json;
            if(jsonObject.has("multiModel") && jsonObject.getAsJsonArray("multiModel").size() > 0)
            {
                HeadInfoDelegate headInfo = context.deserialize(json, HeadInfoDelegate.class);
                headInfo.checkModels();
                return headInfo;
            }
            else if(jsonObject.has("customClass"))
            {
                String forClass = jsonObject.has("forClass") ? jsonObject.get("forClass").getAsString() : "Unknown Class";
                String customClass = jsonObject.get("customClass").getAsString();
                try
                {
                    Class clz = Class.forName(customClass);

                    HeadInfo deserialized = context.deserialize(json, clz);
                    HeadInfo defaultInfo = (HeadInfo)clz.getDeclaredConstructor().newInstance();

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
                    LOGGER.error("Cannot find custom head info class: " + customClass + " for class: " + forClass);
                    e.printStackTrace();
                }
                catch(IllegalAccessException | InstantiationException | ClassCastException | NoSuchMethodException | InvocationTargetException e)
                {
                    LOGGER.error("Error creating custom class: " + customClass + " for class: " + forClass);
                    e.printStackTrace();
                }
            }

            return (new Gson()).fromJson(json, HeadInfo.class);
        }
    }

    /**
     * Use this object when sending Head Infos to iChunUtil via IMC.
     */
    public static class HeadHolder
    {
        public final @Nonnull HeadInfo<?> info;
        public final @Nonnull Class<? extends LivingEntity> clz;

        public HeadHolder(@Nonnull HeadInfo<?> info, @Nonnull Class<? extends LivingEntity> clz) {
            this.info = info;
            this.clz = clz;
        }
    }
}
