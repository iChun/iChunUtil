package me.ichun.mods.ichunutil.api.common.head;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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


    @OnlyIn(Dist.CLIENT)
    @Override
    public float[] getHeadJointOffset(E living, MatrixStack stack, float partialTick, int head)
    {
        return delegate.getHeadJointOffset(living, stack, partialTick, head);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float[] getEyeOffsetFromJoint(E living, MatrixStack stack, float partialTick, int eye)
    {
        return delegate.getEyeOffsetFromJoint(living, stack, partialTick, eye);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getEyeSideOffset(E living, MatrixStack stack, float partialTick, int eye)
    {
        return delegate.getEyeSideOffset(living, stack, partialTick, eye);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getEyeScale(E living, MatrixStack stack, float partialTick, int eye) //base eye scale size
    {
        return delegate.getEyeScale(living, stack, partialTick, eye);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getEyeRotation(E living, MatrixStack stack, float partialTick, int eye)
    {
        return delegate.getEyeRotation(living, stack, partialTick, eye);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getEyeTopRotation(E living, MatrixStack stack, float partialTick, int eye)
    {
        return delegate.getEyeTopRotation(living, stack, partialTick, eye);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getIrisScale(E living, MatrixStack stack, float partialTick, int eye)
    {
        return delegate.getIrisScale(living, stack, partialTick, eye);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float[] getCorneaColours(E living, MatrixStack stack, float partialTick, int eye)
    {
        return delegate.getCorneaColours(living, stack, partialTick, eye);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float[] getIrisColours(E living, MatrixStack stack, float partialTick, int eye)
    {
        return delegate.getIrisColours(living, stack, partialTick, eye);
    }


    //HEAD FUNCTIONS
    @OnlyIn(Dist.CLIENT)
    @Override
    public float[] getHatOffsetFromJoint(E living, MatrixStack stack, float partialTick, int head)
    {
        return delegate.getHatOffsetFromJoint(living, stack, partialTick, head);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getHatScale(E living, MatrixStack stack, float partialTick, int head)
    {
        return delegate.getHatScale(living, stack, partialTick, head);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getHatYaw(E living, MatrixStack stack, float partialTick, int head)
    {
        return delegate.getHatYaw(living, stack, partialTick, head);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getHatPitch(E living, MatrixStack stack, float partialTick, int head)
    {
        return delegate.getHatPitch(living, stack, partialTick, head);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getHeadYaw(E living, MatrixStack stack, float partialTick, int head, int eye)
    {
        return delegate.getHeadYaw(living, stack, partialTick, head, eye);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getHeadPitch(E living, MatrixStack stack, float partialTick, int head, int eye)
    {
        return delegate.getHeadPitch(living, stack, partialTick, head, eye);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getHeadRoll(E living, MatrixStack stack, float partialTick, int head, int eye)
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

    @OnlyIn(Dist.CLIENT)
    @Override
    public float[] getHeadArmorOffset(E living, MatrixStack stack, float partialTick, int head)
    {
        return delegate.getHeadArmorOffset(living, stack, partialTick, head);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getHeadArmorScale(E living, MatrixStack stack, float partialTick, int head)
    {
        return delegate.getHeadArmorScale(living, stack, partialTick, head);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void postHeadTranslation(E living, MatrixStack stack, float partialTick)
    {
        delegate.postHeadTranslation(living, stack, partialTick);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void preChildEntHeadRenderCalls(E living, MatrixStack stack, LivingRenderer<E, ?> render)
    {
        delegate.preChildEntHeadRenderCalls(living, stack, render);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void translateRotateToChild(E living, MatrixStack stack, ModelRenderer renderer)
    {
        delegate.translateRotateToChild(living, stack, renderer);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean setup(E living, LivingRenderer renderer)
    {
        delegate = null;

        EntityModel model = renderer.getEntityModel();
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
    @OnlyIn(Dist.CLIENT)
    @Override
    public void setHeadModel(E living, LivingRenderer renderer)
    {
        if(delegate.headModel == null || aggressiveHeadTracking.getAsInt() == 1 || aggressiveHeadTracking.getAsInt() == 2 && renderer instanceof PlayerRenderer)
        {
            delegate.setHeadModelFromRenderer(living, renderer, renderer.getEntityModel());
        }
    }

    @SuppressWarnings("rawtypes")
    public void checkModels()
    {
        List<HeadInfo> multis = Arrays.asList(multiModel);

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
