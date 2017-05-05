package me.ichun.mods.ichunutil.common.module.worldportals.client.render.world;

import com.google.common.collect.Lists;
import me.ichun.mods.ichunutil.common.module.worldportals.client.render.world.chunk.IRenderChunkWorldPortal;
import me.ichun.mods.ichunutil.common.module.worldportals.client.render.world.factory.ListChunkFactory;
import me.ichun.mods.ichunutil.common.module.worldportals.client.render.world.factory.VboChunkFactory;
import me.ichun.mods.ichunutil.common.module.worldportals.common.portal.WorldPortal;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.*;

public class RenderGlobalProxy extends RenderGlobal
{
    public HashMap<WorldPortal, ViewFrustum> usedViewFrustums = new HashMap<>();
    public ArrayList<ViewFrustum> freeViewFrustums = new ArrayList<>();
    public float playerPrevYaw;
    public float playerPrevPitch;
    public float playerYaw;
    public float playerPitch;
    public float playerPrevHeadYaw;
    public float playerHeadYaw;
    public double playerPosX;
    public double playerPosY;
    public double playerPosZ;
    public double playerPrevPosX;
    public double playerPrevPosY;
    public double playerPrevPosZ;
    public double playerLastTickX;
    public double playerLastTickY;
    public double playerLastTickZ;

    public boolean released;

    public RenderGlobalProxy(Minecraft mcIn)
    {
        super(mcIn);

        if(this.vboEnabled)
        {
            this.renderContainer = new VboRenderList();
            this.renderChunkFactory = new VboChunkFactory();
        }
        else
        {
            this.renderContainer = new RenderList();
            this.renderChunkFactory = new ListChunkFactory();
        }
    }

    @Override
    public void loadRenderers()
    {
        if(this.theWorld != null)
        {
            if(this.renderDispatcher == null)
            {
                this.renderDispatcher = new ChunkRenderDispatcher();
            }

            this.displayListEntitiesDirty = true;
            Blocks.LEAVES.setGraphicsLevel(this.mc.gameSettings.fancyGraphics);
            Blocks.LEAVES2.setGraphicsLevel(this.mc.gameSettings.fancyGraphics);
            this.renderDistanceChunks = this.mc.gameSettings.renderDistanceChunks;
            boolean flag = this.vboEnabled;
            this.vboEnabled = OpenGlHelper.useVbo();

            if(flag && !this.vboEnabled)
            {
                this.renderContainer = new RenderList();
                this.renderChunkFactory = new ListChunkFactory();
            }
            else if(!flag && this.vboEnabled)
            {
                this.renderContainer = new VboRenderList();
                this.renderChunkFactory = new VboChunkFactory();
            }

            if(flag != this.vboEnabled)
            {
                this.generateStars();
                this.generateSky();
                this.generateSky2();
            }

            cleanViewFrustums();

            this.stopChunkUpdates();

            synchronized(this.setTileEntities)
            {
                this.setTileEntities.clear();
            }

            this.renderEntitiesStartupCounter = 2;
        }
    }

    public void cleanViewFrustums()
    {
        for(Map.Entry<WorldPortal, ViewFrustum> e : usedViewFrustums.entrySet())
        {
            e.getValue().deleteGlResources();
            if(e.getValue() == viewFrustum)
            {
                viewFrustum = null;
            }
        }
        usedViewFrustums.clear();
        for(ViewFrustum frustum : freeViewFrustums)
        {
            frustum.deleteGlResources();
            if(frustum == viewFrustum)
            {
                viewFrustum = null;
            }
        }
        freeViewFrustums.clear();
        if(this.viewFrustum != null)
        {
            this.viewFrustum.deleteGlResources();
        }
        freeViewFrustums.add(new ViewFrustum(this.theWorld, this.mc.gameSettings.renderDistanceChunks, this, this.renderChunkFactory));
        viewFrustum = freeViewFrustums.get(0);
    }

    public void releaseViewFrustum(WorldPortal pm)
    {
        ViewFrustum vf = usedViewFrustums.get(pm);
        if(vf != null)
        {
            freeViewFrustums.add(vf);
            usedViewFrustums.remove(pm);
        }
    }

    public void bindViewFrustum(WorldPortal pm)
    {
        ViewFrustum vf = usedViewFrustums.get(pm);
        if(vf == null)
        {
            if(freeViewFrustums.isEmpty())
            {
                vf = new ViewFrustum(this.theWorld, this.mc.gameSettings.renderDistanceChunks, this, this.renderChunkFactory);
            }
            else
            {
                vf = freeViewFrustums.get(0);
                freeViewFrustums.remove(0);
            }
            usedViewFrustums.put(pm, vf);

            if(this.theWorld != null)
            {
                Entity entity = this.mc.getRenderViewEntity();

                if(entity != null)
                {
                    vf.updateChunkPositions(entity.posX, entity.posZ);
                }
            }
        }
        viewFrustum = vf;
        for(RenderChunk renderChunk : viewFrustum.renderChunks)
        {
            if(renderChunk instanceof IRenderChunkWorldPortal)
            {
                ((IRenderChunkWorldPortal)renderChunk).setCurrentPositionAndFace(pm.getPos(), pm.getFaceOn());
            }
        }
    }

    public void storePlayerInfo()
    {
        playerPrevYaw = mc.thePlayer.prevRotationYaw;
        playerPrevPitch = mc.thePlayer.prevRotationPitch;
        playerYaw = mc.thePlayer.rotationYaw;
        playerPitch = mc.thePlayer.rotationPitch;
        playerPrevHeadYaw = mc.thePlayer.prevRotationYawHead;
        playerHeadYaw = mc.thePlayer.rotationYawHead;
        playerPosX = mc.thePlayer.posX;
        playerPosY = mc.thePlayer.posY;
        playerPosZ = mc.thePlayer.posZ;
        playerPrevPosX = mc.thePlayer.prevPosX;
        playerPrevPosY = mc.thePlayer.prevPosY;
        playerPrevPosZ = mc.thePlayer.prevPosZ;
        playerLastTickX = mc.thePlayer.lastTickPosX;
        playerLastTickY = mc.thePlayer.lastTickPosY;
        playerLastTickZ = mc.thePlayer.lastTickPosZ;
    }

    @Override
    public void renderEntities(Entity renderViewEntity, ICamera camera, float partialTicks)
    {
        renderEntities(renderViewEntity, camera, partialTicks, null, null);
    }

    public void renderEntities(Entity renderViewEntity, ICamera camera, float partialTicks, @Nullable WorldPortal portal, @Nullable WorldPortal pair) // we are rendering the pair's perspective
    {
        int pass = net.minecraftforge.client.MinecraftForgeClient.getRenderPass();
        if(this.renderEntitiesStartupCounter > 0)
        {
            if(pass > 0)
            {
                return;
            }
            --this.renderEntitiesStartupCounter;
        }
        else
        {
            double d0 = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * (double)partialTicks;
            double d1 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * (double)partialTicks;
            double d2 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * (double)partialTicks;
            TileEntityRendererDispatcher.instance.prepare(this.theWorld, this.mc.getTextureManager(), this.mc.fontRendererObj, this.mc.getRenderViewEntity(), this.mc.objectMouseOver, partialTicks);
            this.renderManager.cacheActiveRenderInfo(this.theWorld, this.mc.fontRendererObj, this.mc.getRenderViewEntity(), this.mc.pointedEntity, this.mc.gameSettings, partialTicks);
            if(pass == 0)
            {
                this.countEntitiesTotal = 0;
                this.countEntitiesRendered = 0;
                this.countEntitiesHidden = 0;
            }
            Entity entity = this.mc.getRenderViewEntity();
            double d3 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
            double d4 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
            double d5 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
            TileEntityRendererDispatcher.staticPlayerX = d3;
            TileEntityRendererDispatcher.staticPlayerY = d4;
            TileEntityRendererDispatcher.staticPlayerZ = d5;
            this.renderManager.setRenderPosition(d3, d4, d5);
            this.mc.entityRenderer.enableLightmap();
            List<Entity> list = this.theWorld.getLoadedEntityList();
            if(pass == 0)
            {
                this.countEntitiesTotal = list.size();
            }

            for(int i = 0; i < this.theWorld.weatherEffects.size(); ++i)
            {
                Entity entity1 = this.theWorld.weatherEffects.get(i);
                if(pair != null && !shouldRenderEntity(entity1, pair) || !entity1.shouldRenderInPass(pass))
                {
                    continue;
                }
                ++this.countEntitiesRendered;

                if(entity1.isInRangeToRender3d(d0, d1, d2))
                {
                    this.renderManager.renderEntityStatic(entity1, partialTicks, false);
                }
            }

            List<Entity> list1 = Lists.newArrayList();
            List<Entity> list2 = Lists.newArrayList();

            float prevYaw = mc.thePlayer.prevRotationYaw;
            float prevPitch = mc.thePlayer.prevRotationPitch;
            float yaw = mc.thePlayer.rotationYaw;
            float pitch = mc.thePlayer.rotationPitch;
            float prevYawHead = mc.thePlayer.prevRotationYawHead;
            float yawHead = mc.thePlayer.rotationYawHead;
            double posX = mc.thePlayer.posX;
            double posY = mc.thePlayer.posY;
            double posZ = mc.thePlayer.posZ;
            double prevPosX = mc.thePlayer.prevPosX;
            double prevPosY = mc.thePlayer.prevPosY;
            double prevPosZ = mc.thePlayer.prevPosZ;
            double lastX = mc.thePlayer.lastTickPosX;
            double lastY = mc.thePlayer.lastTickPosY;
            double lastZ = mc.thePlayer.lastTickPosZ;

            mc.thePlayer.prevRotationYaw = playerPrevYaw;
            mc.thePlayer.prevRotationPitch = playerPrevPitch;
            mc.thePlayer.rotationYaw = playerYaw;
            mc.thePlayer.rotationPitch = playerPitch;
            mc.thePlayer.prevRotationYawHead = playerPrevHeadYaw;
            mc.thePlayer.rotationYawHead = playerHeadYaw;
            mc.thePlayer.posX = playerPosX;
            mc.thePlayer.posY = playerPosY;
            mc.thePlayer.posZ = playerPosZ;
            mc.thePlayer.prevPosX = playerPrevPosX;
            mc.thePlayer.prevPosY = playerPrevPosY;
            mc.thePlayer.prevPosZ = playerPrevPosZ;
            mc.thePlayer.lastTickPosX = playerLastTickX;
            mc.thePlayer.lastTickPosY = playerLastTickY;
            mc.thePlayer.lastTickPosZ = playerLastTickZ;

            for(RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation : this.renderInfos)
            {
                Chunk chunk = this.theWorld.getChunkFromBlockCoords(renderglobal$containerlocalrenderinformation.renderChunk.getPosition());
                ClassInheritanceMultiMap<Entity> classinheritancemultimap = chunk.getEntityLists()[renderglobal$containerlocalrenderinformation.renderChunk.getPosition().getY() / 16];

                if(!classinheritancemultimap.isEmpty())
                {
                    for(Entity entity2 : classinheritancemultimap)
                    {
                        if(portal != null && pair != null && !(entity2 == mc.thePlayer && mc.gameSettings.thirdPersonView == 0) && portal.lastScanEntities.contains(entity2) && portal.getPortalInsides(entity2).intersectsWith(entity2.getEntityBoundingBox()))
                        {
                            double eePosX = entity2.lastTickPosX + (entity2.posX - entity2.lastTickPosX) * (double)partialTicks;
                            double eePosY = entity2.lastTickPosY + (entity2.posY - entity2.lastTickPosY) * (double)partialTicks;
                            double eePosZ = entity2.lastTickPosZ + (entity2.posZ - entity2.lastTickPosZ) * (double)partialTicks;

                            AxisAlignedBB flatPlane = portal.getFlatPlane();
                            double centerX = (flatPlane.maxX + flatPlane.minX) / 2D;
                            double centerY = (flatPlane.maxY + flatPlane.minY) / 2D;
                            double centerZ = (flatPlane.maxZ + flatPlane.minZ) / 2D;

                            AxisAlignedBB pairFlatPlane = portal.getPair().getFlatPlane();
                            double destX = (pairFlatPlane.maxX + pairFlatPlane.minX) / 2D;
                            double destY = (pairFlatPlane.maxY + pairFlatPlane.minY) / 2D;
                            double destZ = (pairFlatPlane.maxZ + pairFlatPlane.minZ) / 2D;
                            GlStateManager.pushMatrix();

                            double rotX = eePosX - d3;
                            double rotY = eePosY - d4;
                            double rotZ = eePosZ - d5;

                            float[] appliedOffset = portal.getQuaternionFormula().applyPositionalRotation(new float[] { (float)(eePosX - centerX), (float)(eePosY - centerY), (float)(eePosZ - centerZ) });
                            float[] appliedRot = portal.getQuaternionFormula().applyRotationalRotation(new float[] {
                                    180F,
                                    0F,
                                    0F
                            });

                            GlStateManager.translate(destX - eePosX + appliedOffset[0], destY - eePosY + appliedOffset[1], destZ - eePosZ + appliedOffset[2]);
                            GlStateManager.translate(rotX, rotY, rotZ);

                            GlStateManager.rotate(-appliedRot[0], 0F, 1F, 0F);
                            GlStateManager.rotate(-appliedRot[1], 1F, 0F, 0F);
                            GlStateManager.rotate(-appliedRot[2], 0F, 0F, 1F);

                            GlStateManager.translate(-(rotX), -(rotY), -(rotZ));

                            this.renderManager.renderEntityStatic(entity2, partialTicks, false);

                            GlStateManager.popMatrix();
                        }

                        if(pair != null && !shouldRenderEntity(entity2, pair) || !entity2.shouldRenderInPass(pass))
                        {
                            continue;
                        }
                        boolean flag = this.renderManager.shouldRender(entity2, camera, d0, d1, d2) || entity2 == mc.thePlayer || entity2.isRidingOrBeingRiddenBy(this.mc.thePlayer);

                        if(flag && (entity2.posY < 0.0D || entity2.posY >= 256.0D || this.theWorld.isBlockLoaded(new BlockPos(entity2))))
                        {
                            ++this.countEntitiesRendered;
                            boolean disableStencil = false;
                            if(pair != null && pair.lastScanEntities.contains(entity2) && pair.portalInsides.intersectsWith(entity2.getEntityBoundingBox()))
                            {
                                disableStencil = true;
                            }
                            if(disableStencil)
                            {
                                GL11.glDisable(GL11.GL_STENCIL_TEST);
                            }
                            this.renderManager.renderEntityStatic(entity2, partialTicks, false);
                            if(disableStencil)
                            {
                                GL11.glEnable(GL11.GL_STENCIL_TEST);
                            }

                            if(this.isOutlineActive(entity2, entity, camera))
                            {
                                list1.add(entity2);
                            }

                            if(this.renderManager.isRenderMultipass(entity2))
                            {
                                list2.add(entity2);
                            }
                        }
                    }
                }
            }

            if(!list2.isEmpty())
            {
                for(Entity entity3 : list2)
                {
                    this.renderManager.renderMultipass(entity3, partialTicks);
                }
            }

            if(this.isRenderEntityOutlines() && (!list1.isEmpty() || this.entityOutlinesRendered))
            {
                this.entityOutlineFramebuffer.framebufferClear();
                this.entityOutlinesRendered = !list1.isEmpty();

                if(!list1.isEmpty())
                {
                    GlStateManager.depthFunc(519);
                    GlStateManager.disableFog();
                    this.entityOutlineFramebuffer.bindFramebuffer(false);
                    RenderHelper.disableStandardItemLighting();
                    this.renderManager.setRenderOutlines(true);

                    for(int j = 0; j < ((List)list1).size(); ++j)
                    {
                        this.renderManager.renderEntityStatic(list1.get(j), partialTicks, false);
                    }

                    this.renderManager.setRenderOutlines(false);
                    RenderHelper.enableStandardItemLighting();
                    GlStateManager.depthMask(false);
                    this.entityOutlineShader.loadShaderGroup(partialTicks);
                    GlStateManager.enableLighting();
                    GlStateManager.depthMask(true);
                    GlStateManager.enableFog();
                    GlStateManager.enableBlend();
                    GlStateManager.enableColorMaterial();
                    GlStateManager.depthFunc(515);
                    GlStateManager.enableDepth();
                    GlStateManager.enableAlpha();
                }

                this.mc.getFramebuffer().bindFramebuffer(false);
            }

            mc.thePlayer.prevRotationYaw = prevYaw;
            mc.thePlayer.prevRotationPitch = prevPitch;
            mc.thePlayer.rotationYaw = yaw;
            mc.thePlayer.rotationPitch = pitch;
            mc.thePlayer.prevRotationYawHead = prevYawHead;
            mc.thePlayer.rotationYawHead = yawHead;
            mc.thePlayer.posX = posX;
            mc.thePlayer.posY = posY;
            mc.thePlayer.posZ = posZ;
            mc.thePlayer.prevPosX = prevPosX;
            mc.thePlayer.prevPosY = prevPosY;
            mc.thePlayer.prevPosZ = prevPosZ;
            mc.thePlayer.lastTickPosX = lastX;
            mc.thePlayer.lastTickPosY = lastY;
            mc.thePlayer.lastTickPosZ = lastZ;

            RenderHelper.enableStandardItemLighting();

            TileEntityRendererDispatcher.instance.preDrawBatch();
            for(RenderGlobal.ContainerLocalRenderInformation renderglobal$containerlocalrenderinformation1 : this.renderInfos)
            {
                List<TileEntity> list3 = renderglobal$containerlocalrenderinformation1.renderChunk.getCompiledChunk().getTileEntities();

                if(!list3.isEmpty())
                {
                    for(TileEntity tileentity2 : list3)
                    {
                        if(!tileentity2.shouldRenderInPass(pass) || !camera.isBoundingBoxInFrustum(tileentity2.getRenderBoundingBox()))
                        {
                            continue;
                        }
                        TileEntityRendererDispatcher.instance.renderTileEntity(tileentity2, partialTicks, -1);
                    }
                }
            }

            synchronized(this.setTileEntities)
            {
                for(TileEntity tileentity : this.setTileEntities)
                {
                    if(!tileentity.shouldRenderInPass(pass) || !camera.isBoundingBoxInFrustum(tileentity.getRenderBoundingBox()))
                    {
                        continue;
                    }
                    TileEntityRendererDispatcher.instance.renderTileEntity(tileentity, partialTicks, -1);
                }
            }
            TileEntityRendererDispatcher.instance.drawBatch(pass);

            this.preRenderDamagedBlocks();

            for(DestroyBlockProgress destroyblockprogress : this.damagedBlocks.values())
            {
                BlockPos blockpos = destroyblockprogress.getPosition();
                TileEntity tileentity1 = this.theWorld.getTileEntity(blockpos);

                if(tileentity1 instanceof TileEntityChest)
                {
                    TileEntityChest tileentitychest = (TileEntityChest)tileentity1;

                    if(tileentitychest.adjacentChestXNeg != null)
                    {
                        blockpos = blockpos.offset(EnumFacing.WEST);
                        tileentity1 = this.theWorld.getTileEntity(blockpos);
                    }
                    else if(tileentitychest.adjacentChestZNeg != null)
                    {
                        blockpos = blockpos.offset(EnumFacing.NORTH);
                        tileentity1 = this.theWorld.getTileEntity(blockpos);
                    }
                }

                Block block = this.theWorld.getBlockState(blockpos).getBlock();

                if(tileentity1 != null && tileentity1.shouldRenderInPass(pass) && tileentity1.canRenderBreaking() && camera.isBoundingBoxInFrustum(tileentity1.getRenderBoundingBox()))
                {
                    TileEntityRendererDispatcher.instance.renderTileEntity(tileentity1, partialTicks, destroyblockprogress.getPartialBlockDamage());
                }
            }

            this.postRenderDamagedBlocks();
            this.mc.entityRenderer.disableLightmap();
        }
    }

    public boolean shouldRenderEntity(Entity ent, WorldPortal portal)
    {
        return !(portal.getFaceOn().getFrontOffsetX() < 0 && ent.posX > portal.getFlatPlane().minX || portal.getFaceOn().getFrontOffsetX() > 0 && ent.posX < portal.getFlatPlane().minX ||
                portal.getFaceOn().getFrontOffsetY() < 0 && (ent.getEntityBoundingBox().maxY + ent.getEntityBoundingBox().minY) / 2D > portal.getFlatPlane().minY ||
                portal.getFaceOn().getFrontOffsetY() > 0 && (ent.getEntityBoundingBox().maxY + ent.getEntityBoundingBox().minY) / 2D < portal.getFlatPlane().minY ||
                portal.getFaceOn().getFrontOffsetZ() < 0 && ent.posZ > portal.getFlatPlane().minZ || portal.getFaceOn().getFrontOffsetZ() > 0 && ent.posZ < portal.getFlatPlane().minZ);
    }

    @Override
    public void markBlocksForUpdate(int p_184385_1_, int p_184385_2_, int p_184385_3_, int p_184385_4_, int p_184385_5_, int p_184385_6_, boolean p_184385_7_)
    {
        for(Map.Entry<WorldPortal, ViewFrustum> e : usedViewFrustums.entrySet())
        {
            e.getValue().markBlocksForUpdate(p_184385_1_, p_184385_2_, p_184385_3_, p_184385_4_, p_184385_5_, p_184385_6_, p_184385_7_);
        }
        for(ViewFrustum frustum : freeViewFrustums)
        {
            frustum.markBlocksForUpdate(p_184385_1_, p_184385_2_, p_184385_3_, p_184385_4_, p_184385_5_, p_184385_6_, p_184385_7_);
        }
    }

    @Override
    public void playRecord(@Nullable SoundEvent soundIn, BlockPos pos) {}

    @Override
    public void playSoundToAllNearExcept(@Nullable EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) {}

    @Override
    public void broadcastSound(int soundID, BlockPos pos, int data) {}

    public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {}
}