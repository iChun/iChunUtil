package me.ichun.mods.ichunutil.common.module.worldportals.client.render.world.chunk;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public interface IRenderChunkWorldPortal
{
    void setCurrentPositionAndFace(BlockPos pos, EnumFacing face);
}
