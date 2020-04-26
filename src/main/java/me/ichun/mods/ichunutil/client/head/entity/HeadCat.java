package me.ichun.mods.ichunutil.client.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.head.HeadBase;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.OcelotModel;
import net.minecraft.entity.passive.CatEntity;

public class HeadCat extends HeadBase<CatEntity>
{
    public float[][] pupilColourAssortment;

    public HeadCat()
    {
        eyeOffset = new float[]{ 0F, 1F/16F, 3F/16F };
        halfInterpupillaryDistance = 1.5F / 16F;
        eyeScale = 0.7F;
        pupilColourAssortment = new float[][] {
                new float[] { 13F / 255F , 127F / 255F, 200F / 255F },
                new float[] { 77F / 255F , 137F / 255F, 13F / 255F },
                new float[] { 31F / 255F , 111F / 255F, 50F / 255F },
                new float[] { 187F / 255F , 137F / 255F, 16F / 255F },
                new float[] { 18F / 255F , 25F / 255F, 40F / 255F }
        };
    }

    @Override
    public float[] getPupilColours(CatEntity living, MatrixStack stack, float partialTick, int eye)
    {
        livingRand.setSeed(Math.abs(living.hashCode()) * 1231);
        return pupilColourAssortment[livingRand.nextInt(pupilColourAssortment.length)];
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void setHeadModelFromRenderer(LivingRenderer renderer)
    {
        EntityModel model = renderer.getEntityModel();
        if(model instanceof OcelotModel)
        {
            this.headModel[0] = ((OcelotModel)model).ocelotHead;
        }
    }
}
