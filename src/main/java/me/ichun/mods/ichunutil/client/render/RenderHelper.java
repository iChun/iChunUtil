package me.ichun.mods.ichunutil.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Random;

public class RenderHelper
{
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
        bufferbuilder.pos(posX, posY + height, zLevel).tex(u1, v2).endVertex();
        bufferbuilder.pos(posX + width, posY + height, zLevel).tex(u2, v2).endVertex();
        bufferbuilder.pos(posX + width, posY, zLevel).tex(u2, v1).endVertex();
        bufferbuilder.pos(posX, posY, zLevel).tex(u1, v1).endVertex();
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
        GlStateManager.disableTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(posX, posY + height, zLevel).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(posX + width, posY + height, zLevel).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(posX + width, posY, zLevel).color(r, g, b, alpha).endVertex();
        bufferbuilder.pos(posX, posY, zLevel).color(r, g, b, alpha).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture();
    }

    public static void colour(int color)
    {
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        GlStateManager.color4f(r, g, b, 1.0F);
    }

    public static void colour(int color, float alpha)
    {
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        GlStateManager.color4f(r, g, b, alpha);
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

        double scaleW = (double)mc.mainWindow.getFramebufferWidth() / mc.mainWindow.getScaledWidth();
        double scaleH = (double)mc.mainWindow.getFramebufferHeight() / mc.mainWindow.getScaledHeight();

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

        GL11.glScissor((int)Math.floor((double)x * scaleW), (int)Math.floor((double)mc.mainWindow.getFramebufferHeight() - ((double)(y + height) * scaleH)), (int)Math.floor((double)(x + width) * scaleW) - (int)Math.floor((double)x * scaleW), (int)Math.floor((double)mc.mainWindow.getFramebufferHeight() - ((double)y * scaleH)) - (int)Math.floor((double)mc.mainWindow.getFramebufferHeight() - ((double)(y + height) * scaleH))); //starts from lower left corner (minecraft starts from upper left)
    }

    public static void endGlScissor()
    {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public static void renderTestScissor()
    {
        //Basic scissor test
        Minecraft mc = Minecraft.getInstance();

        RenderHelper.startGlScissor(mc.mainWindow.getScaledWidth() / 2 - 50, mc.mainWindow.getScaledHeight() / 2 - 50, 100, 100);
        //        RenderHelper.startGlScissor(10, 10, mc.mainWindow.getScaledWidth() - 20, mc.mainWindow.getScaledHeight() - 20);

        GlStateManager.pushMatrix();

        //        GlStateManager.translatef(-15F, 15F, 0F);

        RenderHelper.drawColour(0xffffff, 255, 0, 0, mc.mainWindow.getScaledWidth(), mc.mainWindow.getScaledHeight(), 0);

        GlStateManager.popMatrix();

        RenderHelper.endGlScissor();
    }
}
