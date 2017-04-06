package me.ichun.mods.ichunutil.common.module.worldportals.common.core;

import me.ichun.mods.ichunutil.api.worldportals.IApi;
import me.ichun.mods.ichunutil.common.module.worldportals.client.render.WorldPortalRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ApiImpl implements IApi
{
    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderLevel()
    {
        return WorldPortalRenderer.renderLevel;
    }
}
