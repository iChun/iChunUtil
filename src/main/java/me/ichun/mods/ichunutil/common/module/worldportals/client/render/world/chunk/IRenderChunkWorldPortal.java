package me.ichun.mods.ichunutil.common.module.worldportals.client.render.world.chunk;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public interface IRenderChunkWorldPortal
{
    void setCurrentPositionsAndFaces(ArrayList<BlockPos> poses, ArrayList<EnumFacing> face);
}
