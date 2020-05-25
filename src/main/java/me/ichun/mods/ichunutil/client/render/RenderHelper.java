package me.ichun.mods.ichunutil.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.List;

public class RenderHelper
{
    private static void renderModel(IBakedModel modelIn, ItemStack stack, int combinedLightIn, int combinedOverlayIn, MatrixStack matrixStackIn, IVertexBuilder bufferIn) {
        Random random = new Random();
        long i = 42L;

        for(Direction direction : Direction.values()) {
            random.setSeed(42L);
            renderQuads(matrixStackIn, bufferIn, modelIn.getQuads((BlockState)null, direction, random), stack, combinedLightIn, combinedOverlayIn);
        }

        random.setSeed(42L);
        renderQuads(matrixStackIn, bufferIn, modelIn.getQuads((BlockState)null, (Direction)null, random), stack, combinedLightIn, combinedOverlayIn);
    }

    private static void renderQuads(MatrixStack matrixStackIn, IVertexBuilder bufferIn, List<BakedQuad> quadsIn, ItemStack itemStackIn, int combinedLightIn, int combinedOverlayIn) {
        boolean flag = !itemStackIn.isEmpty();
        MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();

        for(BakedQuad bakedquad : quadsIn) {
            int i = -1;
            if (flag && bakedquad.hasTintIndex()) {
                i = Minecraft.getInstance().getItemColors().getColor(itemStackIn, bakedquad.getTintIndex());
            }

            float f = (float)(i >> 16 & 255) / 255.0F;
            float f1 = (float)(i >> 8 & 255) / 255.0F;
            float f2 = (float)(i & 255) / 255.0F;
            bufferIn.addVertexData(matrixstack$entry, bakedquad, f, f1, f2, combinedLightIn, combinedOverlayIn, true);
        }

    }

    public static void renderBakedModel(IBakedModel modelIn, ItemStack itemStackIn)
    {
        renderBakedModel(modelIn, itemStackIn, null);
    }

    public static void renderBakedModel(IBakedModel modelIn, ItemStack itemStackIn, RenderType renderTypeOverride)
    {
        renderBakedModel(modelIn, itemStackIn, renderTypeOverride, new MatrixStack(), Minecraft.getInstance().getRenderTypeBuffers().getBufferSource());
    }

    public static void renderBakedModel(IBakedModel modelIn, ItemStack itemStackIn, RenderType renderTypeOverride, MatrixStack matrixStackIn, IRenderTypeBuffer buffer)
    {
        Minecraft mc = Minecraft.getInstance();

        //ItemRenderer.renderItemModelIntoGUI
        RenderSystem.pushMatrix();
        mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmapDirect(false, false);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        //setupGuiTransform removed
        boolean flag4 = !modelIn.func_230044_c_();
        if (flag4) {
            net.minecraft.client.renderer.RenderHelper.setupGuiFlatDiffuseLighting();
        }

        //renderitem
        if (!itemStackIn.isEmpty()) {
            matrixStackIn.push();
            if (itemStackIn.getItem() == Items.TRIDENT) {
                modelIn = mc.getItemRenderer().getItemModelMesher().getModelManager().getModel(new ModelResourceLocation("minecraft:trident#inventory"));
            }

            modelIn = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(matrixStackIn, modelIn, ItemCameraTransforms.TransformType.NONE, false);
            matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
            if (!modelIn.isBuiltInRenderer()) {
                RenderType rendertype = RenderTypeLookup.getRenderType(itemStackIn);
                RenderType rendertype1;
                if(renderTypeOverride != null)
                {
                    rendertype1 = renderTypeOverride;
                }
                else if (Objects.equals(rendertype, Atlases.getTranslucentBlockType())) {
                    rendertype1 = Atlases.getTranslucentCullBlockType();
                } else {
                    rendertype1 = rendertype;
                }

                IVertexBuilder ivertexbuilder = ItemRenderer.getBuffer(buffer, rendertype1, true, itemStackIn.hasEffect());
                //renderModel
                renderModel(modelIn, itemStackIn, 15728880, OverlayTexture.NO_OVERLAY, matrixStackIn, ivertexbuilder);
                //end renderModel
            } else {
                itemStackIn.getItem().getItemStackTileEntityRenderer().render(itemStackIn, matrixStackIn, buffer, 15728880, OverlayTexture.NO_OVERLAY);
            }

            matrixStackIn.pop();
        }
        //end renderitem

        if(buffer instanceof IRenderTypeBuffer.Impl)
        {
            ((IRenderTypeBuffer.Impl)buffer).finish();
        }
        RenderSystem.enableDepthTest();
        if (flag4) {
            net.minecraft.client.renderer.RenderHelper.setupGui3DDiffuseLighting();
        }

        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();

    }

    public static void drawTexture(ResourceLocation resource, double posX, double posY, double width, double height, double zLevel)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(resource);
        draw(posX, posY, width, height, zLevel);
    }

    public static void draw(double posX, double posY, double width, double height, double zLevel)
    {
        draw(posX, posY, width, height, zLevel, 0D, 1D, 0D, 1D);
    }

    public static void draw(double posX, double posY, double width, double height, double zLevel, double u1, double u2, double v1, double v2)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(posX, posY + height, zLevel).tex((float)u1, (float)v2).endVertex();
        bufferbuilder.pos(posX + width, posY + height, zLevel).tex((float)u2, (float)v2).endVertex();
        bufferbuilder.pos(posX + width, posY, zLevel).tex((float)u2, (float)v1).endVertex();
        bufferbuilder.pos(posX, posY, zLevel).tex((float)u1, (float)v1).endVertex();
        tessellator.draw();
    }

    public static void drawColour(int colour, int alpha, double posX, double posY, double width, double height, double zLevel)
    {
        int r = (colour >> 16 & 0xff);
        int g = (colour >> 8 & 0xff);
        int b = (colour & 0xff);
        drawColour(r, g, b, alpha, posX, posY, width, height, zLevel);
    }

    public static void drawColour(int r, int g, int b, int alpha, double posX, double posY, double width, double height, double zLevel)
    {
        if(width <= 0 || height <= 0)
        {
            return;
        }
        RenderSystem.disableTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(posX, posY + height, zLevel).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(posX + width, posY + height, zLevel).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(posX + width, posY, zLevel).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(posX, posY, zLevel).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        RenderSystem.enableTexture();
    }

    public static void colour(int color)
    {
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        RenderSystem.color4f(r, g, b, 1.0F);
    }

    public static void colour(int color, float alpha)
    {
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        RenderSystem.color4f(r, g, b, alpha);
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
        Minecraft mc = Minecraft.getInstance();

        double scaleW = (double)mc.getMainWindow().getFramebufferWidth() / mc.getMainWindow().getScaledWidth();
        double scaleH = (double)mc.getMainWindow().getFramebufferHeight() / mc.getMainWindow().getScaledHeight();

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

        GL11.glScissor((int)Math.floor((double)x * scaleW), (int)Math.floor((double)mc.getMainWindow().getFramebufferHeight() - ((double)(y + height) * scaleH)), (int)Math.floor((double)(x + width) * scaleW) - (int)Math.floor((double)x * scaleW), (int)Math.floor((double)mc.getMainWindow().getFramebufferHeight() - ((double)y * scaleH)) - (int)Math.floor((double)mc.getMainWindow().getFramebufferHeight() - ((double)(y + height) * scaleH))); //starts from lower left corner (minecraft starts from upper left)
    }

    public static void endGlScissor()
    {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public static void renderTestScissor()
    {
        //Basic scissor test
        Minecraft mc = Minecraft.getInstance();

        RenderHelper.startGlScissor(mc.getMainWindow().getScaledWidth() / 2 - 50, mc.getMainWindow().getScaledHeight() / 2 - 50, 100, 100);
        //        RenderHelper.startGlScissor(10, 10, mc.getMainWindow().getScaledWidth() - 20, mc.getMainWindow().getScaledHeight() - 20);

        RenderSystem.pushMatrix();

        //        RenderSystem.translatef(-15F, 15F, 0F);

        RenderHelper.drawColour(0xffffff, 255, 0, 0, mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight(), 0);

        RenderSystem.popMatrix();

        RenderHelper.endGlScissor();
    }

    public static void renderTestStencil()
    {
        //Basic stencil test
        Minecraft mc = Minecraft.getInstance();
        MainWindow reso = mc.getMainWindow();

        GL11.glEnable(GL11.GL_STENCIL_TEST);

        GlStateManager.colorMask(false, false, false, false);
        GlStateManager.depthMask(false);

        GL11.glStencilFunc(GL11.GL_NEVER, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_KEEP);

        GL11.glStencilMask(0xFF);
        GlStateManager.clear(GL11.GL_STENCIL_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);

        RenderHelper.drawColour(0xffffff, 255, 0, 0, 60, 60, 0);

        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.depthMask(true);

        GL11.glStencilMask(0x00);

        GL11.glStencilFunc(GL11.GL_EQUAL, 0, 0xFF);

        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);

        RenderHelper.drawColour(0xffffff, 255, 0, 0, reso.getScaledWidth(), reso.getScaledHeight(), 0);

        GL11.glDisable(GL11.GL_STENCIL_TEST);
    }

    public static HashSet<Framebuffer> frameBuffers = new HashSet<>();

    public static Framebuffer createFrameBuffer()
    {
        Minecraft mc = Minecraft.getInstance();
        Framebuffer render = new Framebuffer(mc.getMainWindow().getFramebufferWidth(), mc.getMainWindow().getFramebufferHeight(), true, Minecraft.IS_RUNNING_ON_MAC);
        if(mc.getFramebuffer().isStencilEnabled()) //if the main framebuffer is using a stencil, we might as well, too.
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

}
