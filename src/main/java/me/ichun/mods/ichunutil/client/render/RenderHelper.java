package me.ichun.mods.ichunutil.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import cpw.mods.modlauncher.api.INameMappingService;
import me.ichun.mods.ichunutil.common.util.ObfHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BreakableBlock;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class RenderHelper
{
    public static void renderModel(IBakedModel modelIn, ItemStack stack, int combinedLightIn, int combinedOverlayIn, MatrixStack matrixStackIn, IVertexBuilder bufferIn) {
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

        boolean flag4 = !modelIn.isSideLit();
        if(renderTypeOverride != null)
        {
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
            if (flag4) {
                net.minecraft.client.renderer.RenderHelper.setupGuiFlatDiffuseLighting();
            }
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
                boolean flag1;
                if (!ItemCameraTransforms.TransformType.NONE.isFirstPerson() && itemStackIn.getItem() instanceof BlockItem) {
                    Block block = ((BlockItem)itemStackIn.getItem()).getBlock();
                    flag1 = !(block instanceof BreakableBlock) && !(block instanceof StainedGlassPaneBlock);
                } else {
                    flag1 = true;
                }
                if (modelIn.isLayered()) { net.minecraftforge.client.ForgeHooksClient.drawItemLayered(Minecraft.getInstance().getItemRenderer(), modelIn, itemStackIn, matrixStackIn, buffer, 0xf000f0, OverlayTexture.NO_OVERLAY, flag1); }
                else {
                    RenderType rendertype = renderTypeOverride != null ? renderTypeOverride : RenderTypeLookup.func_239219_a_(itemStackIn, flag1);
                    IVertexBuilder ivertexbuilder;
                    if (itemStackIn.getItem() == Items.COMPASS && itemStackIn.hasEffect()) {
                        matrixStackIn.push();
                        MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();
                        if (ItemCameraTransforms.TransformType.NONE.isFirstPerson()) {
                            matrixstack$entry.getMatrix().mul(0.75F);
                        }

                        if (flag1) {
                            ivertexbuilder = ItemRenderer.getDirectGlintVertexBuilder(buffer, rendertype, matrixstack$entry);
                        } else {
                            ivertexbuilder = ItemRenderer.getGlintVertexBuilder(buffer, rendertype, matrixstack$entry);
                        }

                        matrixStackIn.pop();
                    } else if (flag1) {
                        ivertexbuilder = ItemRenderer.getEntityGlintVertexBuilder(buffer, rendertype, true, itemStackIn.hasEffect());
                    } else {
                        ivertexbuilder = ItemRenderer.getBuffer(buffer, rendertype, true, itemStackIn.hasEffect());
                    }

                    //renderModel
                    renderModel(modelIn, itemStackIn, 0xf000f0, OverlayTexture.NO_OVERLAY, matrixStackIn, ivertexbuilder);
                    //end renderModel
                }
            } else {
                itemStackIn.getItem().getItemStackTileEntityRenderer().func_239207_a_(itemStackIn, ItemCameraTransforms.TransformType.NONE, matrixStackIn, buffer, 15728880, OverlayTexture.NO_OVERLAY);
            }

            matrixStackIn.pop();
        }
        //end renderitem

        if(buffer instanceof IRenderTypeBuffer.Impl)
        {
            ((IRenderTypeBuffer.Impl)buffer).finish();
        }
        if(renderTypeOverride != null)
        {
            RenderSystem.enableDepthTest();
            if(flag4)
            {
                net.minecraft.client.renderer.RenderHelper.setupGui3DDiffuseLighting();
            }

            RenderSystem.disableAlphaTest();
            RenderSystem.disableRescaleNormal();
            RenderSystem.popMatrix();
        }
    }

    public static TextureAtlasSprite buildTASFromNativeImage(@Nonnull ResourceLocation rl, @Nonnull NativeImage image)
    {
        return new TextureAtlasSprite(Minecraft.getInstance().getModelManager().getAtlasTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE), new TextureAtlasSprite.Info(rl, image.getWidth(), image.getHeight(), AnimationMetadataSection.EMPTY), Minecraft.getInstance().gameSettings.mipmapLevels, image.getWidth(), image.getHeight(), 0, 0, image);
    }

    public static void multiplyStackWithStack(@Nonnull MatrixStack stack, @Nonnull MatrixStack otherStack)
    {
        MatrixStack.Entry entLast = stack.getLast();
        MatrixStack.Entry correctorLast = otherStack.getLast();

        entLast.getMatrix().mul(correctorLast.getMatrix());
        entLast.getNormal().mul(correctorLast.getNormal());
    }

    public static MatrixStack.Entry createInterimStackEntry(MatrixStack.Entry prevEntry, MatrixStack.Entry nextEntry, float prog)
    {
        //create a copy of prevEntry
        MatrixStack stack = new MatrixStack();
        MatrixStack.Entry last = stack.getLast();

        //set to the last entry
        last.getMatrix().mul(prevEntry.getMatrix());
        last.getNormal().mul(prevEntry.getNormal());

        //get the difference
        //matrix
        Matrix4f subtractMatrix = prevEntry.getMatrix().copy();
        subtractMatrix.mul(-1F);
        Matrix4f diffMatrix = nextEntry.getMatrix().copy();
        diffMatrix.add(subtractMatrix);
        diffMatrix.mul(prog);
        last.getMatrix().add(diffMatrix);

        //normal... no add function
        Matrix3f lastNormal = last.getNormal();
        Matrix3f prevNormal = prevEntry.getNormal().copy();
        Matrix3f nextNormal = nextEntry.getNormal().copy();
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

    public static void drawTexture(MatrixStack stack, ResourceLocation resource, double posX, double posY, double width, double height, double zLevel)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(resource);
        draw(stack, posX, posY, width, height, zLevel);
    }

    public static void draw(MatrixStack stack, double posX, double posY, double width, double height, double zLevel)
    {
        draw(stack, posX, posY, width, height, zLevel, 0D, 1D, 0D, 1D);
    }

    public static void draw(MatrixStack stack, double posX, double posY, double width, double height, double zLevel, double u1, double u2, double v1, double v2)
    {
        Matrix4f matrix = stack.getLast().getMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(matrix, (float)posX, (float)(posY + height), (float)zLevel).tex((float)u1, (float)v2).endVertex();
        bufferbuilder.pos(matrix, (float)(posX + width), (float)(posY + height), (float)zLevel).tex((float)u2, (float)v2).endVertex();
        bufferbuilder.pos(matrix, (float)(posX + width), (float)posY, (float)zLevel).tex((float)u2, (float)v1).endVertex();
        bufferbuilder.pos(matrix, (float)posX, (float)posY, (float)zLevel).tex((float)u1, (float)v1).endVertex();
        tessellator.draw();
    }

    public static void startDrawBatch()
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
    }

    public static void drawBatch(MatrixStack stack, double posX, double posY, double width, double height, double zLevel, double u1, double u2, double v1, double v2)
    {
        Matrix4f matrix = stack.getLast().getMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.pos(matrix, (float)posX, (float)(posY + height), (float)zLevel).tex((float)u1, (float)v2).endVertex();
        bufferbuilder.pos(matrix, (float)(posX + width), (float)(posY + height), (float)zLevel).tex((float)u2, (float)v2).endVertex();
        bufferbuilder.pos(matrix, (float)(posX + width), (float)posY, (float)zLevel).tex((float)u2, (float)v1).endVertex();
        bufferbuilder.pos(matrix, (float)posX, (float)posY, (float)zLevel).tex((float)u1, (float)v1).endVertex();
    }

    public static void endDrawBatch()
    {
        Tessellator.getInstance().draw();
    }

    public static void drawColour(MatrixStack stack, int colour, int alpha, double posX, double posY, double width, double height, double zLevel)
    {
        int r = (colour >> 16 & 0xff);
        int g = (colour >> 8 & 0xff);
        int b = (colour & 0xff);
        drawColour(stack, r, g, b, alpha, posX, posY, width, height, zLevel);
    }

    public static void drawColour(MatrixStack stack, int r, int g, int b, int alpha, double posX, double posY, double width, double height, double zLevel)
    {
        if(width <= 0 || height <= 0)
        {
            return;
        }
        Matrix4f matrix = stack.getLast().getMatrix();
        RenderSystem.disableTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(matrix, (float)posX, (float)(posY + height), (float)zLevel).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(matrix, (float)(posX + width), (float)(posY + height), (float)zLevel).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(matrix, (float)(posX + width), (float)posY, (float)zLevel).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(matrix, (float)posX, (float)posY, (float)zLevel).color(r, g, b, alpha).endVertex();
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

    public static void renderTestScissor(MatrixStack stack)
    {
        //Basic scissor test
        Minecraft mc = Minecraft.getInstance();

        RenderHelper.startGlScissor(mc.getMainWindow().getScaledWidth() / 2 - 50, mc.getMainWindow().getScaledHeight() / 2 - 50, 100, 100);
        //        RenderHelper.startGlScissor(10, 10, mc.getMainWindow().getScaledWidth() - 20, mc.getMainWindow().getScaledHeight() - 20);

        RenderSystem.pushMatrix();

        //        RenderSystem.translatef(-15F, 15F, 0F);

        RenderHelper.drawColour(stack, 0xffffff, 255, 0, 0, mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight(), 0);

        RenderSystem.popMatrix();

        RenderHelper.endGlScissor();
    }

    public static void renderTestStencil(MatrixStack stack)
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

        RenderHelper.drawColour(stack, 0xffffff, 255, 0, 0, 60, 60, 0);

        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.depthMask(true);

        GL11.glStencilMask(0x00);

        GL11.glStencilFunc(GL11.GL_EQUAL, 0, 0xFF);

        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);

        RenderHelper.drawColour(stack, 0xffffff, 255, 0, 0, reso.getScaledWidth(), reso.getScaledHeight(), 0);

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

    //REFLECTIVE METHODS
    @OnlyIn(Dist.CLIENT)
    @Nullable
    public static <T extends EntityRenderer<?>, V extends Entity> ResourceLocation getEntityTexture(T rend, V ent)
    {
        return getEntityTexture(rend, rend.getClass(), ent);
    }

    @OnlyIn(Dist.CLIENT)
    public static <T extends LivingRenderer<?, ?>, V extends LivingEntity> void invokePreRenderCallback(T rend, V ent, MatrixStack stack, float rendTick)
    {
        invokePreRenderCallback(rend, rend.getClass(), ent, stack, rendTick);
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    private static <T extends EntityRenderer<?>, V extends Entity> ResourceLocation getEntityTexture(T rend, Class clz, V ent)
    {
        try
        {
            Method m = clz.getDeclaredMethod(ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, ObfHelper.getEntityTexture), Entity.class);
            m.setAccessible(true);
            return (ResourceLocation)m.invoke(rend, ent);
        }
        catch(NoSuchMethodException e)
        {
            if(clz != EntityRenderer.class)
            {
                return getEntityTexture(rend, clz.getSuperclass(), ent);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    private static <T extends LivingRenderer<?, ?>, V extends LivingEntity> void invokePreRenderCallback(T rend, Class clz, V ent, MatrixStack stack, float rendTick)
    {
        try
        {
            Method m = clz.getDeclaredMethod(ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, ObfHelper.preRenderCallback), LivingEntity.class, MatrixStack.class, float.class);
            m.setAccessible(true);
            m.invoke(rend, ent, stack, rendTick);
        }
        catch(NoSuchMethodException e)
        {
            if(clz != LivingRenderer.class)
            {
                invokePreRenderCallback(rend, clz.getSuperclass(), ent, stack, rendTick);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
