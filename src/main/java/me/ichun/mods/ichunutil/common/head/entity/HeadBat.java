package me.ichun.mods.ichunutil.common.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.common.head.HeadInfo;
import net.minecraft.entity.passive.BatEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HeadBat extends HeadInfo<BatEntity>
{
    @OnlyIn(Dist.CLIENT)
    @Override
    public float getHeadYaw(BatEntity living, MatrixStack stack, float partialTick, int eye, int head)
    {
        if(living.getIsBatHanging())
        {
            return -super.getHeadYaw(living, stack, partialTick, eye, head);
        }
        else
        {
            return super.getHeadYaw(living, stack, partialTick, eye, head);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getHeadYaw(BatEntity living, float partialTick, int eye, int head)
    {
        if(living.getIsBatHanging())
        {
            return -super.getHeadYaw(living, partialTick, eye, head);
        }
        else
        {
            return super.getHeadYaw(living, partialTick, eye, head);
        }
    }
}
