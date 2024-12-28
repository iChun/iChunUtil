package me.ichun.mods.ichunutil.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.Random;
import java.util.function.Function;

public class RenderHelper
{
    public static MultiBufferSource.BufferSource getBufferSource()
    {
        return Minecraft.getInstance().renderBuffers().bufferSource(); //Minecraft.getInstance().renderBuffers().bufferSource() should be the same as GuiGraphics's bufferSource field
    }

    public static void draw(ResourceLocation resourceLocation, PoseStack stack, double posX, double posY, double width, double height, double zLevel)
    {
        draw(RenderType::guiTextured, resourceLocation, stack, posX, posY, width, height, zLevel);
    }

    public static void draw(Function<ResourceLocation, RenderType> renderTypeGetter, ResourceLocation resourceLocation, PoseStack stack, double posX, double posY, double width, double height, double zLevel)
    {
        draw(renderTypeGetter, resourceLocation, stack, posX, posY, width, height, zLevel, 0D, 1D, 0D, 1D);
    }

    public static void draw(ResourceLocation resourceLocation, PoseStack stack, double posX, double posY, double width, double height, double zLevel, double u1, double u2, double v1, double v2)
    {
        draw(RenderType::guiTextured, resourceLocation, stack, posX, posY, width, height, zLevel, u1, u2, v1, v2);
    }

    public static void draw(Function<ResourceLocation, RenderType> renderTypeGetter, ResourceLocation resourceLocation, PoseStack stack, double posX, double posY, double width, double height, double zLevel, double u1, double u2, double v1, double v2)
    {
        VertexConsumer vertexConsumer = startDrawBatch(renderTypeGetter, resourceLocation);
        drawBatch(stack, vertexConsumer, posX, posY, width, height, zLevel, u1, u2, v1, v2);
        getBufferSource().endLastBatch();
    }

    public static VertexConsumer startDrawBatch(ResourceLocation resourceLocation)
    {
        return startDrawBatch(RenderType::guiTextured, resourceLocation);
    }

    public static VertexConsumer startDrawBatch(Function<ResourceLocation, RenderType> renderTypeGetter, ResourceLocation resourceLocation)
    {
        //Grossly adapted from GuiGraphics' innerBlit method
        RenderType renderType = renderTypeGetter.apply(resourceLocation);
        return getBufferSource().getBuffer(renderType);
    }

    public static void drawBatch(PoseStack stack, VertexConsumer vertexConsumer, double posX, double posY, double width, double height, double zLevel, double u1, double u2, double v1, double v2)
    {
        Matrix4f matrix = stack.last().pose();
        vertexConsumer.addVertex(matrix, (float)posX, (float)(posY + height), (float)zLevel)          .setUv((float)u1, (float)v2).setColor(1F, 1F, 1F, 1F);
        vertexConsumer.addVertex(matrix, (float)(posX + width), (float)(posY + height), (float)zLevel).setUv((float)u2, (float)v2).setColor(1F, 1F, 1F, 1F);
        vertexConsumer.addVertex(matrix, (float)(posX + width), (float)posY, (float)zLevel)           .setUv((float)u2, (float)v1).setColor(1F, 1F, 1F, 1F);
        vertexConsumer.addVertex(matrix, (float)posX, (float)posY, (float)zLevel)                     .setUv((float)u1, (float)v1).setColor(1F, 1F, 1F, 1F);
    }

    public static void endDrawBatch()
    {
        getBufferSource().endLastBatch();
    }

    public static void drawColour(GuiGraphics graphics, int colour, int alpha, double posX, double posY, double width, double height, double zLevel)
    {
        int r = (colour >> 16 & 0xff);
        int g = (colour >> 8 & 0xff);
        int b = (colour & 0xff);
        drawColour(graphics, r, g, b, alpha, posX, posY, width, height, zLevel);
    }

    public static void drawColour(GuiGraphics graphics, int[] rgb, int alpha, double posX, double posY, double width, double height, double zLevel)
    {
        drawColour(graphics, rgb[0], rgb[1], rgb[2], alpha, posX, posY, width, height, zLevel);
    }

    public static void drawColour(GuiGraphics graphics, int r, int g, int b, int alpha, double posX, double posY, double width, double height, double zLevel)
    {
        if(width <= 0 || height <= 0)
        {
            return;
        }

        Matrix4f matrix = graphics.pose().last().pose();
        VertexConsumer bufferbuilder = getBufferSource().getBuffer(RenderType.gui());
        bufferbuilder.addVertex(matrix, (float)posX, (float)(posY + height), (float)zLevel).setColor(r, g, b, alpha);
        bufferbuilder.addVertex(matrix, (float)(posX + width), (float)(posY + height), (float)zLevel).setColor(r, g, b, alpha);
        bufferbuilder.addVertex(matrix, (float)(posX + width), (float)posY, (float)zLevel).setColor(r, g, b, alpha);
        bufferbuilder.addVertex(matrix, (float)posX, (float)posY, (float)zLevel).setColor(r, g, b, alpha);
        getBufferSource().endLastBatch();
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

        RenderSystem.enableScissor((int)Math.floor((double)x * scaleW), (int)Math.floor((double)mc.getWindow().getHeight() - ((double)(y + height) * scaleH)), (int)Math.floor((double)(x + width) * scaleW) - (int)Math.floor((double)x * scaleW), (int)Math.floor((double)mc.getWindow().getHeight() - ((double)y * scaleH)) - (int)Math.floor((double)mc.getWindow().getHeight() - ((double)(y + height) * scaleH))); //starts from lower left corner (minecraft starts from upper left)
    }

    public static void endGlScissor()
    {
        RenderSystem.disableScissor();
    }

    public static void renderTestScissor(GuiGraphics graphics)
    {
        //Basic scissor test
        Minecraft mc = Minecraft.getInstance();

        RenderHelper.startGlScissor(mc.getWindow().getGuiScaledWidth() / 2 - 50, mc.getWindow().getGuiScaledHeight() / 2 - 50, 100, 100);
        //        RenderHelper.startGlScissor(10, 10, mc.getMainWindow().getScaledWidth() - 20, mc.getMainWindow().getScaledHeight() - 20);

        RenderHelper.drawColour(graphics, 0xffffff, 255, 0, 0, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight(), 0);

        RenderHelper.endGlScissor();
    }
}
