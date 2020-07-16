package me.ichun.mods.ichunutil.client.head.entity;

import me.ichun.mods.ichunutil.client.head.HeadBase;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.GuardianModel;
import net.minecraft.entity.monster.GuardianEntity;

public class HeadGuardian extends HeadBase<GuardianEntity>
{
    public HeadGuardian()
    {
        eyeOffset = new float[]{ 0F, -16F/16F, 8F/16F };
        irisColour = new float[] { 214F / 255F, 211F / 255F, 203F / 255F };
        pupilColour = new float[] { 101F / 255F, 35F / 255F, 31F / 255F };
        halfInterpupillaryDistance = 0F;
        eyeScale = 2F;
    }

    @Override
    public int getEyeCount(GuardianEntity living)
    {
        return 1;
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void setHeadModelFromRenderer(LivingRenderer renderer)
    {
        EntityModel model = renderer.getEntityModel();
        if(model instanceof GuardianModel)
        {
            this.headModel[0] = ((GuardianModel)model).guardianBody;
        }
    }
}
