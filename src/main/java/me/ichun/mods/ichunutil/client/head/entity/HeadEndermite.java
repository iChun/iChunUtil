package me.ichun.mods.ichunutil.client.head.entity;

import me.ichun.mods.ichunutil.client.head.HeadBase;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EndermiteModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.monster.EndermiteEntity;

public class HeadEndermite extends HeadBase<EndermiteEntity>
{
    public HeadEndermite()
    {
        eyeOffset = new float[]{ 0F, 0F, 1F/16F };
        irisColour = new float[] { 87F/255F, 23F/255F, 50F/255F };
        halfInterpupillaryDistance = 0F;
        eyeScale = 0.6F;
    }

    @Override
    public int getEyeCount(EndermiteEntity living)
    {
        return 1;
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void setHeadModelFromRenderer(LivingRenderer renderer)
    {
        EntityModel model = renderer.getEntityModel();
        if(model instanceof EndermiteModel)
        {
            this.headModel[0] = ((EndermiteModel)model).bodyParts[0];
        }
    }
}
