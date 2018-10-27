package me.ichun.mods.ichunutil.common.module.worldportals.client.render.world.chunk;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.ListedRenderChunk;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.client.FMLClientHandler;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class ListedRenderChunkWorldPortal extends ListedRenderChunk implements IRenderChunkWorldPortal
{
    public BlockPos pos;
    public EnumFacing face;
    public boolean noCull;
    public boolean wasCulled;

    public ListedRenderChunkWorldPortal(World worldIn, RenderGlobal renderGlobalIn, int indexIn)
    {
        super(worldIn, renderGlobalIn, indexIn);
        this.pos = BlockPos.ORIGIN;
        this.face = EnumFacing.UP;
    }

    @Override
    public void setCurrentPositionsAndFaces(BlockPos pos, EnumFacing face)
    {
        this.pos = pos;
        this.face = face;
    }

    @Override
    public void setNoCull(boolean flag)
    {
        if(flag && noCull != flag && wasCulled == flag)
        {
            setNeedsUpdate(false);
        }
        this.noCull = flag;
    }

    @Override
    public void rebuildChunk(float x, float y, float z, ChunkCompileTaskGenerator generator)
    {
        wasCulled = false;
        CompiledChunk compiledchunk = new CompiledChunk();
        int i = 1;
        BlockPos blockpos = this.position;
        BlockPos blockpos1 = blockpos.add(15, 15, 15);
        generator.getLock().lock();

        try
        {
            if(generator.getStatus() != ChunkCompileTaskGenerator.Status.COMPILING)
            {
                return;
            }

            generator.setCompiledChunk(compiledchunk);
        }
        finally
        {
            generator.getLock().unlock();
        }

        VisGraph lvt_9_1_ = new VisGraph();
        HashSet lvt_10_1_ = Sets.newHashSet();

        ChunkCache worldView = getWorldView();
        if(!worldView.isEmpty())
        {
            ++renderChunksUpdated;
            boolean[] aboolean = new boolean[BlockRenderLayer.values().length];
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

            for(BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(blockpos, blockpos1))
            {
                boolean noRender = !noCull && (face.getXOffset() < 0 && blockpos$mutableblockpos.getX() > pos.getX() || face.getXOffset() > 0 && blockpos$mutableblockpos.getX() < pos.getX() || face.getYOffset() < 0 && blockpos$mutableblockpos.getY() > pos.getY() || face.getYOffset() > 0 && blockpos$mutableblockpos.getY() < pos.getY() || face.getZOffset() < 0 && blockpos$mutableblockpos.getZ() > pos.getZ() || face.getZOffset() > 0 && blockpos$mutableblockpos.getZ() < pos.getZ());
                if(noRender)
                {
                    wasCulled = true;
                }
                IBlockState iblockstate = worldView.getBlockState(blockpos$mutableblockpos);
                Block block = iblockstate.getBlock();

                if(iblockstate.isOpaqueCube())
                {
                    lvt_9_1_.setOpaqueCube(blockpos$mutableblockpos);
                }

                if(!noRender && block.hasTileEntity(iblockstate))
                {
                    TileEntity tileentity = worldView.getTileEntity(blockpos$mutableblockpos, Chunk.EnumCreateEntityType.CHECK);

                    if (tileentity != null)
                    {
                        TileEntitySpecialRenderer<TileEntity> tileentityspecialrenderer = TileEntityRendererDispatcher.instance.getRenderer(tileentity);

                        if (tileentityspecialrenderer != null)
                        {
                            compiledchunk.addTileEntity(tileentity);

                            if (tileentityspecialrenderer.isGlobalRenderer(tileentity))
                            {
                                lvt_10_1_.add(tileentity);
                            }
                        }
                    }
                }

                for(BlockRenderLayer blockrenderlayer1 : BlockRenderLayer.values())
                {
                    if(!block.canRenderInLayer(iblockstate, blockrenderlayer1))
                    {
                        continue;
                    }
                    net.minecraftforge.client.ForgeHooksClient.setRenderLayer(blockrenderlayer1);
                    int j = blockrenderlayer1.ordinal();

                    if(block.getDefaultState().getRenderType() != EnumBlockRenderType.INVISIBLE)
                    {
                        net.minecraft.client.renderer.BufferBuilder bufferbuilder = generator.getRegionRenderCacheBuilder().getWorldRendererByLayerId(j);

                        if(!compiledchunk.isLayerStarted(blockrenderlayer1))
                        {
                            compiledchunk.setLayerStarted(blockrenderlayer1);
                            this.preRenderBlocks(bufferbuilder, blockpos);
                        }

                        if(!noRender && blockrendererdispatcher.renderBlock(iblockstate, blockpos$mutableblockpos, worldView, bufferbuilder))
                        {
                            aboolean[j] |= true;
                        }
                    }
                }
                net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
            }

            for(BlockRenderLayer blockrenderlayer : BlockRenderLayer.values())
            {
                if(aboolean[blockrenderlayer.ordinal()])
                {
                    compiledchunk.setLayerUsed(blockrenderlayer);
                }

                if(compiledchunk.isLayerStarted(blockrenderlayer))
                {
                    this.postRenderBlocks(blockrenderlayer, x, y, z, generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(blockrenderlayer), compiledchunk);
                }
            }
        }

        compiledchunk.setVisibility(lvt_9_1_.computeVisibility());
        this.lockCompileTask.lock();

        try
        {
            Set<TileEntity> set = Sets.newHashSet(lvt_10_1_);
            Set<TileEntity> set1 = Sets.newHashSet(this.setTileEntities);
            set.removeAll(this.setTileEntities);
            set1.removeAll(lvt_10_1_);
            this.setTileEntities.clear();
            this.setTileEntities.addAll(lvt_10_1_);
            this.renderGlobal.updateTileEntities(set1, set);
        }
        finally
        {
            this.lockCompileTask.unlock();
        }
    }

    //Optifine support stuffs
    private ChunkCompileTaskGenerator optiCompileTask;
    public ChunkCache optiWorldView;

    public ChunkCache getWorldView()
    {
        if(FMLClientHandler.instance().hasOptifine())
        {
            return optiWorldView;
        }
        else
        {
            return worldView;
        }
    }

    @Override
    protected void finishCompileTask()
    {
        if(!FMLClientHandler.instance().hasOptifine()) { super.finishCompileTask(); return; }

        this.lockCompileTask.lock();

        try
        {
            if (this.optiCompileTask != null && this.optiCompileTask.getStatus() != ChunkCompileTaskGenerator.Status.DONE)
            {
                this.optiCompileTask.finish();
                this.optiCompileTask = null;
            }
        }
        finally
        {
            this.lockCompileTask.unlock();
        }
    }

    @Override
    public ChunkCompileTaskGenerator makeCompileTaskChunk()
    {
        if(!FMLClientHandler.instance().hasOptifine()) { return super.makeCompileTaskChunk(); }

        this.lockCompileTask.lock();
        ChunkCompileTaskGenerator chunkcompiletaskgenerator;

        try
        {
            this.finishCompileTask();
            this.optiCompileTask = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.REBUILD_CHUNK, this.getDistanceSq());
            this.rebuildWorldView();
            chunkcompiletaskgenerator = this.optiCompileTask;
        }
        finally
        {
            this.lockCompileTask.unlock();
        }

        return chunkcompiletaskgenerator;
    }

    private void rebuildWorldView()
    {
        int i = 1;
        ChunkCache cache = createRegionRenderCache(this.world, this.position.add(-1, -1, -1), this.position.add(16, 16, 16), 1);
        net.minecraftforge.client.MinecraftForgeClient.onRebuildChunk(this.world, this.position, cache);
        this.optiWorldView = cache;
    }

    @Nullable
    @Override
    public ChunkCompileTaskGenerator makeCompileTaskTransparency()
    {
        if(!FMLClientHandler.instance().hasOptifine()) { return super.makeCompileTaskTransparency(); }

        this.lockCompileTask.lock();
        ChunkCompileTaskGenerator chunkcompiletaskgenerator;

        try
        {
            if (this.optiCompileTask == null || this.optiCompileTask.getStatus() != ChunkCompileTaskGenerator.Status.PENDING)
            {
                if (this.optiCompileTask != null && this.optiCompileTask.getStatus() != ChunkCompileTaskGenerator.Status.DONE)
                {
                    this.optiCompileTask.finish();
                    this.optiCompileTask = null;
                }

                this.optiCompileTask = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY, this.getDistanceSq());
                this.optiCompileTask.setCompiledChunk(this.compiledChunk);
                chunkcompiletaskgenerator = this.optiCompileTask;
                return chunkcompiletaskgenerator;
            }

            chunkcompiletaskgenerator = null;
        }
        finally
        {
            this.lockCompileTask.unlock();
        }

        return chunkcompiletaskgenerator;
    }
}
