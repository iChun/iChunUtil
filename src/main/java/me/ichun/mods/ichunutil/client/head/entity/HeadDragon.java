package me.ichun.mods.ichunutil.client.head.entity;

import me.ichun.mods.ichunutil.client.head.HeadBase;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;

public class HeadDragon extends HeadBase<EnderDragonEntity>
{
    public HeadDragon() //WARNING. ENDER DRAGON USES A DIFFERENT HOOK THAT ISN'T A LAYERRENDERER-RELATED HOOK
    {
        eyeOffset = new float[] { 0F, 4F/16F, 10F/16F };
        eyeScale = 2F;
        halfInterpupillaryDistance = 5F/16F;
        pupilColour = new float[] { 204F / 255F, 0F, 250F / 255F };
    }

    @Override
    public boolean affectedByInvisibility(EnderDragonEntity living, int eye)
    {
        return false;
    }

    @Override
    public boolean doesEyeGlow(EnderDragonEntity living, int eye)
    {
        return true;
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void setHeadModelFromRenderer(LivingRenderer renderer)
    {
        EntityModel model = renderer.getEntityModel();
        if(model instanceof EnderDragonRenderer.EnderDragonModel)
        {
            this.headModel[0] = ((EnderDragonRenderer.EnderDragonModel)model).head;
        }
    }
}
