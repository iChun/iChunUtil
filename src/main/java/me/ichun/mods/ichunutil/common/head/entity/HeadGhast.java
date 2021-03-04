package me.ichun.mods.ichunutil.common.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.common.head.HeadInfo;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HeadGhast extends HeadInfo<GhastEntity>
{
    @Override
    public float getEyeScale(GhastEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(living.isAttacking())
        {
            return eyeScale;
        }
        return 0F;
    }
}
