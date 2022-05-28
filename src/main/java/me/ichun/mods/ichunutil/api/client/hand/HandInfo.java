package me.ichun.mods.ichunutil.api.client.hand;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.api.common.PlacementCorrector;
import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import me.ichun.mods.ichunutil.loader.LoaderHandler;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class HandInfo
{
    protected static final Logger LOGGER = LogManager.getLogger();

    public String author; //Used for credit.
    public String forClass = "THIS SHOULD BE FILLED UP";

    public ModelRendererMarker[] leftHandParts;
    public ModelRendererMarker[] rightHandParts;

    public transient Class<? extends EntityModel> modelClass;

    public boolean setup()
    {
        if(modelClass == null)
        {
            try
            {
                Class clz = Class.forName(forClass);
                if(!(EntityModel.class.isAssignableFrom(clz)))
                {
                    LOGGER.error("{} does not extend EntityModel!", clz.getSimpleName());
                    return false;
                }

                modelClass = clz;

                if(leftHandParts != null)
                {
                    for(ModelRendererMarker leftHandPart : leftHandParts)
                    {
                        if(!leftHandPart.setupFields(clz))
                        {
                            return false;
                        }
                    }
                }
                if(rightHandParts != null)
                {
                    for(ModelRendererMarker rightHandPart : rightHandParts)
                    {
                        if(!rightHandPart.setupFields(clz))
                        {
                            return false;
                        }
                    }
                }

                return true;
            }
            catch(ClassNotFoundException ignored)
            {
                return false;
            }
        }
        return true; //model class has been set, we've been set up.
    }

    public ModelPart[] getHandParts(HumanoidArm side, EntityModel model) //Objects in array should not be null but can be
    {
        ModelRendererMarker[] markers = side == HumanoidArm.LEFT ? leftHandParts : rightHandParts;

        ModelPart[] parts = new ModelPart[markers.length];

        for(int i = 0; i < parts.length; i++)
        {
            parts[i] = markers[i].getModelRenderer(model);
        }

        return parts;
    }

    public PoseStack[] getPlacementCorrectors(HumanoidArm side) //Objects in array can be null
    {
        ModelRendererMarker[] markers = side == HumanoidArm.LEFT ? leftHandParts : rightHandParts;

        PoseStack[] stacks = new PoseStack[markers.length];

        for(int i = 0; i < stacks.length; i++)
        {
            stacks[i] = markers[i].getPlacementCorrectorStack();
        }

        return stacks;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public static class ModelRendererMarker
    {
        public String fieldName = null;
        public PlacementCorrector[] placementCorrectors;

        private transient Field field;
        private transient ArrayList<Integer> fieldIndices;

        private transient PoseStack stackPlacementCorrector;

        public boolean setupFields(Class<? extends EntityModel> clz)
        {
            if(field == null)
            {
                String fieldNameFull = this.fieldName;
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
                            LOGGER.error("Error parsing field of {} of model {}", fieldNameFull, clz.getSimpleName());
                            return false; //we errored.
                        }

                        indicesString = indicesString.substring(closeBracketIndex + 1);
                    }
                }
                else
                {
                    indices.add(-1);
                }
                Field field = HeadInfo.findField(clz, LoaderHandler.d().remapField(fieldName));
                if(field != null)
                {
                    field.setAccessible(true);
                    this.field = field;
                    this.fieldIndices = indices;
                    return true;
                }

                LOGGER.error("Error finding field of {} from {} of model {}", fieldName, fieldNameFull, clz.getSimpleName());
                return false;
            }
            return true;
        }

        public void correctPlacement(PoseStack stack)
        {
            if(placementCorrectors != null && placementCorrectors.length > 0)
            {
                if(stackPlacementCorrector == null)
                {
                    stackPlacementCorrector = new PoseStack();

                    for(PlacementCorrector renderCorrector : placementCorrectors)
                    {
                        renderCorrector.apply(stackPlacementCorrector);
                    }
                }

                PlacementCorrector.multiplyStackWithStack(stack, stackPlacementCorrector);
            }
        }

        public ModelPart getModelRenderer(EntityModel model)
        {
            try
            {
                Object o = field.get(model);

                Object modelAtIndex = o;

                for(Integer index : fieldIndices)
                {
                    modelAtIndex = HeadInfo.digForModelRendererWithIndex(modelAtIndex, index);
                }

                if(modelAtIndex instanceof ModelPart)
                {
                    return (ModelPart)modelAtIndex;
                }
            }
            catch(NullPointerException | IllegalAccessException | ArrayIndexOutOfBoundsException e)
            {
                LOGGER.error("Error getting model renderer from field {} of class {} in model {}", field.getName(), field.getDeclaringClass().getSimpleName(), model);
                e.printStackTrace();
            }
            return null;
        }

        public PoseStack getPlacementCorrectorStack()
        {
            if(placementCorrectors != null && placementCorrectors.length > 0)
            {
                if(stackPlacementCorrector == null)
                {
                    stackPlacementCorrector = new PoseStack();

                    for(PlacementCorrector renderCorrector : placementCorrectors)
                    {
                        renderCorrector.apply(stackPlacementCorrector);
                    }
                }

                return stackPlacementCorrector;
            }
            return null;
        }

        @Nullable
        private ModelPart getModel(Field field, ArrayList<Integer> indices, EntityModel model)
        {
            field.setAccessible(true);
            try
            {
                Object o = field.get(model);

                Object modelAtIndex = o;

                for(Integer index : indices)
                {
                    modelAtIndex = HeadInfo.digForModelRendererWithIndex(modelAtIndex, index);
                }

                if(modelAtIndex instanceof ModelPart)
                {
                    return (ModelPart)modelAtIndex;
                }
            }
            catch(NullPointerException | IllegalAccessException | ArrayIndexOutOfBoundsException e)
            {
                LOGGER.error("Error getting hand info of {} for {} in {}", field.getName(), field.getDeclaringClass().getSimpleName(), model);
                e.printStackTrace();
            }
            return null;
        }
    }
}
