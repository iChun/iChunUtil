package me.ichun.mods.ichunutil.client.render.entity;

import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.entity.EntityBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.ArrayList;

@SuppressWarnings("deprecation")
public class RenderBlock extends Render<EntityBlock>
{
    public ArrayList<Class<? extends TileEntity>> classesNotToRender = new ArrayList<Class<? extends TileEntity>>();

    public RenderBlock(RenderManager renderManager)
    {
        super(renderManager);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityBlock entity)
    {
        return TextureMap.locationBlocksTexture;
    }

    @Override
    public void doRender(EntityBlock entBlock, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if(!entBlock.setup)
        {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        if(entBlock.getCanRotate())
        {
            GlStateManager.translate(0D, entBlock.height / 2D, 0D);

            GlStateManager.rotate(EntityHelper.interpolateRotation(entBlock.prevRotYaw, entBlock.rotYaw, partialTicks), 0F, 1F, 0F);
            GlStateManager.rotate(EntityHelper.interpolateRotation(entBlock.prevRotPitch, entBlock.rotPitch, partialTicks), 0F, 0F, 1F);

            GlStateManager.translate(0D, -(entBlock.height / 2D), 0D);
        }

        GlStateManager.translate(0.5D * (entBlock.blocks.length - 1), 1.0D * (entBlock.blocks[0].length - 1), 0.5D * (entBlock.blocks[0][0].length - 1));
        GlStateManager.disableLighting();

        BlockPos blockpos = new BlockPos(entBlock);
        World world = entBlock.worldObj;

        for(int ii = 0; ii < entBlock.blocks.length; ii++)
        {
            for(int jj = 0; jj < entBlock.blocks[ii].length; jj++)
            {
                for(int kk = 0; kk < entBlock.blocks[ii][jj].length; kk++)
                {
                    if(entBlock.blocks[ii][jj][kk] != null)
                    {
                        IBlockState iblockstate = entBlock.blocks[ii][jj][kk];
                        Block block = iblockstate.getBlock();

                        GlStateManager.pushMatrix();
                        GlStateManager.translate(-ii, -jj, -kk);

                        if (iblockstate != world.getBlockState(blockpos) && block.getRenderType() != -1)
                        {
                            if (block.getRenderType() == 3)
                            {
                                bindTexture(TextureMap.locationBlocksTexture);

                                Tessellator tessellator = Tessellator.getInstance();
                                WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                                worldrenderer.begin(7, DefaultVertexFormats.BLOCK);
                                int i = blockpos.getX();
                                int j = blockpos.getY();
                                int k = blockpos.getZ();
                                worldrenderer.setTranslation((double)((float)(-i) - 0.5F), (double)(-j), (double)((float)(-k) - 0.5F));
                                BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
                                IBakedModel ibakedmodel = blockrendererdispatcher.getModelFromBlockState(iblockstate, world, (BlockPos)null);
                                blockrendererdispatcher.getBlockModelRenderer().renderModel(world, ibakedmodel, iblockstate, blockpos, worldrenderer, false);
                                worldrenderer.setTranslation(0.0D, 0.0D, 0.0D);
                                tessellator.draw();
                            }
                        }
                        if(entBlock.tileEntityNBTs[ii][jj][kk] != null && block.hasTileEntity(iblockstate))
                        {
                            if(entBlock.renderingTileEntities == null)
                            {
                                entBlock.renderingTileEntities = new TileEntity[entBlock.blocks.length][entBlock.blocks[ii].length][entBlock.blocks[ii][jj].length];
                            }
                            if(entBlock.renderingTileEntities[ii][jj][kk] == null)
                            {
                                TileEntity te = block.createTileEntity(entBlock.worldObj, iblockstate);
                                te.readFromNBT((NBTTagCompound)entBlock.tileEntityNBTs[ii][jj][kk].copy());
                                entBlock.renderingTileEntities[ii][jj][kk] = te;
                            }
                            if(!classesNotToRender.contains(entBlock.renderingTileEntities[ii][jj][kk].getClass()))
                            {
                                try
                                {
                                    TileEntityRendererDispatcher.instance.renderTileEntityAt(entBlock.renderingTileEntities[ii][jj][kk], -0.5D, 0.0D, -0.5D, partialTicks);
                                }
                                catch(ReportedException e)
                                {
                                    classesNotToRender.add(entBlock.renderingTileEntities[ii][jj][kk].getClass());
                                }
                            }
                        }

                        GlStateManager.popMatrix();
                    }
                }
            }
        }
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
