package me.ichun.mods.ichunutil.api.common.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HeadShulker extends HeadInfo<ShulkerEntity>
{
    @OnlyIn(Dist.CLIENT)
    @Override
    public float getEyeScale(ShulkerEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(living.getClientPeekAmount(partialTick) <= 0F)
        {
            return 0F;
        }
        return eyeScale;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float[] getHeadJointOffset(ShulkerEntity living, MatrixStack stack, float partialTick, int eye, int head)
    {
        switch (living.getAttachmentFacing())
        {
            case DOWN:
            default:
                break;
            case EAST:
                stack.rotate(Vector3f.ZP.rotationDegrees(90.0F));
                stack.rotate(Vector3f.XP.rotationDegrees(90.0F));
                stack.translate(1.0F, -1.0F, 0.0F);
                stack.rotate(Vector3f.YP.rotationDegrees(180.0F));
                break;
            case WEST:
                stack.rotate(Vector3f.ZP.rotationDegrees(-90.0F));
                stack.rotate(Vector3f.XP.rotationDegrees(90.0F));
                stack.translate(-1.0F, -1.0F, 0.0F);
                stack.rotate(Vector3f.YP.rotationDegrees(180.0F));
                break;
            case NORTH:
                stack.rotate(Vector3f.XP.rotationDegrees(90.0F));
                stack.translate(0.0F, -1.0F, -1.0F);
                break;
            case SOUTH:
                stack.rotate(Vector3f.ZP.rotationDegrees(180.0F));
                stack.rotate(Vector3f.XP.rotationDegrees(90.0F));
                stack.translate(0.0F, -1.0F, 1.0F);
                break;
            case UP:
                stack.rotate(Vector3f.XP.rotationDegrees(180.0F));
                stack.translate(0.0F, -2.0F, 0.0F);
        }
        return super.getHeadJointOffset(living, stack, partialTick, eye, head);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getHeadPitch(ShulkerEntity living, MatrixStack stack, float partialTick, int eye, int head)
    {
        return 0F;
    }

    @Override
    public float getHeadPitch(ShulkerEntity living, float partialTick, int eye, int head)
    {
        return 0F;
    }
}
