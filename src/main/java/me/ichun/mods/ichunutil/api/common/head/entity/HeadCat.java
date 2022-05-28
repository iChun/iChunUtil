package me.ichun.mods.ichunutil.api.common.head.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import net.minecraft.world.entity.animal.Cat;


public class HeadCat extends HeadInfo<Cat>
{
    public float[][] pupilColourAssortment;

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float[] getIrisColours(Cat living, PoseStack stack, float partialTick, int eye)
    {
        rand.setSeed(Math.abs(living.hashCode()) * 1231L);
        return pupilColourAssortment[rand.nextInt(pupilColourAssortment.length)];
    }
}
