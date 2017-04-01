package me.ichun.mods.ichunutil.common.module.worldportals.client.render.world.factory;

import me.ichun.mods.ichunutil.common.module.worldportals.client.render.world.chunk.RenderChunkWorldPortal;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.world.World;

public class VboChunkFactory implements IRenderChunkFactory
{
    public RenderChunk create(World worldIn, RenderGlobal globalRenderer, int index)
    {
        return new RenderChunkWorldPortal(worldIn, globalRenderer, index);
    }
}