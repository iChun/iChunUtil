package me.ichun.mods.ichunutil.api.common.head.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import net.minecraft.world.entity.monster.EnderMan;


public class HeadEnderman extends HeadInfo<EnderMan>
{
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getIrisScale(EnderMan living, PoseStack stack, float partialTick, int eye)
    {
        if(living.isCreepy())
        {
            return 0.4F;
        }
        return super.getIrisScale(living, stack, partialTick, eye);
    }
}
