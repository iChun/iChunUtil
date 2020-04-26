package me.ichun.mods.ichunutil.client.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.head.HeadBase;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.QuadrupedModel;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.DyeColor;

public class HeadSheep extends HeadBase<SheepEntity>
{
    public HeadSheep()
    {
        eyeOffset = new float[]{ 0F, 1.5F/16F, 6F/16F };
        eyeScale = 0.65F;
    }

    @Override
    public float getPupilScale(SheepEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(living.getSheared())
        {
            return 0.5F;
        }
        return super.getPupilScale(living, stack, partialTick, eye);
    }

    @Override
    public float[] getIrisColours(SheepEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if (living.hasCustomName() && "jeb_".equals(living.getName().getUnformattedComponentText()))
        {
            livingRand.setSeed(Math.abs(living.hashCode()) * 9823 * (eye + 1));
            int i = living.ticksExisted / 25 + living.getEntityId() + livingRand.nextInt(25);
            int j = DyeColor.values().length;
            int k = i % j;
            int l = (i + 1) % j;
            float f = ((float)(living.ticksExisted % 25) + partialTick) / 25.0F;
            float[] afloat1 = SheepEntity.getDyeRgb(DyeColor.byId(k));
            float[] afloat2 = SheepEntity.getDyeRgb(DyeColor.byId(l));
            return new float[] { afloat1[0] * (1.0F - f) + afloat2[0] * f, afloat1[1] * (1.0F - f) + afloat2[1] * f, afloat1[2] * (1.0F - f) + afloat2[2] * f };
        }
        else if(living.getSheared())
        {
            float[] afloat = SheepEntity.getDyeRgb(living.getFleeceColor());
            return afloat;
        }
        return irisColour;
    }

    @Override
    public float[] getPupilColours(SheepEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if (living.hasCustomName() && "jeb_".equals(living.getName().getUnformattedComponentText()))
        {
            livingRand.setSeed(Math.abs(living.hashCode()) * 2145 * (eye + 1));
            int i = living.ticksExisted / 25 + living.getEntityId() + livingRand.nextInt(25);
            int j = DyeColor.values().length;
            int k = i % j;
            int l = (i + 1) % j;
            float f = ((float)(living.ticksExisted % 25) + partialTick) / 25.0F;
            float[] afloat1 = SheepEntity.getDyeRgb(DyeColor.byId(k));
            float[] afloat2 = SheepEntity.getDyeRgb(DyeColor.byId(l));
            return new float[] { afloat1[0] * (1.0F - f) + afloat2[0] * f, afloat1[1] * (1.0F - f) + afloat2[1] * f, afloat1[2] * (1.0F - f) + afloat2[2] * f };
        }
        else if(living.getSheared())
        {
            return irisColour;
        }
        else
        {
            DyeColor clr = living.getFleeceColor();
            if(clr != DyeColor.WHITE)
            {
                float[] afloat = SheepEntity.getDyeRgb(clr);
                return afloat;
            }
            return pupilColour;
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void setHeadModelFromRenderer(LivingRenderer renderer)
    {
        EntityModel model = renderer.getEntityModel();
        if(model instanceof QuadrupedModel)
        {
            this.headModel[0] = ((QuadrupedModel)model).headModel;
        }
    }
}
