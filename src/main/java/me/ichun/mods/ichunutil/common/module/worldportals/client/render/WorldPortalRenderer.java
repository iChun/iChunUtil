package me.ichun.mods.ichunutil.common.module.worldportals.client.render;

import me.ichun.mods.ichunutil.client.render.RendererHelper;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.worldportals.client.render.culling.ClippingHelperPortal;
import me.ichun.mods.ichunutil.common.module.worldportals.client.render.culling.Frustum;
import me.ichun.mods.ichunutil.common.module.worldportals.client.render.world.RenderGlobalProxy;
import me.ichun.mods.ichunutil.common.module.worldportals.common.WorldPortals;
import me.ichun.mods.ichunutil.common.module.worldportals.common.portal.EntityTransformationStack;
import me.ichun.mods.ichunutil.common.module.worldportals.common.portal.WorldPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import java.util.ArrayList;
import java.util.Queue;

public class WorldPortalRenderer
{
    public static int renderLevel = 0;
    public static int renderCount = 0;
    public static int frameCount;
    public static ArrayList<Float> rollFactor = new ArrayList<>();

    public static float getRollFactor(int level, float partialTick)
    {
        if(level <= 0)
        {
            return EntityHelper.interpolateValues(WorldPortals.eventHandlerClient.prevCameraRoll, WorldPortals.eventHandlerClient.cameraRoll, partialTick);
        }
        else if(level <= rollFactor.size())
        {
            float roll = EntityHelper.interpolateValues(WorldPortals.eventHandlerClient.prevCameraRoll, WorldPortals.eventHandlerClient.cameraRoll, partialTick);
            for(int i = level - 1; i >= 0 ; i--)
            {
                roll += rollFactor.get(i);
            }
            return roll;
        }
        return 0F;
    }

    //World Portal we're rendering
    //World
    //Entity viewing the world portal
    //applied offset and rotations from the pair
    //partial tick
    public static void renderWorldPortal(Minecraft mc, WorldPortal portal, Entity ent, float[] appliedOffset, float[] appliedRotation, float partialTick)
    {
        if(RendererHelper.canUseStencils() && renderLevel <= (iChunUtil.config.maxRecursion - 1) && renderCount < iChunUtil.config.maxRendersPerTick && portal.hasPair())
        {
            renderLevel++;
            renderCount++;

            //set fields and render the stencil area.
            GL11.glEnable(GL11.GL_STENCIL_TEST);
            GlStateManager.colorMask(false, false, false, false);
            GlStateManager.depthMask(true);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            GL11.glStencilFunc(GL11.GL_ALWAYS, iChunUtil.config.stencilValue + renderLevel, 0xFF); //set the stencil test to always pass, set reference value, set mask.
            if(renderLevel == 1) GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);  // sPass, dFail, dPass. Set stencil where the depth passes fails. (which means the render is the topmost - depth mask is on)
            else GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_INCR);  // sPass, dFail, dPass. Set stencil where the depth passes fails. (which means the render is the topmost - depth mask is on)
            GL11.glStencilMask(0xFF); //set the stencil mask.

            GL11.glClearStencil(0); //stencil clears to 0 when cleared.
            if(renderLevel == 1)GlStateManager.clear(GL11.GL_STENCIL_BUFFER_BIT);

            GlStateManager.disableTexture2D();
            //only draw the portal on first recursion, we shouldn't draw anything outside the portal.
            portal.drawPlane(partialTick);//draw to stencil to set the areas that pass stencil and depth to our reference value

            GL11.glStencilMask(0x00); //Disable drawing to the stencil buffer now.
            GL11.glStencilFunc(GL11.GL_EQUAL, iChunUtil.config.stencilValue + renderLevel, 0xFF); //anything drawn now will only show if the value on the stencil equals to our reference value.
            //This is where we hope nothing would have done GL_INCR or GL_DECR where we've drawn our stuffs.

            //set the z-buffer to the farthest value for every pixel in the portal, before rendering the stuff in the portal
            GlStateManager.depthFunc(GL11.GL_ALWAYS);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex3d(1, -1, 1);
            GL11.glVertex3d(1, 1, 1);
            GL11.glVertex3d(-1, 1, 1);
            GL11.glVertex3d(-1, -1, 1);
            GL11.glEnd();
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.popMatrix();
            GlStateManager.depthFunc(GL11.GL_LEQUAL);

            //reset the colour and gl states
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.enableNormalize();
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            //After here

            //Add roll level
            rollFactor.add(appliedRotation[2]);

            //render world here
            drawWorld(mc, portal, ent, appliedOffset, appliedRotation, partialTick);
            //End render world

            //remove roll level
            if(!rollFactor.isEmpty())
            {
                rollFactor.remove(rollFactor.size() - 1);
            }

            //(stuff rendered at the local side of the portal after the portal is rendered should see the same depth value as if it's a simple normal quad)
            //This call fixes that
            //This also resets the stencil buffer to how it was previously before this function was called..
            GlStateManager.disableTexture2D();
            GL11.glColorMask(false, false, false, false);
            if(renderLevel > 1)
            {
                GL11.glStencilFunc(GL11.GL_ALWAYS, iChunUtil.config.stencilValue + renderLevel - 1, 0xFF); //set the stencil test to always pass, set reference value, set mask.
                GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_DECR);  // sPass, dFail, dPass. Set stencil where the depth passes fails. (which means the render is the topmost - depth mask is on)
                GL11.glStencilMask(0xFF); //set the stencil mask.
            }
            portal.drawPlane(partialTick);
            if(renderLevel > 1)
            {
                GL11.glStencilMask(0x00); //Disable drawing to the stencil buffer now.
                GL11.glStencilFunc(GL11.GL_EQUAL, iChunUtil.config.stencilValue + renderLevel - 1, 0xFF); //anything drawn now will only show if the value on the stencil equals to our reference value.
            }
            GL11.glColorMask(true, true, true, true);
            GlStateManager.enableTexture2D();

            if(renderLevel == 1) GL11.glDisable(GL11.GL_STENCIL_TEST);

            renderLevel--;
        }
    }

    private static void drawWorld(Minecraft mc, WorldPortal worldPortal, Entity renderer, float[] posOffset, float[] rotOffset, float partialTick)
    {
        WorldPortal pair = worldPortal.getPair();

        TileEntityRendererDispatcher.instance.drawBatch(net.minecraftforge.client.MinecraftForgeClient.getRenderPass());

        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.color(1F, 1F, 1F);
        boolean hideGui = mc.gameSettings.hideGUI;
        mc.gameSettings.hideGUI = true;

        double cameraZoom = mc.entityRenderer.cameraZoom;
        mc.entityRenderer.cameraZoom = 1.0125F; //Lightly narrow the FoV of the player view to reduce the rendering but it's not being very helpful :/

        int renderDist = mc.gameSettings.renderDistanceChunks;
        mc.gameSettings.renderDistanceChunks = worldPortal.getRenderDistanceChunks();

        RenderGlobal global = mc.renderGlobal;
        mc.renderGlobal = WorldPortals.eventHandlerClient.renderGlobalProxy;
        WorldPortals.eventHandlerClient.renderGlobalProxy.cloudTickCounter = global.cloudTickCounter;
        WorldPortals.eventHandlerClient.renderGlobalProxy.bindViewFrustum(pair); //binds to the View Frustum for this TE.
        WorldPortals.eventHandlerClient.renderGlobalProxy.storePlayerInfo();

        AxisAlignedBB pairFlatPlane = pair.getFlatPlane();
        double destX = (pairFlatPlane.maxX + pairFlatPlane.minX) / 2D;
        double destY = (pairFlatPlane.maxY + pairFlatPlane.minY) / 2D;
        double destZ = (pairFlatPlane.maxZ + pairFlatPlane.minZ) / 2D;

        EntityTransformationStack ets = new EntityTransformationStack(renderer).moveEntity(destX, destY, destZ, posOffset, rotOffset, partialTick);
        drawWorld(WorldPortals.eventHandlerClient.renderGlobalProxy, renderer, worldPortal, pair, partialTick);
        ets.pop();

        mc.gameSettings.renderDistanceChunks = renderDist;
        mc.renderGlobal = global;

        mc.entityRenderer.cameraZoom = cameraZoom;
        mc.gameSettings.hideGUI = hideGui;

        ForgeHooksClient.setRenderPass(0);

        double d0 = renderer.lastTickPosX + (renderer.posX - renderer.lastTickPosX) * partialTick;
        double d1 = renderer.lastTickPosY + (renderer.posY - renderer.lastTickPosY) * partialTick;
        double d2 = renderer.lastTickPosZ + (renderer.posZ - renderer.lastTickPosZ) * partialTick;

        //TODO double check if this is still necessary after each individual WP gets it's own render global proxy.
        //Water render recursing portal fix
        if(renderLevel > 1)
        {
            WorldPortals.eventHandlerClient.renderGlobalProxy.bindViewFrustum(pair); //binds to the View Frustum for this TE.
            WorldPortals.eventHandlerClient.renderGlobalProxy.renderContainer.initialize(d0, d1, d2);

            WorldPortals.eventHandlerClient.renderGlobalProxy.lastViewEntityX = renderer.posX;
            WorldPortals.eventHandlerClient.renderGlobalProxy.lastViewEntityY = renderer.posY;
            WorldPortals.eventHandlerClient.renderGlobalProxy.lastViewEntityZ = renderer.posZ;
            WorldPortals.eventHandlerClient.renderGlobalProxy.lastViewEntityPitch = (double)renderer.rotationPitch;
            WorldPortals.eventHandlerClient.renderGlobalProxy.lastViewEntityYaw = (double)renderer.rotationYaw;
        }
        //Water render recursing portal fix end

        TileEntityRendererDispatcher.instance.prepare(mc.theWorld, mc.getTextureManager(), mc.fontRendererObj, renderer, mc.objectMouseOver, partialTick);
        mc.getRenderManager().cacheActiveRenderInfo(mc.theWorld, mc.fontRendererObj, renderer, mc.pointedEntity, mc.gameSettings, partialTick);
        mc.getRenderManager().renderPosX = d0;
        mc.getRenderManager().renderPosY = d1;
        mc.getRenderManager().renderPosZ = d2;
        TileEntityRendererDispatcher.staticPlayerX = d0;
        TileEntityRendererDispatcher.staticPlayerY = d1;
        TileEntityRendererDispatcher.staticPlayerZ = d2;
        ActiveRenderInfo.updateRenderInfo(mc.thePlayer, false); // view changes?
        ClippingHelperPortal.getInstance();
        Particle.interpPosX = d0;
        Particle.interpPosY = d1;
        Particle.interpPosZ = d2;
        Particle.cameraViewDir = renderer.getLook(partialTick);

        GlStateManager.popMatrix();

        TileEntityRendererDispatcher.instance.preDrawBatch();
    }

    private static void drawWorld(RenderGlobalProxy renderglobal, Entity entity, WorldPortal portal, WorldPortal pair, float partialTick)
    {
        Minecraft mc = Minecraft.getMinecraft();
        ParticleManager particlemanager = mc.effectRenderer;
        GlStateManager.enableCull();
        GlStateManager.viewport(0, 0, mc.displayWidth, mc.displayHeight);
        GlStateManager.clearColor(0.0F, 0.0F, 0.0F, 0.0F);

        GlStateManager.enableTexture2D();
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.enableLighting();
        GlStateManager.disableFog();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);

        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTick;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTick;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTick;

        setupCameraTransform(mc, mc.entityRenderer, entity, partialTick, 2);

        ActiveRenderInfo.updateRenderInfo(mc.thePlayer, false);
        ClippingHelperPortal.getInstance();

        ICamera icamera = new Frustum();
        icamera.setPosition(d0, d1, d2);

        RenderHelper.disableStandardItemLighting();

        if(mc.gameSettings.renderDistanceChunks >= 4)
        {
            mc.entityRenderer.setupFog(-1, partialTick);
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.loadIdentity();
            Project.gluPerspective(mc.entityRenderer.getFOVModifier(partialTick, true), (float)mc.displayWidth / (float)mc.displayHeight, 0.05F, mc.entityRenderer.farPlaneDistance * 2.0F);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            renderglobal.renderSky(partialTick, 2);
            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.loadIdentity();
            Project.gluPerspective(mc.entityRenderer.getFOVModifier(partialTick, true), (float)mc.displayWidth / (float)mc.displayHeight, 0.05F, mc.entityRenderer.farPlaneDistance * MathHelper.SQRT_2);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        }

        mc.entityRenderer.setupFog(0, partialTick);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        if(entity.posY + (double)entity.getEyeHeight() < 128.0D)
        {
            mc.entityRenderer.renderCloudsCheck(renderglobal, partialTick, 2);
        }

        mc.entityRenderer.setupFog(0, partialTick);
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        RenderHelper.disableStandardItemLighting();
        renderglobal.setupTerrain(entity, (double)partialTick, icamera, frameCount++, mc.thePlayer.isSpectator());

        int j = Math.min(Minecraft.getDebugFPS(), mc.gameSettings.limitFramerate);
        j = Math.max(j, 60);
        long l = Math.max((long)(1000000000 / j / 4), 0L);
        mc.renderGlobal.updateChunks(System.nanoTime() + l);

        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.disableAlpha();
        renderglobal.renderBlockLayer(BlockRenderLayer.SOLID, (double)partialTick, 2, entity);
        GlStateManager.enableAlpha();
        renderglobal.renderBlockLayer(BlockRenderLayer.CUTOUT_MIPPED, (double)partialTick, 2, entity);
        mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        renderglobal.renderBlockLayer(BlockRenderLayer.CUTOUT, (double)partialTick, 2, entity);
        mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);

        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        net.minecraftforge.client.ForgeHooksClient.setRenderPass(0);
        renderglobal.renderEntities(entity, icamera, partialTick, portal, pair);
        net.minecraftforge.client.ForgeHooksClient.setRenderPass(0);
        RenderHelper.disableStandardItemLighting();
        mc.entityRenderer.disableLightmap();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();

        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.popMatrix();

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        renderglobal.drawBlockDamageTexture(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), entity, partialTick);
        mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
        GlStateManager.disableBlend();

        mc.entityRenderer.enableLightmap();
        Particle.interpPosX = d0;
        Particle.interpPosY = d1;
        Particle.interpPosZ = d2;
        Particle.cameraViewDir = entity.getLook(partialTick);
        float f = 0.017453292F;
        float f1 = MathHelper.cos(entity.rotationYaw * 0.017453292F);
        float f2 = MathHelper.sin(entity.rotationYaw * 0.017453292F);
        float f3 = -f2 * MathHelper.sin(entity.rotationPitch * 0.017453292F);
        float f4 = f1 * MathHelper.sin(entity.rotationPitch * 0.017453292F);
        float f5 = MathHelper.cos(entity.rotationPitch * 0.017453292F);

        for(int i = 0; i < 2; ++i)
        {
            Queue<Particle> queue = particlemanager.fxLayers[3][i];

            if(!queue.isEmpty())
            {
                Tessellator tessellator = Tessellator.getInstance();
                VertexBuffer vertexbuffer = tessellator.getBuffer();

                for(Particle particle : queue)
                {
                    if(canDrawParticle(particle, pair.getFaceOn(), pair.getPos()))
                    {
                        particle.renderParticle(vertexbuffer, entity, partialTick, f1, f5, f2, f3, f4);
                    }
                }
            }
        }
        RenderHelper.disableStandardItemLighting();
        mc.entityRenderer.setupFog(0, partialTick);

        float ff = ActiveRenderInfo.getRotationX();
        float ff1 = ActiveRenderInfo.getRotationZ();
        float ff2 = ActiveRenderInfo.getRotationYZ();
        float ff3 = ActiveRenderInfo.getRotationXY();
        float ff4 = ActiveRenderInfo.getRotationXZ();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(516, 0.003921569F);

        for(int i_nf = 0; i_nf < 3; ++i_nf)
        {
            final int i = i_nf;

            for(int jj = 0; jj < 2; ++jj)
            {
                if(!particlemanager.fxLayers[i][jj].isEmpty())
                {
                    switch(jj)
                    {
                        case 0:
                            GlStateManager.depthMask(false);
                            break;
                        case 1:
                            GlStateManager.depthMask(true);
                    }

                    switch(i)
                    {
                        case 0:
                        default:
                            mc.getTextureManager().bindTexture(ParticleManager.PARTICLE_TEXTURES);
                            break;
                        case 1:
                            mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                    }

                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    Tessellator tessellator = Tessellator.getInstance();
                    VertexBuffer vertexbuffer = tessellator.getBuffer();
                    vertexbuffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

                    for(final Particle particle : particlemanager.fxLayers[i][jj])
                    {
                        if(canDrawParticle(particle, pair.getFaceOn(), pair.getPos()))
                        {
                            particle.renderParticle(vertexbuffer, entity, partialTick, ff, ff4, ff1, ff2, ff3);
                        }
                    }

                    tessellator.draw();
                }
            }
        }

        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1F);

        mc.entityRenderer.disableLightmap();

        GlStateManager.depthMask(false);
        GlStateManager.enableCull();
        mc.entityRenderer.renderRainSnow(partialTick);
        GlStateManager.depthMask(true);
        renderglobal.renderWorldBorder(entity, partialTick);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        mc.entityRenderer.setupFog(0, partialTick);
        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        renderglobal.renderBlockLayer(BlockRenderLayer.TRANSLUCENT, (double)partialTick, 2, entity);
        RenderHelper.enableStandardItemLighting();
        net.minecraftforge.client.ForgeHooksClient.setRenderPass(1);
        renderglobal.renderEntities(entity, icamera, partialTick, portal, pair);
        net.minecraftforge.client.ForgeHooksClient.setRenderPass(-1);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.disableFog();

        if(entity.posY + (double)entity.getEyeHeight() >= 128.0D)
        {
            mc.entityRenderer.renderCloudsCheck(renderglobal, partialTick, 2);
        }

        net.minecraftforge.client.ForgeHooksClient.dispatchRenderLast(renderglobal, partialTick);

        RenderHelper.enableStandardItemLighting();
    }

    //This is a modified copy of setupCameraTransform and orientCamera in EntityRenderer.
    private static void setupCameraTransform(Minecraft mc, EntityRenderer entityRenderer, Entity entity, float partialTicks, int pass)
    {
        entityRenderer.farPlaneDistance = (float)(mc.gameSettings.renderDistanceChunks * 16);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        float f = 0.07F;

        if(mc.gameSettings.anaglyph)
        {
            GlStateManager.translate((float)(-(pass * 2 - 1)) * f, 0.0F, 0.0F);
        }

        if(entityRenderer.cameraZoom != 1.0D)
        {
            GlStateManager.translate((float)entityRenderer.cameraYaw, (float)(-entityRenderer.cameraPitch), 0.0F);
            GlStateManager.scale(entityRenderer.cameraZoom, entityRenderer.cameraZoom, 1.0D);
        }

        Project.gluPerspective(entityRenderer.getFOVModifier(partialTicks, true), (float)mc.displayWidth / (float)mc.displayHeight, 0.05F, entityRenderer.farPlaneDistance * MathHelper.SQRT_2);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();

        if(mc.gameSettings.anaglyph)
        {
            GlStateManager.translate((float)(pass * 2 - 1) * 0.1F, 0.0F, 0.0F);
        }

        entityRenderer.hurtCameraEffect(partialTicks);

        if(mc.gameSettings.viewBobbing)
        {
            entityRenderer.setupViewBobbing(partialTicks);
        }

        float eyeHeight = entity.getEyeHeight();

        if(entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPlayerSleeping())
        {
            eyeHeight = (float)((double)eyeHeight + 1.0D);
            GlStateManager.translate(0.0F, 0.3F, 0.0F);

            if(!mc.gameSettings.debugCamEnable)
            {
                BlockPos blockpos = new BlockPos(entity);
                IBlockState iblockstate = mc.theWorld.getBlockState(blockpos);
                net.minecraftforge.client.ForgeHooksClient.orientBedCamera(mc.theWorld, blockpos, iblockstate, entity);
                //TODO does the bed camera affect the portal view? Is this required?

                GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F, 0.0F, -1.0F, 0.0F);
                GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, -1.0F, 0.0F, 0.0F);
            }
        }
        else if(mc.gameSettings.thirdPersonView > 0)
        {
            double d3 = (double)(entityRenderer.thirdPersonDistancePrev + (entityRenderer.thirdPersonDistance - entityRenderer.thirdPersonDistancePrev) * partialTicks);

            if(mc.gameSettings.debugCamEnable)
            {
                GlStateManager.translate(0.0F, 0.0F, (float)(-d3));
            }
            else
            {
                float f1 = entity.rotationYaw;
                float f2 = entity.rotationPitch;

                if(mc.gameSettings.thirdPersonView == 2)
                {
                    f2 += 180.0F;
                }

                if(entityRenderer.mc.gameSettings.thirdPersonView == 2)
                {
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                }

                GlStateManager.rotate(entity.rotationPitch - f2, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(entity.rotationYaw - f1, 0.0F, 1.0F, 0.0F);
                GlStateManager.translate(0.0F, 0.0F, (float)(-d3));
                GlStateManager.rotate(f1 - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(f2 - entity.rotationPitch, 1.0F, 0.0F, 0.0F);
            }
        }
        else
        {
            GlStateManager.translate(0.0F, 0.0F, 0.05F);
        }

        if(!mc.gameSettings.debugCamEnable)
        {
            float yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F;
            float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
            float roll = getRollFactor(renderLevel, partialTicks);
            if(entity instanceof EntityAnimal)
            {
                EntityAnimal entityanimal = (EntityAnimal)entity;
                yaw = entityanimal.prevRotationYawHead + (entityanimal.rotationYawHead - entityanimal.prevRotationYawHead) * partialTicks + 180.0F;
            }
            IBlockState state = ActiveRenderInfo.getBlockStateAtEntityViewpoint(entityRenderer.mc.theWorld, entity, partialTicks);
            net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup event = new net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup(entityRenderer, entity, state, partialTicks, yaw, pitch, roll);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
            GlStateManager.rotate(event.getRoll(), 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(event.getPitch(), 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(event.getYaw(), 0.0F, 1.0F, 0.0F);
        }

        GlStateManager.translate(0.0F, -eyeHeight, 0.0F);
        double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)partialTicks;
        double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)partialTicks + (double)eyeHeight;
        double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)partialTicks;
        entityRenderer.cloudFog = mc.renderGlobal.hasCloudFog(d0, d1, d2, partialTicks);

        if(entityRenderer.debugView)
        {
            switch(entityRenderer.debugViewDirection)
            {
                case 0:
                    GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case 1:
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case 2:
                    GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case 3:
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                    break;
                case 4:
                    GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            }
        }
    }

    private static boolean canDrawParticle(Particle ent, EnumFacing face, BlockPos pos)
    {
        return !(face.getFrontOffsetX() < 0 && ent.posX > pos.getX() + 2 || face.getFrontOffsetX() > 0 && ent.posX < pos.getX() - 1 || face.getFrontOffsetY() < 0 && (ent.getEntityBoundingBox().maxY + ent.getEntityBoundingBox().minY) / 2D > pos.getY() + 2 || face.getFrontOffsetY() > 0 && (ent.getEntityBoundingBox().maxY + ent.getEntityBoundingBox().minY) / 2D < pos.getY() - 1 || face.getFrontOffsetZ() < 0 && ent.posZ > pos.getZ() + 2 || face.getFrontOffsetZ() > 0 && ent.posZ < pos.getZ() - 1);
    }
}
