package me.ichun.mods.ichunutil.client.render;

import me.ichun.mods.ichunutil.client.render.world.RenderGlobalProxy;
import me.ichun.mods.ichunutil.common.core.util.ResourceHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class RendererHelper
{
    private static boolean canUseStencils;
    private static Random lightningRand;

    public static void init()
    {
        canUseStencils = Minecraft.getMinecraft().getFramebuffer().isStencilEnabled();
        if(iChunUtil.config.enableStencils == 1 && !canUseStencils)
        {
            canUseStencils = Minecraft.getMinecraft().getFramebuffer().enableStencil();
        }
        lightningRand = new Random();
    }

    public static boolean canUseStencils()
    {
        return canUseStencils;
    }

    public static void spawnParticle(Particle particle)
    {
        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
    }

    public static Vec3d getCameraPosition(Entity viewer, float partialTicks)
    {
        Vec3d position = new Vec3d(viewer.posX, viewer.posY + viewer.getEyeHeight(), viewer.posZ);
        Minecraft mc = Minecraft.getMinecraft();
        if(mc.gameSettings.thirdPersonView > 0)
        {
            double d3 = (double)(mc.entityRenderer.thirdPersonDistancePrev + (4.0F - mc.entityRenderer.thirdPersonDistancePrev) * partialTicks);
            if (!mc.gameSettings.debugCamEnable)
            {
                double d0 = viewer.prevPosX + (viewer.posX - viewer.prevPosX) * (double)partialTicks;
                double d1 = viewer.prevPosY + (viewer.posY - viewer.prevPosY) * (double)partialTicks;
                double d2 = viewer.prevPosZ + (viewer.posZ - viewer.prevPosZ) * (double)partialTicks;

                float f1 = viewer.rotationYaw;
                float f2 = viewer.rotationPitch;

                if (mc.gameSettings.thirdPersonView == 2)
                {
                    f2 += 180.0F;
                }

                double d4 = (double)(-MathHelper.sin(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F)) * d3;
                double d5 = (double)(MathHelper.cos(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F)) * d3;
                double d6 = (double)(-MathHelper.sin(f2 * 0.017453292F)) * d3;

                for (int i = 0; i < 8; ++i)
                {
                    float f3 = (float)((i & 1) * 2 - 1);
                    float f4 = (float)((i >> 1 & 1) * 2 - 1);
                    float f5 = (float)((i >> 2 & 1) * 2 - 1);
                    f3 = f3 * 0.1F;
                    f4 = f4 * 0.1F;
                    f5 = f5 * 0.1F;
                    RayTraceResult raytraceresult = mc.world.rayTraceBlocks(new Vec3d(d0 + (double)f3, d1 + (double)f4, d2 + (double)f5), new Vec3d(d0 - d4 + (double)f3 + (double)f5, d1 - d6 + (double)f4, d2 - d5 + (double)f5));

                    if (raytraceresult != null)
                    {
                        double d7 = raytraceresult.hitVec.distanceTo(new Vec3d(d0, d1, d2));

                        if (d7 < d3)
                        {
                            d3 = d7;
                        }
                    }
                }

                if (mc.gameSettings.thirdPersonView == 2)
                {
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                }
                position = position.addVector(-d4, -d6, -d5);
            }
        }
        return position;
    }

    public static void renderBakedModel(IBakedModel model, int color, ItemStack stack)
    {
        Minecraft mc = Minecraft.getMinecraft();

        GlStateManager.pushMatrix();
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        //renderItem

        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);

        if(model.isBuiltInRenderer())
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
            TileEntityItemStackRenderer.instance.renderByItem(stack);
        }
        else
        {
            renderModel(model, color, stack);

            if(stack.hasEffect())
            {
                GlStateManager.depthMask(false);
                GlStateManager.depthFunc(GL11.GL_EQUAL);
                GlStateManager.disableLighting();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
                mc.getTextureManager().bindTexture(ResourceHelper.texGlint);
                GlStateManager.matrixMode(GL11.GL_TEXTURE);
                GlStateManager.pushMatrix();
                GlStateManager.scale(8.0F, 8.0F, 8.0F);
                float f = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
                GlStateManager.translate(f, 0.0F, 0.0F);
                GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
                renderModel(model, -8372020, null);
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.scale(8.0F, 8.0F, 8.0F);
                float f1 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
                GlStateManager.translate(-f1, 0.0F, 0.0F);
                GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
                renderModel(model, -8372020, null);
                GlStateManager.popMatrix();
                GlStateManager.matrixMode(GL11.GL_MODELVIEW);
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                GlStateManager.enableLighting();
                GlStateManager.depthFunc(GL11.GL_LEQUAL);
                GlStateManager.depthMask(true);
                mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            }
        }

        GlStateManager.popMatrix();

        //end renderItem

        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
    }

    private static void renderModel(IBakedModel model, int color, ItemStack stack)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.ITEM);

        for(EnumFacing enumfacing : EnumFacing.values())
        {
            renderQuads(bufferbuilder, model.getQuads((IBlockState)null, enumfacing, 0L), color, stack);
        }

        renderQuads(bufferbuilder, model.getQuads((IBlockState)null, (EnumFacing)null, 0L), color, stack);
        tessellator.draw();
    }

    private static void renderQuads(BufferBuilder renderer, List<BakedQuad> quads, int color, ItemStack stack)
    {
        boolean flag = color == -1 && stack != null;
        int i = 0;

        for(int j = quads.size(); i < j; ++i)
        {
            BakedQuad bakedquad = (BakedQuad)quads.get(i);
            int k = color;

            if(flag && bakedquad.hasTintIndex())
            {
                k = Minecraft.getMinecraft().getItemColors().colorMultiplier(stack, bakedquad.getTintIndex());

                if(EntityRenderer.anaglyphEnable)
                {
                    k = TextureUtil.anaglyphColor(k);
                }

                k = k | -16777216;
            }

            net.minecraftforge.client.model.pipeline.LightUtil.renderQuadColor(renderer, bakedquad, k);
        }
    }

    public static void renderSinLight(double distance, double size, double startAngle, double angleFactor, boolean droppingIntensity, int clr)//TODO not finished.
    {
        float r = (clr >> 16 & 0xff) / 255F;
        float g = (clr >> 8 & 0xff) / 255F;
        float b = (clr & 0xff) / 255F;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        bufferbuilder.begin(4, DefaultVertexFormats.POSITION_COLOR); //GL_TRIANGLE_STRIP
        float max = (float)distance * 10F;
        double halfSize = size / 2D;
        for(float f = 0F; f < max; f++)
        {
            bufferbuilder.pos(-halfSize, distance * (f / max), 0).color(r, g, b, 1F).endVertex();
            bufferbuilder.pos(halfSize, distance * (f / max), 0).color(r, g, b, 1F).endVertex();
        }
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
    }

    public static void renderLighting(double x, double y, double z, int randSeed, int bends, int spread, double heightOfBend, int layerCount, double layerSize, float intensity, int clr)
    {
        float r = (clr >> 16 & 0xff) / 255F;
        float g = (clr >> 8 & 0xff) / 255F;
        float b = (clr & 0xff) / 255F;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        double[] adouble = new double[bends];
        double[] adouble1 = new double[bends];
        double d3 = 0.0D;
        double d4 = 0.0D;
        lightningRand.setSeed(randSeed);

        for(int i = bends - 1; i >= 0; --i)
        {
            adouble[i] = d3;
            adouble1[i] = d4;
            d3 += (double)(lightningRand.nextInt((spread * 2) + 1) - spread);
            d4 += (double)(lightningRand.nextInt((spread * 2) + 1) - spread);
        }

        for(int layers = 0; layers < layerCount; ++layers)
        {
            Random random1 = new Random(randSeed);

            int k = bends - 1;
            int l = 0;

            double d5 = 0D;
            double d6 = 0D;

            for(int i1 = k; i1 >= l; --i1)
            {
                double d7 = d5;
                double d8 = d6;

                d5 += (double)(random1.nextInt((spread * 2) + 1) - spread);
                d6 += (double)(random1.nextInt((spread * 2) + 1) - spread);

                if(i1 == l)
                {
                    d5 = 0D;
                    d6 = 0D;
                }

                bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);
                double d9 = 0.1D + (double)layers * layerSize;

                d9 *= (double)i1 * 0.1D + 0.5D;

                double d10 = 0.1D + (double)layers * layerSize;

                d10 *= (double)(i1 - 1) * 0.1D + 0.5D;

                for(int j1 = 0; j1 < 5; ++j1)
                {
                    double d11 = x + 0.5D - d9;
                    double d12 = z + 0.5D - d9;

                    if(j1 == 1 || j1 == 2)
                    {
                        d11 += d9 * 2.0D;
                    }

                    if(j1 == 2 || j1 == 3)
                    {
                        d12 += d9 * 2.0D;
                    }

                    double d13 = x + 0.5D - d10;
                    double d14 = z + 0.5D - d10;

                    if(j1 == 1 || j1 == 2)
                    {
                        d13 += d10 * 2.0D;
                    }

                    if(j1 == 2 || j1 == 3)
                    {
                        d14 += d10 * 2.0D;
                    }

                    bufferbuilder.pos(d13 + d5, y + (i1 * heightOfBend), d14 + d6).color(r * intensity, g * intensity, b * intensity, 0.3F).endVertex();
                    bufferbuilder.pos(d11 + d7, y + ((i1 + 1) * heightOfBend), d12 + d8).color(r * intensity, g * intensity, b * intensity, 0.3F).endVertex();
                }

                tessellator.draw();
            }
        }

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
    }

    public static void setColorFromInt(int color)
    {
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        GlStateManager.color(r, g, b, 1.0F);
    }

    public static void setColorFromInt(int color, float alpha)
    {
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        GlStateManager.color(r, g, b, alpha);
    }

    public static void drawTextureOnScreen(ResourceLocation resource, double posX, double posY, double width, double height, double zLevel)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
        drawOnScreen(posX, posY, width, height, zLevel);
    }

    public static void drawOnScreen(double posX, double posY, double width, double height, double zLevel)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(posX, posY + height, zLevel).tex(0.0D, 1.0D).endVertex();
        bufferbuilder.pos(posX + width, posY + height, zLevel).tex(1.0D, 1.0D).endVertex();
        bufferbuilder.pos(posX + width, posY, zLevel).tex(1.0D, 0.0D).endVertex();
        bufferbuilder.pos(posX, posY, zLevel).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
    }

    public static void drawColourOnScreen(int colour, int alpha, double posX, double posY, double width, double height, double zLevel)
    {
        int r = (colour >> 16 & 0xff);
        int g = (colour >> 8 & 0xff);
        int b = (colour & 0xff);
        drawColourOnScreen(r, g, b, alpha, posX, posY, width, height, zLevel);
    }

    public static void drawColourOnScreen(int r, int g, int b, int alpha, double posX, double posY, double width, double height, double zLevel)
    {
        if(width <= 0 || height <= 0)
        {
            return;
        }
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(posX, posY + height, zLevel).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(posX + width, posY + height, zLevel).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(posX + width, posY, zLevel).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(posX, posY, zLevel).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
    }

    public static void drawGradientOnScreen(int downLeft, int downRight, int upLeft, int upRight, double posX, double posY, double width, double height, double zLevel)
    {
        if(width <= 0 || height <= 0)
        {
            return;
        }
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);

        int alpha = (downLeft >> 24 & 0xff);
        int r = (downLeft >> 16 & 0xff);
        int g = (downLeft >> 8 & 0xff);
        int b = (downLeft & 0xff);
        bufferbuilder.pos(posX, posY + height, zLevel).color(r, g, b, alpha).endVertex();

        alpha = (downRight >> 24 & 0xff);
        r = (downRight >> 16 & 0xff);
        g = (downRight >> 8 & 0xff);
        b = (downRight & 0xff);
        bufferbuilder.pos(posX + width, posY + height, zLevel).color(r, g, b, alpha).endVertex();

        alpha = (upRight >> 24 & 0xff);
        r = (upRight >> 16 & 0xff);
        g = (upRight >> 8 & 0xff);
        b = (upRight & 0xff);
        bufferbuilder.pos(posX + width, posY, zLevel).color(r, g, b, alpha).endVertex();

        alpha = (upLeft >> 24 & 0xff);
        r = (upLeft >> 16 & 0xff);
        g = (upLeft >> 8 & 0xff);
        b = (upLeft & 0xff);
        bufferbuilder.pos(posX, posY, zLevel).color(r, g, b, alpha).endVertex();

        tessellator.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawHueStripOnScreen(int alpha, double posX, double posY, double width, double height, double zLevel)
    {
        if(width <= 0 || height <= 0)
        {
            return;
        }
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        double sHeight = height / 6D;
        int[][] colourArray = new int[][] { { 255, 0, 0 }, { 255, 0, 255 }, { 0, 0, 255 }, { 0, 255, 255 }, { 0, 255, 0 }, { 255, 255, 0 }, { 255, 0, 0 } };
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        for(int i = 0; i < 6; i++)
        {
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
            bufferbuilder.pos(posX, posY + (sHeight * (i + 1)), zLevel).color(colourArray[i + 1][0], colourArray[i + 1][1], colourArray[i + 1][2], alpha).endVertex();
            bufferbuilder.pos(posX + width, posY + (sHeight * (i + 1)), zLevel).color(colourArray[i + 1][0], colourArray[i + 1][1], colourArray[i + 1][2], alpha).endVertex();
            bufferbuilder.pos(posX + width, posY + (sHeight * i), zLevel).color(colourArray[i][0], colourArray[i][1], colourArray[i][2], alpha).endVertex();
            bufferbuilder.pos(posX, posY + (sHeight * i), zLevel).color(colourArray[i][0], colourArray[i][1], colourArray[i][2], alpha).endVertex();
            tessellator.draw();
        }

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static int getRandomColourFromString(String s)
    {
        Random rand = new Random();
        rand.setSeed(Math.abs(s.hashCode() * 1000));
        int clr = Math.round(0xffffff * rand.nextFloat());
        float[] hsb = new float[3];
        Color.RGBtoHSB(clr >> 16 & 0xff, clr >> 8 & 0xff, clr & 0xff, hsb);
        hsb[2] = 0.65F + 0.25F * hsb[2];
        clr = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
        return clr;
    }

    public static void startGlScissor(int x, int y, int width, int height)//From top left corner, like how Minecraft guis are. Don't forget to call endGlScissor after rendering
    {
        Minecraft mc = Minecraft.getMinecraft();

        ScaledResolution reso = new ScaledResolution(mc);

        double scaleW = (double)mc.displayWidth / reso.getScaledWidth_double();
        double scaleH = (double)mc.displayHeight / reso.getScaledHeight_double();

        if(width <= 0 || height <= 0)
        {
            return;
        }
        if(x < 0)
        {
            x = 0;
        }
        if(y < 0)
        {
            y = 0;
        }

        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        GL11.glScissor((int)Math.floor((double)x * scaleW), (int)Math.floor((double)mc.displayHeight - ((double)(y + height) * scaleH)), (int)Math.floor((double)(x + width) * scaleW) - (int)Math.floor((double)x * scaleW), (int)Math.floor((double)mc.displayHeight - ((double)y * scaleH)) - (int)Math.floor((double)mc.displayHeight - ((double)(y + height) * scaleH))); //starts from lower left corner (minecraft starts from upper left)
    }

    public static void endGlScissor()
    {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public static void renderTestStencil()
    {
        //Basic stencil test
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution reso = new ScaledResolution(mc);

        GL11.glEnable(GL11.GL_STENCIL_TEST);

        GlStateManager.colorMask(false, false, false, false);
        GlStateManager.depthMask(false);

        GL11.glStencilFunc(GL11.GL_NEVER, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_KEEP);

        GL11.glStencilMask(0xFF);
        GlStateManager.clear(GL11.GL_STENCIL_BUFFER_BIT);

        RendererHelper.drawColourOnScreen(0xffffff, 255, 0, 0, 60, 60, 0);

        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.depthMask(true);

        GL11.glStencilMask(0x00);

        GL11.glStencilFunc(GL11.GL_EQUAL, 0, 0xFF);

        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);

        RendererHelper.drawColourOnScreen(0xffffff, 255, 0, 0, reso.getScaledWidth_double(), reso.getScaledHeight_double(), 0);

        GL11.glDisable(GL11.GL_STENCIL_TEST);
    }

    public static void renderTestScissor()
    {
        //Basic scissor test
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution reso1 = new ScaledResolution(mc);

        RendererHelper.startGlScissor(reso1.getScaledWidth() / 2 - 50, reso1.getScaledHeight() / 2 - 50, 100, 100);

        GlStateManager.pushMatrix();

        GlStateManager.translate(-15F, 15F, 0F);

        RendererHelper.drawColourOnScreen(0xffffff, 255, 0, 0, reso1.getScaledWidth_double(), reso1.getScaledHeight_double(), 0);

        GlStateManager.popMatrix();

        RendererHelper.endGlScissor();
    }

    public static HashSet<Framebuffer> frameBuffers = new HashSet<>();

    public static Framebuffer createFrameBuffer(boolean useDepth, boolean useStencil)
    {
        Framebuffer render = new Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, useDepth);
        if(useStencil && canUseStencils())
        {
            render.enableStencil();
        }
        frameBuffers.add(render);
        return render;
    }

    public static void deleteFrameBuffer(Framebuffer buffer)
    {
        if(buffer.framebufferObject >= 0)
        {
            buffer.deleteFramebuffer();
        }
        frameBuffers.remove(buffer);
    }

    //Proxies are pooled to prevent constant regeneration of render sky/stars etc. Pooling probably not required by many of my mods but it's nice to have.
    public static HashSet<RenderGlobalProxy> renderGlobalProxies = new HashSet<>();

    public static RenderGlobalProxy requestRenderGlobalProxy()
    {
        RenderGlobalProxy proxy = null;
        for(RenderGlobalProxy pooledProxy : renderGlobalProxies)
        {
            if(pooledProxy.released)
            {
                proxy = pooledProxy;
                proxy.released = false;
                break;
            }
        }
        if(proxy == null)
        {
            proxy = new RenderGlobalProxy(Minecraft.getMinecraft());
            renderGlobalProxies.add(proxy);
        }
        if(iChunUtil.eventHandlerClient.getRenderGlobalWorldInstance() != null)
        {
            proxy.setWorldAndLoadRenderers(iChunUtil.eventHandlerClient.getRenderGlobalWorldInstance());
        }
        return proxy;
    }

    public static void releaseRenderGlobalProxy(RenderGlobalProxy proxy)
    {
        proxy.released = true;
        proxy.setWorldAndLoadRenderers(null);
    }
}
