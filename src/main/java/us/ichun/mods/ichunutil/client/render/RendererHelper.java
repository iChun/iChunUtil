package us.ichun.mods.ichunutil.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class RendererHelper
{
    public static final ResourceLocation texEnchant = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    //TODO GlStateManager replacing all GL11 stuffs.

    // Taken from copygirl. Thanks!
    //TODO renderItem
    //	public static void renderItemIn3d(ItemStack stack)
    //	{
    //		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
    //		// Not sure why but this can be null when the world loads.
    //		if (textureManager == null) return;
    //		Item item = stack.getItem();
    //
    //		GlStateManager.pushMatrix();
    //
    //		Tessellator tessellator = Tessellator.instance;
    //		GlStateManager.enableRescaleNormal();
    //		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
    //		GlStateManager.translate(-0.5F, -0.5F, 1 / 32.0F);
    //
    //		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    //
    //		int passes = item.getRenderPasses(stack.getItemDamage());
    //		for (int pass = 0; pass < passes; pass++) {
    //			textureManager.bindTexture(((stack.getItemSpriteNumber() == 0) ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture));
    //            IIcon icon = item.getIcon(stack, pass);
    //			float minU = icon.getMinU();
    //			float maxU = icon.getMaxU();
    //			float minV = icon.getMinV();
    //			float maxV = icon.getMaxV();
    //			setColorFromInt(item.getColorFromItemStack(stack, pass));
    //			ItemRenderer.renderItemIn2D(tessellator, maxU, minV, minU, maxV, icon.getIconWidth(), icon.getIconHeight(), 0.0625F);
    //		}
    //
    //		if (stack.hasEffect(0)) {
    //			GL11.glDepthFunc(GL11.GL_EQUAL);
    //			GlStateManager.disableLighting();
    //			textureManager.bindTexture(texEnchant);
    //			GlStateManager.enableBlend();
    //			GlStateManager.blendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
    //			float f7 = 0.76F;
    //			GlStateManager.color(0.5F * f7, 0.25F * f7, 0.8F * f7, 1.0F);
    //			GL11.glMatrixMode(GL11.GL_TEXTURE);
    //			GlStateManager.pushMatrix();
    //			float f8 = 0.125F;
    //			GlStateManager.scale(f8, f8, f8);
    //			float f9 = Minecraft.getSystemTime() % 3000L / 3000.0F * 8.0F;
    //			GlStateManager.translate(f9, 0.0F, 0.0F);
    //			GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
    //			ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
    //			GlStateManager.popMatrix();
    //			GlStateManager.pushMatrix();
    //			GlStateManager.scale(f8, f8, f8);
    //			f9 = Minecraft.getSystemTime() % 4873L / 4873.0F * 8.0F;
    //			GlStateManager.translate(-f9, 0.0F, 0.0F);
    //			GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
    //			ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
    //			GlStateManager.popMatrix();
    //			GL11.glMatrixMode(GL11.GL_MODELVIEW);
    //			GlStateManager.disableBlend();
    //			GlStateManager.enableLighting();
    //			GL11.glDepthFunc(GL11.GL_LEQUAL);
    //		}
    //
    //		GlStateManager.disableRescaleNormal();
    //
    //		GlStateManager.popMatrix();
    //	}

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

    //TODO "Disabeld StencilBits by default, to prevent issues with intel cards. You must now opt-in to enabeling stencil bits by suppling the -Dforge.forceDisplayStencil=true flag."
    //TODO find a way to detect/enable stencils in 1.8
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
