package me.ichun.mods.ichunutil.api.common.head.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import net.minecraft.world.entity.monster.Ghast;


public class HeadGhast extends HeadInfo<Ghast>
{
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getEyeScale(Ghast living, PoseStack stack, float partialTick, int eye)
    {
        if(living.isCharging())
        {
            return eyeScale;
        }
        return 0F;
    }
}
