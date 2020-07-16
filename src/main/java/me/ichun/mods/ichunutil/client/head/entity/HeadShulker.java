package me.ichun.mods.ichunutil.client.head.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.head.HeadBase;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.ShulkerModel;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.util.math.vector.Vector3f;

public class HeadShulker extends HeadBase<ShulkerEntity>
{
    public HeadShulker()
    {
        eyeOffset = new float[] { 0F, -3.5F/16F, 3F/16F };
        halfInterpupillaryDistance = 2F/16F;
        eyeScale = 0.6F;
    }

    @Override
    public float getEyeScale(ShulkerEntity living, MatrixStack stack, float partialTick, int eye)
    {
        if(living.getClientPeekAmount(partialTick) <= 0F)
        {
            return 0F;
        }
        return eyeScale;
    }

    @Override
    public float[] getHeadJointOffset(ShulkerEntity living, MatrixStack stack, float partialTick, int eye)
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
        return super.getHeadJointOffset(living, stack, partialTick, eye);
    }

    @Override
    public float getHeadPitch(ShulkerEntity living, MatrixStack stack, float partialTick, int eye)
    {
        return 0F;
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void setHeadModelFromRenderer(LivingRenderer renderer)
    {
        EntityModel model = renderer.getEntityModel();
        if(model instanceof ShulkerModel)
        {
            this.headModel[0] = ((ShulkerModel)model).head;
        }
    }
}
