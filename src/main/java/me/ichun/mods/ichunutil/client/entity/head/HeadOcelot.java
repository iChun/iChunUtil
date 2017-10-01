package me.ichun.mods.ichunutil.client.entity.head;

import net.minecraft.entity.passive.EntityOcelot;

public class HeadOcelot extends HeadBase<EntityOcelot>
{
    public float pupilColourAssortment[][];

    public HeadOcelot()
    {
        eyeOffset = new float[]{ 0F, 1F/16F, 3F/16F };
        halfInterpupillaryDistance = 1.5F / 16F;
        eyeScale = 0.7F;
        pupilColourAssortment = new float[][] {
                new float[] { 13F / 255F , 127F / 255F, 200F / 255F },
                new float[] { 77F / 255F , 137F / 255F, 13F / 255F },
                new float[] { 31F / 255F , 111F / 255F, 50F / 255F }
        };
    }

    @Override
    public float[] getPupilColours(EntityOcelot living, float partialTick, int eye)
    {
        livingRand.setSeed(Math.abs(living.hashCode()) * 1231);
        return pupilColourAssortment[livingRand.nextInt(3)];
    }
}
