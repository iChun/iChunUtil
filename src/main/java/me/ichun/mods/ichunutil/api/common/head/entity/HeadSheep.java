package me.ichun.mods.ichunutil.api.common.head.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;


public class HeadSheep extends HeadInfo<Sheep>
{
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getIrisScale(Sheep living, PoseStack stack, float partialTick, int eye)
    {
        if(living.isSheared())
        {
            return 0.5F;
        }
        return super.getIrisScale(living, stack, partialTick, eye);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float[] getCorneaColours(Sheep living, PoseStack stack, float partialTick, int eye)
    {
        if (living.hasCustomName() && "jeb_".equals(living.getName().getContents()))
        {
            rand.setSeed(Math.abs(living.hashCode()) * 9823L * (eye + 1));
            int i = living.tickCount / 25 + living.getId() + rand.nextInt(25);
            int j = DyeColor.values().length;
            int k = i % j;
            int l = (i + 1) % j;
            float f = ((float)(living.tickCount % 25) + partialTick) / 25.0F;
            float[] afloat1 = Sheep.getColorArray(DyeColor.byId(k));
            float[] afloat2 = Sheep.getColorArray(DyeColor.byId(l));
            return new float[] { afloat1[0] * (1.0F - f) + afloat2[0] * f, afloat1[1] * (1.0F - f) + afloat2[1] * f, afloat1[2] * (1.0F - f) + afloat2[2] * f };
        }
        else if(living.isSheared())
        {
            float[] afloat = Sheep.getColorArray(living.getColor());
            return afloat;
        }
        return corneaColour;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float[] getIrisColours(Sheep living, PoseStack stack, float partialTick, int eye)
    {
        if (living.hasCustomName() && "jeb_".equals(living.getName().getContents()))
        {
            rand.setSeed(Math.abs(living.hashCode()) * 2145L * (eye + 1));
            int i = living.tickCount / 25 + living.getId() + rand.nextInt(25);
            int j = DyeColor.values().length;
            int k = i % j;
            int l = (i + 1) % j;
            float f = ((float)(living.tickCount % 25) + partialTick) / 25.0F;
            float[] afloat1 = Sheep.getColorArray(DyeColor.byId(k));
            float[] afloat2 = Sheep.getColorArray(DyeColor.byId(l));
            return new float[] { afloat1[0] * (1.0F - f) + afloat2[0] * f, afloat1[1] * (1.0F - f) + afloat2[1] * f, afloat1[2] * (1.0F - f) + afloat2[2] * f };
        }
        else if(living.isSheared())
        {
            return corneaColour;
        }
        else
        {
            DyeColor clr = living.getColor();
            if(clr != DyeColor.WHITE)
            {
                return Sheep.getColorArray(clr);
            }
            return irisColour;
        }
    }
}
