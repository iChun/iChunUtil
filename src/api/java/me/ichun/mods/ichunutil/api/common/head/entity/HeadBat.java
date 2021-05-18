package me.ichun.mods.ichunutil.api.common.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import net.minecraft.entity.passive.BatEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HeadBat extends HeadInfo<BatEntity>
{
    @OnlyIn(Dist.CLIENT)
    @Override
    public float getHeadYaw(BatEntity living, MatrixStack stack, float partialTick, int head, int eye)
    {
        if(living.getIsBatHanging())
        {
            return -super.getHeadYaw(living, stack, partialTick, head, eye);
        }
        else
        {
            return super.getHeadYaw(living, stack, partialTick, head, eye);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getHeadYaw(BatEntity living, float partialTick, int head, int eye)
    {
        if(living.getIsBatHanging())
        {
            return -super.getHeadYaw(living, partialTick, head, eye);
        }
        else
        {
            return super.getHeadYaw(living, partialTick, head, eye);
        }
    }
}
