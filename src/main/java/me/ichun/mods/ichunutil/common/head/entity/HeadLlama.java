package me.ichun.mods.ichunutil.common.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.LlamaModel;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HeadLlama extends HeadHorse
{
    @OnlyIn(Dist.CLIENT)
    @Override
    public float getEyeRotation(AbstractHorseEntity living, MatrixStack stack, float partialTick, int eye)
    {
        return 0F;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getHeadPitch(AbstractHorseEntity living, MatrixStack stack, float partialTick, int eye, int head)
    {
        return iChunUtil.configClient.horseEasterEgg ? 0F : super.getHeadPitch(living, stack, partialTick, eye, head);
    }

    @Override
    public float getHeadPitch(AbstractHorseEntity living, float partialTick, int eye, int head)
    {
        return iChunUtil.configClient.horseEasterEgg ? 0F : super.getHeadPitch(living, partialTick, eye, head);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void preChildEntHeadRenderCalls(AbstractHorseEntity living, MatrixStack stack, LivingRenderer render)
    {
        if(living.isChild()) //I don't like this if statement any more than you do.
        {
            float modelScale = 0.0625F;
            if(iChunUtil.configClient.horseEasterEgg)
            {
                stack.scale(0.625F, 0.45454544F, 0.45454544F);
                stack.translate(0.0F, 33.0F * modelScale, 0.0F);
            }
            else
            {
                stack.scale(0.71428573F, 0.64935064F, 0.7936508F);
                stack.translate(0.0F, 21.0F * modelScale, 0.22F);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    @SuppressWarnings("rawtypes")
    protected void setHeadModelFromRenderer(LivingRenderer renderer)
    {
        EntityModel model = renderer.getEntityModel();
        if(model instanceof LlamaModel)
        {
            this.headModel[0] = iChunUtil.configClient.horseEasterEgg ? ((LlamaModel)model).body : ((LlamaModel)model).head;
        }
    }
}
