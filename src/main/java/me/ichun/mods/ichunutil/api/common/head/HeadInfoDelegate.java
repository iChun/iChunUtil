package me.ichun.mods.ichunutil.api.common.head;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

@SuppressWarnings({"unchecked", "rawtypes"})
public class HeadInfoDelegate<E extends LivingEntity> extends HeadInfo<E>
{
    public transient HeadInfo delegate;

    @Override
    public boolean affectedByInvisibility(E living, int eye)
    {
        return delegate.affectedByInvisibility(living, eye);
    }

    @Override
    public boolean doesEyeGlow(E living, int eye)
    {
        return delegate.doesEyeGlow(living, eye);
    }

    @Override
    public int getEyeCount(E living)
    {
        return delegate.getEyeCount(living);
    }

    @Override
    public int getHeadCount(E living)
    {
        return delegate.getHeadCount(living);
    }

    @Override
    public HeadInfo getHeadInfo(E living, int head)
    {
        return delegate.getHeadInfo(living, head);
    }


    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float[] getHeadJointOffset(E living, PoseStack stack, float partialTick, int head)
    {
        return delegate.getHeadJointOffset(living, stack, partialTick, head);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float[] getEyeOffsetFromJoint(E living, PoseStack stack, float partialTick, int eye)
    {
        return delegate.getEyeOffsetFromJoint(living, stack, partialTick, eye);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getEyeSideOffset(E living, PoseStack stack, float partialTick, int eye)
    {
        return delegate.getEyeSideOffset(living, stack, partialTick, eye);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getEyeScale(E living, PoseStack stack, float partialTick, int eye) //base eye scale size
    {
        return delegate.getEyeScale(living, stack, partialTick, eye);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getEyeRotation(E living, PoseStack stack, float partialTick, int eye)
    {
        return delegate.getEyeRotation(living, stack, partialTick, eye);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getEyeTopRotation(E living, PoseStack stack, float partialTick, int eye)
    {
        return delegate.getEyeTopRotation(living, stack, partialTick, eye);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getIrisScale(E living, PoseStack stack, float partialTick, int eye)
    {
        return delegate.getIrisScale(living, stack, partialTick, eye);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float[] getCorneaColours(E living, PoseStack stack, float partialTick, int eye)
    {
        return delegate.getCorneaColours(living, stack, partialTick, eye);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float[] getIrisColours(E living, PoseStack stack, float partialTick, int eye)
    {
        return delegate.getIrisColours(living, stack, partialTick, eye);
    }


    //HEAD FUNCTIONS
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float[] getHatOffsetFromJoint(E living, PoseStack stack, float partialTick, int head)
    {
        return delegate.getHatOffsetFromJoint(living, stack, partialTick, head);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getHatScale(E living, PoseStack stack, float partialTick, int head)
    {
        return delegate.getHatScale(living, stack, partialTick, head);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getHatYaw(E living, PoseStack stack, float partialTick, int head)
    {
        return delegate.getHatYaw(living, stack, partialTick, head);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getHatPitch(E living, PoseStack stack, float partialTick, int head)
    {
        return delegate.getHatPitch(living, stack, partialTick, head);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getHeadYaw(E living, PoseStack stack, float partialTick, int head, int eye)
    {
        return delegate.getHeadYaw(living, stack, partialTick, head, eye);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getHeadPitch(E living, PoseStack stack, float partialTick, int head, int eye)
    {
        return delegate.getHeadPitch(living, stack, partialTick, head, eye);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getHeadRoll(E living, PoseStack stack, float partialTick, int head, int eye)
    {
        return delegate.getHeadRoll(living, stack, partialTick, head, eye);
    }

    //Mathed functions that don't share the model/require matrixstack (during rendering)
    @Override
    public float getHeadYaw(E living, float partialTick, int head, int eye)
    {
        return delegate.getHeadYaw(living, partialTick, head, eye);
    }

    @Override
    public float getHeadPitch(E living, float partialTick, int head, int eye)
    {
        return delegate.getHeadPitch(living, partialTick, head, eye);
    }

    @Override
    public float getHeadRoll(E living, float partialTick, int head, int eye)
    {
        return delegate.getHeadRoll(living, partialTick, head, eye);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float[] getHeadArmorOffset(E living, PoseStack stack, float partialTick, int head)
    {
        return delegate.getHeadArmorOffset(living, stack, partialTick, head);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getHeadArmorScale(E living, PoseStack stack, float partialTick, int head)
    {
        return delegate.getHeadArmorScale(living, stack, partialTick, head);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public void postHeadTranslation(E living, PoseStack stack, float partialTick)
    {
        delegate.postHeadTranslation(living, stack, partialTick);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public void preChildEntHeadRenderCalls(E living, PoseStack stack, LivingEntityRenderer<E, ?> render)
    {
        delegate.preChildEntHeadRenderCalls(living, stack, render);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public void translateRotateToChild(E living, PoseStack stack, ModelPart renderer)
    {
        delegate.translateRotateToChild(living, stack, renderer);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public boolean setup(E living, LivingEntityRenderer renderer)
    {
        delegate = null;

        EntityModel model = renderer.getModel();
        for(HeadInfo head : multiModel)
        {
            if(head.forClass.startsWith("horseEasterEgg"))
            {
                if(head.forClass.endsWith("true") && HeadInfo.horseEasterEgg.getAsBoolean() || !head.forClass.endsWith("true") && !HeadInfo.horseEasterEgg.getAsBoolean())
                {
                    delegate = head;
                    break;
                }
            }
            else if(head.multiModelClass.isInstance(model))
            {
                delegate = head;
                break;
            }
        }

        return delegate != null;
    }

    @Override
    public boolean setup(E living)
    {
        delegate = multiModel.length > 0 ? multiModel[0] : null;
        return delegate != null;
    }

    @SuppressWarnings("rawtypes")
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public void setHeadModel(E living, LivingEntityRenderer renderer)
    {
        if(delegate.headModel == null || aggressiveHeadTracking.getAsInt() == 1 || aggressiveHeadTracking.getAsInt() == 2 && renderer instanceof PlayerRenderer)
        {
            delegate.setHeadModelFromRenderer(living, renderer, renderer.getModel());
        }
    }

    @SuppressWarnings("rawtypes")
    public void checkModels()
    {
        ArrayList<HeadInfo> multis = new ArrayList<>(Arrays.asList(multiModel)); //ArrayList from Arrays.asList is not java.util.ArrayList

        Iterator<HeadInfo> iterator = multis.iterator();
        while(iterator.hasNext())
        {
            HeadInfo head = iterator.next();
            if(!head.forClass.startsWith("horseEasterEgg"))
            {
                if(head.multiModelClass == null)
                {
                    try
                    {
                        head.multiModelClass = Class.forName(head.forClass);
                    }
                    catch(ClassNotFoundException e)
                    {
                        iterator.remove();

                        HeadInfo.LOGGER.warn("Cannot find model class, removing from head info: {}", head.forClass);
                        e.printStackTrace();
                    }
                }
            }
        }

        if(multis.size() != multiModel.length)
        {
            multiModel = multis.toArray(new HeadInfo[0]);
        }
    }
}
