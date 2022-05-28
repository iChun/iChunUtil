package me.ichun.mods.ichunutil.api.common.head.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import net.minecraft.world.entity.animal.horse.AbstractHorse;


public class HeadBodyHorse extends HeadInfo<AbstractHorse>
{
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getHeadYaw(AbstractHorse living, PoseStack stack, float partialTick, int head, int eye)
    {
        return 180F;
    }

    @Override
    public float getHeadYaw(AbstractHorse living, float partialTick, int head, int eye)
    {
        return (living.yBodyRotO + (living.yBodyRot - living.yBodyRotO) * partialTick) - 180F;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getHeadPitch(AbstractHorse living, PoseStack stack, float partialTick, int head, int eye)
    {
        return (float)Math.toDegrees(living.getStandAnim(partialTick) * ((float)Math.PI / 4F));
    }

    @Override
    public float getHeadPitch(AbstractHorse living, float partialTick, int head, int eye)
    {
        return (float)Math.toDegrees(living.getStandAnim(partialTick) * ((float)Math.PI / 4F));
    }
}
