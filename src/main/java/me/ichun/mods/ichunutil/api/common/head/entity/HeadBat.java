package me.ichun.mods.ichunutil.api.common.head.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import net.minecraft.world.entity.ambient.Bat;


public class HeadBat extends HeadInfo<Bat>
{
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getHeadYaw(Bat living, PoseStack stack, float partialTick, int head, int eye)
    {
        if(living.isResting())
        {
            return -super.getHeadYaw(living, stack, partialTick, head, eye);
        }
        else
        {
            return super.getHeadYaw(living, stack, partialTick, head, eye);
        }
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getHeadYaw(Bat living, float partialTick, int head, int eye)
    {
        if(living.isResting())
        {
            return -super.getHeadYaw(living, partialTick, head, eye);
        }
        else
        {
            return super.getHeadYaw(living, partialTick, head, eye);
        }
    }
}
