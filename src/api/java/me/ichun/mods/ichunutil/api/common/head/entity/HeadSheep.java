package me.ichun.mods.ichunutil.api.common.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HeadSheep extends HeadInfo<SheepEntity>
{
    @OnlyIn(Dist.CLIENT)
    @Override
    public float getIrisScale(SheepEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(living.getSheared())
        {
            return 0.5F;
        }
        return super.getIrisScale(living, stack, partialTick, eye);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float[] getCorneaColours(SheepEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if (living.hasCustomName() && "jeb_".equals(living.getName().getUnformattedComponentText()))
        {
            rand.setSeed(Math.abs(living.hashCode()) * 9823L * (eye + 1));
            int i = living.ticksExisted / 25 + living.getEntityId() + rand.nextInt(25);
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
        return corneaColour;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float[] getIrisColours(SheepEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if (living.hasCustomName() && "jeb_".equals(living.getName().getUnformattedComponentText()))
        {
            rand.setSeed(Math.abs(living.hashCode()) * 2145L * (eye + 1));
            int i = living.ticksExisted / 25 + living.getEntityId() + rand.nextInt(25);
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
            return corneaColour;
        }
        else
        {
            DyeColor clr = living.getFleeceColor();
            if(clr != DyeColor.WHITE)
            {
                return SheepEntity.getDyeRgb(clr);
            }
            return irisColour;
        }
    }
}
