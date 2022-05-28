package me.ichun.mods.ichunutil.client.render;

import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import me.ichun.mods.ichunutil.loader.LoaderDelegate;
import me.ichun.mods.ichunutil.loader.LoaderHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class RenderHelper
{
    public static void renderModel(BakedModel modelIn, ItemStack stack, int combinedLightIn, int combinedOverlayIn, PoseStack matrixStackIn, VertexConsumer bufferIn) {
        Random random = new Random();
        long i = 42L;

        for(Direction direction : Direction.values()) {
            random.setSeed(42L);
            renderQuads(matrixStackIn, bufferIn, modelIn.getQuads((BlockState)null, direction, random), stack, combinedLightIn, combinedOverlayIn);
        }

        random.setSeed(42L);
        renderQuads(matrixStackIn, bufferIn, modelIn.getQuads((BlockState)null, (Direction)null, random), stack, combinedLightIn, combinedOverlayIn);
    }

    private static void renderQuads(PoseStack matrixStackIn, VertexConsumer bufferIn, List<BakedQuad> quadsIn, ItemStack itemStackIn, int combinedLightIn, int combinedOverlayIn) {
        boolean flag = !itemStackIn.isEmpty();
        PoseStack.Pose matrixstack$entry = matrixStackIn.last();

        for(BakedQuad bakedquad : quadsIn) {
            int i = -1;
            if (flag && bakedquad.isTinted()) {
                i = Minecraft.getInstance().getItemRenderer().itemColors.getColor(itemStackIn, bakedquad.getTintIndex());
            }

            float f = (float)(i >> 16 & 255) / 255.0F;
            float f1 = (float)(i >> 8 & 255) / 255.0F;
            float f2 = (float)(i & 255) / 255.0F;
            bufferIn.putBulkData(matrixstack$entry, bakedquad, f, f1, f2, combinedLightIn, combinedOverlayIn);
        }

    }

//    public static void renderBakedModel(BakedModel modelIn, ItemStack itemStackIn)
//    {
//        renderBakedModel(modelIn, itemStackIn, null);
//    }
//
//    public static void renderBakedModel(BakedModel modelIn, ItemStack itemStackIn, RenderType renderTypeOverride)
//    {
//        renderBakedModel(modelIn, itemStackIn, renderTypeOverride, new PoseStack(), Minecraft.getInstance().renderBuffers().bufferSource());
//    }
//
//    public static void renderBakedModel(BakedModel pModel, ItemStack pItemStack, RenderType renderTypeOverride, PoseStack matrixStackIn, MultiBufferSource buffer)
//    {
//        Minecraft mc = Minecraft.getInstance();
//
//        PoseStack poseStack = RenderSystem.getModelViewStack();
//        boolean flag4 = !pModel.usesBlockLight();
//        if(renderTypeOverride != null)
//        {
//            //ItemRenderer.renderGuiItem
//            mc.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
//            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
//            RenderSystem.enableBlend();
//            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
//            poseStack.pushPose();
//            //setupGuiTransform removed
//            if (flag4) {
//                Lighting.setupForFlatItems();
//            }
//        }
//
//        //renderitem //TODO why can't I just call ItemRenderer.renderItem???
//        if (!pItemStack.isEmpty()) {
//            matrixStackIn.pushPose();
//            if (pItemStack.getItem() == Items.TRIDENT) {
//                pModel = mc.getItemRenderer().getItemModelShaper().getModelManager().getModel(new ModelResourceLocation("minecraft:trident#inventory"));
//            }
//            else if (pItemStack.is(Items.SPYGLASS)) {
//                pModel = mc.getItemRenderer().getItemModelShaper().getModelManager().getModel(new ModelResourceLocation("minecraft:spyglass#inventory"));
//            }
//
//            pModel = LoaderHandler.d().getCameraTransformsModel(matrixStackIn, pModel, ItemTransforms.TransformType.NONE, false);
//            matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
//            if (!pModel.isCustomRenderer()) {
//                boolean flag1;
//                if (!ItemTransforms.TransformType.NONE.firstPerson() && pItemStack.getItem() instanceof BlockItem) {
//                    Block block = ((BlockItem)pItemStack.getItem()).getBlock();
//                    flag1 = !(block instanceof HalfTransparentBlock) && !(block instanceof StainedGlassPaneBlock);
//                } else {
//                    flag1 = true;
//                }
//                if (LoaderHandler.d().doItemModelIsLayered(Minecraft.getInstance().getItemRenderer(), pModel, pItemStack, matrixStackIn, buffer, 0xf000f0, OverlayTexture.NO_OVERLAY, flag1)) {}
//                else {
//                    RenderType rendertype = renderTypeOverride != null ? renderTypeOverride : ItemBlockRenderTypes.getRenderType(pItemStack, flag1);
//                    VertexConsumer ivertexbuilder;
//                    if (pItemStack.getItem() == Items.COMPASS && pItemStack.hasFoil()) {
//                        matrixStackIn.pushPose();
//                        PoseStack.Pose matrixstack$entry = matrixStackIn.last();
//                        if (ItemTransforms.TransformType.NONE.firstPerson()) {
//                            matrixstack$entry.pose().multiply(0.75F);
//                        }
//
//                        if (flag1) {
//                            ivertexbuilder = ItemRenderer.getCompassFoilBufferDirect(buffer, rendertype, matrixstack$entry);
//                        } else {
//                            ivertexbuilder = ItemRenderer.getCompassFoilBuffer(buffer, rendertype, matrixstack$entry);
//                        }
//
//                        matrixStackIn.popPose();
//                    } else if (flag1) {
//                        ivertexbuilder = ItemRenderer.getFoilBufferDirect(buffer, rendertype, true, pItemStack.hasFoil());
//                    } else {
//                        ivertexbuilder = ItemRenderer.getFoilBuffer(buffer, rendertype, true, pItemStack.hasFoil());
//                    }
//
//                    //renderModel
//                    renderModel(pModel, pItemStack, 0xf000f0, OverlayTexture.NO_OVERLAY, matrixStackIn, ivertexbuilder);
//                    //end renderModel
//                }
//            } else {
//                LoaderHandler.d().customRendererRenderByItem(pItemStack, ItemTransforms.TransformType.NONE, matrixStackIn, buffer, 15728880, OverlayTexture.NO_OVERLAY);
//            }
//
//            matrixStackIn.popPose();
//        }
//        //end renderitem
//
//        if(buffer instanceof MultiBufferSource.BufferSource)
//        {
//            ((MultiBufferSource.BufferSource)buffer).endBatch();
//        }
//        if(renderTypeOverride != null)
//        {
//            RenderSystem.enableDepthTest();
//            if(flag4)
//            {
//                com.mojang.blaze3d.platform.Lighting.setupFor3DItems();
//            }
//
//            poseStack.popPose();
//            RenderSystem.applyModelViewMatrix();
//        }
//    }

    public static TextureAtlasSprite buildTASFromNativeImage(@Nonnull ResourceLocation rl, @Nonnull NativeImage image)
    {
        return new TextureAtlasSprite(Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS), new TextureAtlasSprite.Info(rl, image.getWidth(), image.getHeight(), AnimationMetadataSection.EMPTY), Minecraft.getInstance().options.mipmapLevels, image.getWidth(), image.getHeight(), 0, 0, image);
    }

    public static PoseStack.Pose createInterimStackEntry(PoseStack.Pose prevEntry, PoseStack.Pose nextEntry, float prog)
    {
        //create a copy of prevEntry
        PoseStack stack = new PoseStack();
        PoseStack.Pose last = stack.last();

        //set to the last entry
        last.pose().multiply(prevEntry.pose());
        last.normal().mul(prevEntry.normal());

        //get the difference
        //matrix
        Matrix4f subtractMatrix = prevEntry.pose().copy();
        subtractMatrix.multiply(-1F);
        Matrix4f diffMatrix = nextEntry.pose().copy();
        diffMatrix.add(subtractMatrix);
        diffMatrix.multiply(prog);
        last.pose().add(diffMatrix);

        //normal... no add function
        Matrix3f lastNormal = last.normal();
        Matrix3f prevNormal = prevEntry.normal().copy();
        Matrix3f nextNormal = nextEntry.normal().copy();
        lastNormal.m00 = prevNormal.m00 + (nextNormal.m00 - prevNormal.m00) * prog;
        lastNormal.m01 = prevNormal.m01 + (nextNormal.m01 - prevNormal.m01) * prog;
        lastNormal.m02 = prevNormal.m02 + (nextNormal.m02 - prevNormal.m02) * prog;
        lastNormal.m10 = prevNormal.m10 + (nextNormal.m10 - prevNormal.m10) * prog;
        lastNormal.m11 = prevNormal.m11 + (nextNormal.m11 - prevNormal.m11) * prog;
        lastNormal.m12 = prevNormal.m12 + (nextNormal.m12 - prevNormal.m12) * prog;
        lastNormal.m20 = prevNormal.m20 + (nextNormal.m20 - prevNormal.m20) * prog;
        lastNormal.m21 = prevNormal.m21 + (nextNormal.m21 - prevNormal.m21) * prog;
        lastNormal.m22 = prevNormal.m22 + (nextNormal.m22 - prevNormal.m22) * prog;

        return last;
    }

    public static void drawTexture(PoseStack stack, ResourceLocation resource, double posX, double posY, double width, double height, double zLevel)
    {
        RenderSystem.setShaderTexture(0, resource);
        draw(stack, posX, posY, width, height, zLevel);
    }

    public static void draw(PoseStack stack, double posX, double posY, double width, double height, double zLevel)
    {
        draw(stack, posX, posY, width, height, zLevel, 0D, 1D, 0D, 1D);
    }

    public static void draw(PoseStack stack, double posX, double posY, double width, double height, double zLevel, double u1, double u2, double v1, double v2)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix = stack.last().pose();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix, (float)posX, (float)(posY + height), (float)zLevel).uv((float)u1, (float)v2).endVertex();
        bufferbuilder.vertex(matrix, (float)(posX + width), (float)(posY + height), (float)zLevel).uv((float)u2, (float)v2).endVertex();
        bufferbuilder.vertex(matrix, (float)(posX + width), (float)posY, (float)zLevel).uv((float)u2, (float)v1).endVertex();
        bufferbuilder.vertex(matrix, (float)posX, (float)posY, (float)zLevel).uv((float)u1, (float)v1).endVertex();
        tessellator.end();
    }

    public static void startDrawBatch()
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
    }

    public static void drawBatch(PoseStack stack, double posX, double posY, double width, double height, double zLevel, double u1, double u2, double v1, double v2)
    {
        Matrix4f matrix = stack.last().pose();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.vertex(matrix, (float)posX, (float)(posY + height), (float)zLevel).uv((float)u1, (float)v2).endVertex();
        bufferbuilder.vertex(matrix, (float)(posX + width), (float)(posY + height), (float)zLevel).uv((float)u2, (float)v2).endVertex();
        bufferbuilder.vertex(matrix, (float)(posX + width), (float)posY, (float)zLevel).uv((float)u2, (float)v1).endVertex();
        bufferbuilder.vertex(matrix, (float)posX, (float)posY, (float)zLevel).uv((float)u1, (float)v1).endVertex();
    }

    public static void endDrawBatch()
    {
        Tesselator.getInstance().end();
    }

    public static void drawColour(PoseStack stack, int colour, int alpha, double posX, double posY, double width, double height, double zLevel)
    {
        int r = (colour >> 16 & 0xff);
        int g = (colour >> 8 & 0xff);
        int b = (colour & 0xff);
        drawColour(stack, r, g, b, alpha, posX, posY, width, height, zLevel);
    }

    public static void drawColour(PoseStack stack, int r, int g, int b, int alpha, double posX, double posY, double width, double height, double zLevel)
    {
        if(width <= 0 || height <= 0)
        {
            return;
        }
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Matrix4f matrix = stack.last().pose();
        RenderSystem.disableTexture();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(matrix, (float)posX, (float)(posY + height), (float)zLevel).color(r, g, b, alpha).endVertex();
        bufferbuilder.vertex(matrix, (float)(posX + width), (float)(posY + height), (float)zLevel).color(r, g, b, alpha).endVertex();
        bufferbuilder.vertex(matrix, (float)(posX + width), (float)posY, (float)zLevel).color(r, g, b, alpha).endVertex();
        bufferbuilder.vertex(matrix, (float)posX, (float)posY, (float)zLevel).color(r, g, b, alpha).endVertex();
        tessellator.end();
        RenderSystem.enableTexture();
    }

    public static void colour(int color)
    {
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        RenderSystem.setShaderColor(r, g, b, 1.0F);
    }

    public static void colour(int color, float alpha)
    {
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        RenderSystem.setShaderColor(r, g, b, alpha);
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

        double scaleW = (double)mc.getWindow().getWidth() / mc.getWindow().getGuiScaledWidth();
        double scaleH = (double)mc.getWindow().getHeight() / mc.getWindow().getGuiScaledHeight();

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

        GL11.glScissor((int)Math.floor((double)x * scaleW), (int)Math.floor((double)mc.getWindow().getHeight() - ((double)(y + height) * scaleH)), (int)Math.floor((double)(x + width) * scaleW) - (int)Math.floor((double)x * scaleW), (int)Math.floor((double)mc.getWindow().getHeight() - ((double)y * scaleH)) - (int)Math.floor((double)mc.getWindow().getHeight() - ((double)(y + height) * scaleH))); //starts from lower left corner (minecraft starts from upper left)
    }

    public static void endGlScissor()
    {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public static void renderTestScissor(PoseStack stack)
    {
        //Basic scissor test
        Minecraft mc = Minecraft.getInstance();

        RenderHelper.startGlScissor(mc.getWindow().getGuiScaledWidth() / 2 - 50, mc.getWindow().getGuiScaledHeight() / 2 - 50, 100, 100);
        //        RenderHelper.startGlScissor(10, 10, mc.getMainWindow().getScaledWidth() - 20, mc.getMainWindow().getScaledHeight() - 20);

        PoseStack poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();

        //        RenderSystem.translatef(-15F, 15F, 0F);

        RenderHelper.drawColour(stack, 0xffffff, 255, 0, 0, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight(), 0);

        poseStack.popPose();

        RenderHelper.endGlScissor();
    }

    public static void renderTestStencil(PoseStack stack)
    {
        //Basic stencil test
        Minecraft mc = Minecraft.getInstance();
        Window reso = mc.getWindow();

        GL11.glEnable(GL11.GL_STENCIL_TEST);

        GlStateManager._colorMask(false, false, false, false);
        GlStateManager._depthMask(false);

        GL11.glStencilFunc(GL11.GL_NEVER, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_KEEP);

        GL11.glStencilMask(0xFF);
        GlStateManager._clear(GL11.GL_STENCIL_BUFFER_BIT, Minecraft.ON_OSX);

        RenderHelper.drawColour(stack, 0xffffff, 255, 0, 0, 60, 60, 0);

        GlStateManager._colorMask(true, true, true, true);
        GlStateManager._depthMask(true);

        GL11.glStencilMask(0x00);

        GL11.glStencilFunc(GL11.GL_EQUAL, 0, 0xFF);

        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);

        RenderHelper.drawColour(stack, 0xffffff, 255, 0, 0, reso.getGuiScaledWidth(), reso.getGuiScaledHeight(), 0);

        GL11.glDisable(GL11.GL_STENCIL_TEST);
    }

    public static HashSet<MainTarget> frameBuffers = new HashSet<>();

    public static MainTarget createFrameBuffer()
    {
        Minecraft mc = Minecraft.getInstance();
        MainTarget render = new MainTarget(mc.getWindow().getWidth(), mc.getWindow().getHeight());
        render.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        render.clear(Minecraft.ON_OSX);
        LoaderHandler.d().checkEnableStencil(render);
        frameBuffers.add(render);
        return render;
    }

    public static void deleteFrameBuffer(MainTarget buffer)
    {
        if(buffer.frameBufferId >= 0)
        {
            buffer.destroyBuffers();
        }
        frameBuffers.remove(buffer);
    }
}
