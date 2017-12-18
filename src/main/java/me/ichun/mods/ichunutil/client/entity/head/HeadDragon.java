package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.boss.EntityDragon;

public class HeadDragon extends HeadBase<EntityDragon>
{
    public float yaw, pitch, roll;

    public HeadDragon()
    {
        eyeOffset = new float[] { 0F, 4F/16F, 10F/16F };
        eyeScale = 2F;
        halfInterpupillaryDistance = 5F/16F;
        pupilColour = new float[] { 204F / 255F, 0F, 250F / 255F };
    }

    @Override
    public float getHeadYaw(EntityDragon living, float partialTick, int eye)
    {
        return yaw;
    }

    @Override
    public float getHeadPitch(EntityDragon living, float partialTick, int eye)
    {
        return pitch;
    }

    @Override
    public float[] getHeadJointOffset(EntityDragon living, float partialTick, int eye)
    {
        float f = living.prevAnimTime + (living.animTime - living.prevAnimTime) * partialTick;
        float f1 = (float)(Math.sin((double)(f * ((float)Math.PI * 2F) - 1.0F)) + 1.0D);
        f1 = (f1 * f1 + f1 * 2.0F) * 0.05F;
        GlStateManager.translate(0.0F, f1 - 2.0F, -3.0F);
        GlStateManager.rotate(f1 * 2.0F, 1.0F, 0.0F, 0.0F);

        float f2 = 20.0F;
        float f4 = 0.0F;
        double[] adouble = living.getMovementOffsets(6, partialTick);
        float f6 = this.updateRotations(living.getMovementOffsets(5, partialTick)[0] - living.getMovementOffsets(10, partialTick)[0]);
        float f7 = this.updateRotations(living.getMovementOffsets(5, partialTick)[0] + (double)(f6 / 2.0F));
        float f8 = f * ((float)Math.PI * 2F);
        float f3 = -12.0F;

        double rotateAngleX, rotateAngleY;
        for (int i = 0; i < 5; ++i)
        {
            double[] adouble1 = living.getMovementOffsets(5 - i, partialTick);
            float f9 = (float)Math.cos((double)((float)i * 0.45F + f8)) * 0.15F;
            rotateAngleY = this.updateRotations(adouble1[0] - adouble[0]) * 0.017453292F * 1.5F;
            rotateAngleX = f9 + living.getHeadPartYOffset(i, adouble, adouble1) * 0.017453292F * 1.5F * 5.0F;
            f2 = (float)((double)f2 + Math.sin(rotateAngleX) * 10.0D);
            f3 = (float)((double)f3 - Math.cos(rotateAngleY) * Math.cos(rotateAngleX) * 10.0D);
            f4 = (float)((double)f4 - Math.sin(rotateAngleY) * Math.cos(rotateAngleX) * 10.0D);
        }

        headJoint[1] = -(f2 / 16F);
        headJoint[2] = -(f3 / 16F);
        headJoint[0] = -(f4 / 16F);

        double[] adouble2 = living.getMovementOffsets(0, partialTick);
        yaw = (float)Math.toDegrees(this.updateRotations(adouble2[0] - adouble[0]) * 0.017453292F);
        pitch = (float)Math.toDegrees(this.updateRotations((double)living.getHeadPartYOffset(6, adouble, adouble2)) * 0.017453292F * 1.5F * 5.0F);
        roll = (float)Math.toDegrees(-this.updateRotations(adouble2[0] - (double)f7) * 0.017453292F);

        return headJoint;
    }

    @Override
    public boolean affectedByInvisibility(EntityDragon living, int eye)
    {
        return false;
    }

    @Override
    public boolean doesEyeGlow(EntityDragon living, int eye)
    {
        return true;
    }


    private float updateRotations(double p_78214_1_)
    {
        while (p_78214_1_ >= 180.0D)
        {
            p_78214_1_ -= 360.0D;
        }

        while (p_78214_1_ < -180.0D)
        {
            p_78214_1_ += 360.0D;
        }

        return (float)p_78214_1_;
    }

}
