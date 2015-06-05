package us.ichun.mods.ichunutil.client.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3i;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import us.ichun.mods.ichunutil.common.core.util.ResourceHelper;
import us.ichun.mods.ichunutil.common.iChunUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class RendererHelper
{
    public static void init()
    {
        if(iChunUtil.config.enableStencils == 1)
        {
            Minecraft.getMinecraft().getFramebuffer().enableStencil();
        }
    }

    public static void renderBakedModel(IBakedModel model, int color, ItemStack stack)
    {
        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        if (model.isBuiltInRenderer() && stack != null)
        {
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
            TileEntityItemStackRenderer.instance.renderByItem(stack);
        }
        else
        {
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);

            renderModel(model, color, stack);

            if (stack != null && stack.getItem() != null && stack.hasEffect())
            {
                GlStateManager.depthMask(false);
                GlStateManager.depthFunc(GL11.GL_EQUAL);
                GlStateManager.disableLighting();
                GlStateManager.blendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
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
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GlStateManager.enableLighting();
                GlStateManager.depthFunc(GL11.GL_LEQUAL);
                GlStateManager.depthMask(true);
                mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            }
        }
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
    }

    private static void renderModel(IBakedModel model, int color, ItemStack stack)
    {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.startDrawingQuads();
        worldrenderer.setVertexFormat(DefaultVertexFormats.ITEM);
        EnumFacing[] aenumfacing = EnumFacing.values();
        int j = aenumfacing.length;

        for (int k = 0; k < j; ++k)
        {
            EnumFacing enumfacing = aenumfacing[k];
            renderQuads(worldrenderer, model.getFaceQuads(enumfacing), color, stack);
        }

        renderQuads(worldrenderer, model.getGeneralQuads(), color, stack);
        tessellator.draw();
    }

    private static void renderQuads(WorldRenderer renderer, List quads, int color, ItemStack stack)
    {
        BakedQuad bakedquad;
        int j;

        for (Iterator iterator = quads.iterator(); iterator.hasNext(); renderQuad(renderer, bakedquad, j))
        {
            bakedquad = (BakedQuad)iterator.next();
            j = color;

            if (color == -1)
            {
                if(bakedquad.hasTintIndex() && stack != null && stack.getItem() != null)
                {
                    j = stack.getItem().getColorFromItemStack(stack, bakedquad.getTintIndex());

                    if(EntityRenderer.anaglyphEnable)
                    {
                        j = TextureUtil.anaglyphColor(j);
                    }

                    j |= -16777216;
                }
            }
        }
    }

    private static void renderQuad(WorldRenderer renderer, BakedQuad quad, int color)
    {
        renderer.addVertexData(quad.getVertexData());
        if(quad instanceof net.minecraftforge.client.model.IColoredBakedQuad)
            net.minecraftforge.client.ForgeHooksClient.putQuadColor(renderer, quad, color);
        else
            renderer.putColor4(color);
        putQuadNormal(renderer, quad);
    }

    private static void putQuadNormal(WorldRenderer renderer, BakedQuad quad)
    {
        Vec3i vec3i = quad.getFace().getDirectionVec();
        renderer.putNormal((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
    }

    public static void renderLighting(double x, double y, double z, int randSeed, int bends, int spread, double heightOfBend, int layerCount, double layerSize, float intensity, int clr)
    {
        float r = (clr >> 16 & 0xff) / 255F;
        float g = (clr >> 8 & 0xff) / 255F;
        float b = (clr & 0xff) / 255F;

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        double[] adouble = new double[bends];
        double[] adouble1 = new double[bends];
        double d3 = 0.0D;
        double d4 = 0.0D;
        Random random = new Random(randSeed);

        for(int i = bends - 1; i >= 0; --i)
        {
            adouble[i] = d3;
            adouble1[i] = d4;
            d3 += (double)(random.nextInt((spread * 2) + 1) - spread);
            d4 += (double)(random.nextInt((spread * 2) + 1) - spread);
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

                worldrenderer.startDrawing(5);
                worldrenderer.setColorRGBA_F(r * intensity, g * intensity, b * intensity, 0.3F);
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

                    worldrenderer.addVertex(d13 + d5, y + (double)(i1 * heightOfBend), d14 + d6);
                    worldrenderer.addVertex(d11 + d7, y + (double)((i1 + 1) * heightOfBend), d12 + d8);
                }

                tessellator.draw();
            }
        }

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
    }

    public static void setColorFromInt(int color) {
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        GlStateManager.color(r, g, b, 1.0F);
    }

    public static void drawTextureOnScreen(ResourceLocation resource, double posX, double posY, double width, double height, double zLevel)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
        drawOnScreen(posX, posY, width, height, zLevel);
    }

    public static void drawOnScreen(double posX, double posY, double width, double height, double zLevel)
    {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.startDrawingQuads();
        worldrenderer.addVertexWithUV(posX		, posY + height	, zLevel, 0.0D, 1.0D);
        worldrenderer.addVertexWithUV(posX + width, posY + height	, zLevel, 1.0D, 1.0D);
        worldrenderer.addVertexWithUV(posX + width, posY			, zLevel, 1.0D, 0.0D);
        worldrenderer.addVertexWithUV(posX		, posY			, zLevel, 0.0D, 0.0D);
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
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.startDrawingQuads();
        worldrenderer.setColorRGBA(r, g, b, alpha);
        worldrenderer.addVertex(posX		  , posY + height	, zLevel);
        worldrenderer.addVertex(posX + width, posY + height	, zLevel);
        worldrenderer.addVertex(posX + width, posY			, zLevel);
        worldrenderer.addVertex(posX		  , posY			, zLevel);
        tessellator.draw();
        GlStateManager.enableTexture2D();
    }

    public static void startGlScissor(int x, int y, int width, int height)//From top left corner, like how Minecraft guis are. Don't forget to call endGlScissor after rendering
    {
        Minecraft mc = Minecraft.getMinecraft();

        ScaledResolution reso = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

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
        ScaledResolution reso1 = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

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

        RendererHelper.drawColourOnScreen(0xffffff, 255, 0, 0, reso1.getScaledWidth_double(), reso1.getScaledHeight_double(), 0);

        GL11.glDisable(GL11.GL_STENCIL_TEST);
    }

    public static void renderTestScissor()
    {
        //Basic scissor test
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution reso1 = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

        RendererHelper.startGlScissor(reso1.getScaledWidth() / 2 - 50, reso1.getScaledHeight() / 2 - 50, 100, 100);

        GlStateManager.pushMatrix();

        GlStateManager.translate(-15F, 15F, 0F);

        RendererHelper.drawColourOnScreen(0xffffff, 255, 0, 0, reso1.getScaledWidth_double(), reso1.getScaledHeight_double(), 0);

        GlStateManager.popMatrix();

        RendererHelper.endGlScissor();
    }

    public static ArrayList<Framebuffer> frameBuffers = new ArrayList<Framebuffer>();

    public static Framebuffer createFrameBuffer(String modId, boolean useDepth)
    {
        Framebuffer render = new Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, useDepth);
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
}
