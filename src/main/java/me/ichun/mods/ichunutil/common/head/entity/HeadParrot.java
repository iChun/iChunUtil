package me.ichun.mods.ichunutil.common.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.common.head.HeadInfo;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HeadParrot extends HeadInfo<ParrotEntity>
{
    @OnlyIn(Dist.CLIENT)
    @Override
    public float getPupilScale(ParrotEntity living, MatrixStack stack, float partialTick, int eye)
    {
        return super.getPupilScale(living, stack, partialTick, eye) * (living.isPartying() ? 1.6F : 1F);
    }
}
