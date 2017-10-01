package me.ichun.mods.ichunutil.client.entity.head;

import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;

public class HeadSheep extends HeadBase<EntitySheep>
{
    public HeadSheep()
    {
        headJoint = new float[]{ 0F, -6F/16F, 8F/16F };
        eyeOffset = new float[]{ 0F, 1.5F/16F, 6F/16F };
        eyeScale = 0.65F;
    }

    @Override
    public float[] getHeadJointOffset(EntitySheep living, float partialTick, int eye)
    {
        float offset = living.getHeadRotationPointY(partialTick) * 9.0F;
        if(offset <= 0F)
        {
            return headJoint;
        }
        else
        {
            return new float[]{ 0F, -(6F + offset)/16F, 8F/16F };
        }
    }

    @Override
    public float getHeadPitch(EntitySheep living, float partialTick, int eye)
    {
        return (float)Math.toDegrees(living.getHeadRotationAngleX(partialTick));
    }

    @Override
    public float getHeadPitchForTracker(EntitySheep living)
    {
        return (float)Math.toDegrees(living.getHeadRotationAngleX(1F));
    }

    @Override
    public float getPupilScale(EntitySheep living, float partialTick, int eye)
    {
        if(living.getSheared())
        {
            return 0.5F;
        }
        return super.getPupilScale(living, partialTick, eye);
    }

    @Override
    public float[] getIrisColours(EntitySheep living, float partialTick, int eye)
    {
        if (living.hasCustomName() && "jeb_".equals(living.getCustomNameTag()))
        {
            livingRand.setSeed(Math.abs(living.hashCode()) * 9823 * (eye + 1));
            int i = living.ticksExisted / 25 + living.getEntityId() + livingRand.nextInt(25);
            int j = EnumDyeColor.values().length;
            int k = i % j;
            int l = (i + 1) % j;
            float f = ((float)(living.ticksExisted % 25) + partialTick) / 25.0F;
            float[] afloat1 = EntitySheep.getDyeRgb(EnumDyeColor.byMetadata(k));
            float[] afloat2 = EntitySheep.getDyeRgb(EnumDyeColor.byMetadata(l));
            return new float[] { afloat1[0] * (1.0F - f) + afloat2[0] * f, afloat1[1] * (1.0F - f) + afloat2[1] * f, afloat1[2] * (1.0F - f) + afloat2[2] * f };
        }
        else if(living.getSheared())
        {
            float[] afloat = EntitySheep.getDyeRgb(living.getFleeceColor());
            return afloat;
        }
        return irisColour;
    }

    @Override
    public float[] getPupilColours(EntitySheep living, float partialTick, int eye)
    {
        if (living.hasCustomName() && "jeb_".equals(living.getCustomNameTag()))
        {
            livingRand.setSeed(Math.abs(living.hashCode()) * 2145 * (eye + 1));
            int i = living.ticksExisted / 25 + living.getEntityId() + livingRand.nextInt(25);
            int j = EnumDyeColor.values().length;
            int k = i % j;
            int l = (i + 1) % j;
            float f = ((float)(living.ticksExisted % 25) + partialTick) / 25.0F;
            float[] afloat1 = EntitySheep.getDyeRgb(EnumDyeColor.byMetadata(k));
            float[] afloat2 = EntitySheep.getDyeRgb(EnumDyeColor.byMetadata(l));
            return new float[] { afloat1[0] * (1.0F - f) + afloat2[0] * f, afloat1[1] * (1.0F - f) + afloat2[1] * f, afloat1[2] * (1.0F - f) + afloat2[2] * f };
        }
        else if(living.getSheared())
        {
            return irisColour;
        }
        else
        {
            EnumDyeColor clr = living.getFleeceColor();
            if(clr != EnumDyeColor.WHITE)
            {
                float[] afloat = EntitySheep.getDyeRgb(clr);
                return afloat;
            }
            return pupilColour;
        }
    }
}
