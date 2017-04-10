package me.ichun.mods.ichunutil.api.worldportals;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ApiDummy implements IApi
{
    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderLevel()
    {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getCameraRoll(int renderLevel, float partialTick)
    {
        return 0F;
    }
}
