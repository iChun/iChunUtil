package me.ichun.mods.ichunutil.api.common.head.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import me.ichun.mods.ichunutil.api.common.head.HeadInfo;
import net.minecraft.world.entity.monster.Shulker;


public class HeadShulker extends HeadInfo<Shulker>
{
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getEyeScale(Shulker living, PoseStack stack, float partialTick, int eye)
    {
        if(living.getClientPeekAmount(partialTick) <= 0F)
        {
            return 0F;
        }
        return eyeScale;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float[] getHeadJointOffset(Shulker living, PoseStack stack, float partialTick, int head)
    {
        switch (living.getAttachFace())
        {
            case DOWN:
            default:
                break;
            case EAST:
                stack.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
                stack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
                stack.translate(1.0F, -1.0F, 0.0F);
                stack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                break;
            case WEST:
                stack.mulPose(Vector3f.ZP.rotationDegrees(-90.0F));
                stack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
                stack.translate(-1.0F, -1.0F, 0.0F);
                stack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                break;
            case NORTH:
                stack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
                stack.translate(0.0F, -1.0F, -1.0F);
                break;
            case SOUTH:
                stack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
                stack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
                stack.translate(0.0F, -1.0F, 1.0F);
                break;
            case UP:
                stack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
                stack.translate(0.0F, -2.0F, 0.0F);
        }
        return super.getHeadJointOffset(living, stack, partialTick, head);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    @Override
    public float getHeadPitch(Shulker living, PoseStack stack, float partialTick, int head, int eye)
    {
        return 0F;
    }

    @Override
    public float getHeadPitch(Shulker living, float partialTick, int head, int eye)
    {
        return 0F;
    }
}
