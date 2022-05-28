package me.ichun.mods.ichunutil.api.common.head.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import net.minecraft.world.entity.monster.MagmaCube;


public class HeadMagmaCube extends HeadInfo<MagmaCube>
{
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getEyeScale(MagmaCube living, PoseStack stack, float partialTick, int eye)
    {
        float squishFactor = living.oSquish + (living.squish - living.oSquish) * partialTick;
        if(squishFactor <= 0F)
        {
            return eyeScale;
        }
        else
        {
            return eyeScale + squishFactor * 1.5F;
        }
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float[] getHeadJointOffset(MagmaCube living, PoseStack stack, float partialTick, int head)
    {
        float squishFactor = living.oSquish + (living.squish - living.oSquish) * partialTick;
        if(squishFactor <= 0F)
        {
            return super.getHeadJointOffset(living, stack, partialTick, head);
        }
        else
        {
            return new float[]{ 0F, -(0 - squishFactor * 2.5F)/16F, 0F };
        }
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getHeadArmorScale(MagmaCube living, PoseStack stack, float partialTick, int head)
    {
        float squishFactor = living.oSquish + (living.squish - living.oSquish) * partialTick;
        if(squishFactor > 0F)
        {
            stack.scale(1F, 1F + squishFactor * 4F, 1F);
        }

        return super.getHeadArmorScale(living, stack, partialTick, head);
    }
}
