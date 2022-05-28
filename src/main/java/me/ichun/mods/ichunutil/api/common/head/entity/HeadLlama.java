package me.ichun.mods.ichunutil.api.common.head.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import net.minecraft.world.entity.animal.horse.Llama;


public class HeadLlama extends HeadInfo<Llama>
{
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getHeadYaw(Llama living, PoseStack stack, float partialTick, int head, int eye)
    {
        return HeadInfo.horseEasterEgg.getAsBoolean() ? 180F : super.getHeadYaw(living, stack, partialTick, head, eye);
    }

    @Override
    public float getHeadYaw(Llama living, float partialTick, int head, int eye)
    {
        return HeadInfo.horseEasterEgg.getAsBoolean() ? (living.yBodyRotO + (living.yBodyRot - living.yBodyRotO) * partialTick) - 180F : super.getHeadYaw(living, partialTick, head, eye);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getHeadPitch(Llama living, PoseStack stack, float partialTick, int head, int eye)
    {
        return HeadInfo.horseEasterEgg.getAsBoolean() ? 0F : super.getHeadPitch(living, stack, partialTick, head, eye);
    }

    @Override
    public float getHeadPitch(Llama living, float partialTick, int head, int eye)
    {
        return HeadInfo.horseEasterEgg.getAsBoolean() ? 0F : super.getHeadPitch(living, partialTick, head, eye);
    }
}
