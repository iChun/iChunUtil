package me.ichun.mods.ichunutil.client.render.entity;

import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.entity.EntityBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import java.util.HashSet;

public class RenderBlock extends Render<EntityBlock>
{
    public HashSet<Class<? extends TileEntity>> classesNotToRender = new HashSet<>();

    public RenderBlock(RenderManager renderManager)
    {
        super(renderManager);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityBlock entity)
    {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
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
        BlockPos blockposMaxY = new BlockPos(entBlock.posX, entBlock.getEntityBoundingBox().maxY, entBlock.posZ);
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

                        if (iblockstate.getRenderType() == EnumBlockRenderType.MODEL)
                        {
                            if (iblockstate != world.getBlockState(blockpos) && iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE)
                            {
                                this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                                GlStateManager.pushMatrix();
                                GlStateManager.disableLighting();
                                Tessellator tessellator = Tessellator.getInstance();
                                VertexBuffer vertexbuffer = tessellator.getBuffer();

                                if (this.renderOutlines)
                                {
                                    GlStateManager.enableColorMaterial();
                                    GlStateManager.enableOutlineMode(this.getTeamColor(entBlock));
                                }

                                vertexbuffer.begin(7, DefaultVertexFormats.BLOCK);
                                GlStateManager.translate((float)(x - (double)blockposMaxY.getX() - 0.5D), (float)(y - (double)blockposMaxY.getY()), (float)(z - (double)blockposMaxY.getZ() - 0.5D));
                                BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
                                blockrendererdispatcher.getBlockModelRenderer().renderModel(world, blockrendererdispatcher.getModelForState(iblockstate), iblockstate, blockposMaxY, vertexbuffer, false, MathHelper.getPositionRandom(entBlock.getOrigin().add(ii, jj, kk)));
                                tessellator.draw();

                                if (this.renderOutlines)
                                {
                                    GlStateManager.disableOutlineMode();
                                    GlStateManager.disableColorMaterial();
                                }

                                GlStateManager.enableLighting();
                                GlStateManager.popMatrix();
                                super.doRender(entBlock, x, y, z, entityYaw, partialTicks);
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
                                te.readFromNBT(entBlock.tileEntityNBTs[ii][jj][kk].copy());
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

    public static class RenderFactory implements IRenderFactory<EntityBlock>
    {
        @Override
        public Render<EntityBlock> createRenderFor(RenderManager manager)
        {
            return new RenderBlock(manager);
        }
    }
}
