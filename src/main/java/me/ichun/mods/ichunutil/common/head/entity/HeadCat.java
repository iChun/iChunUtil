package me.ichun.mods.ichunutil.common.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.common.head.HeadInfo;
import net.minecraft.entity.passive.CatEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HeadCat extends HeadInfo<CatEntity>
{
    public float[][] pupilColourAssortment;

    @OnlyIn(Dist.CLIENT)
    @Override
    public float[] getPupilColours(CatEntity living, MatrixStack stack, float partialTick, int eye)
    {
        rand.setSeed(Math.abs(living.hashCode()) * 1231L);
        return pupilColourAssortment[rand.nextInt(pupilColourAssortment.length)];
    }
}
