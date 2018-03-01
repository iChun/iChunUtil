package me.ichun.mods.ichunutil.client.module.eula;

import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEulaNotifier extends Gui
{
    private static final ResourceLocation achievementBg = new ResourceLocation("textures/gui/toasts.png");
    private Minecraft mc;

    private static GuiEulaNotifier instance;
    private static long notificationTime;
    public static boolean notifyAll;

    public static void update()
    {
        if(instance != null)
        {
            instance.updateWindow();
        }
    }

    public static void createIfRequired()
    {
        if(instance == null && !(iChunUtil.config.eulaAcknowledged.equalsIgnoreCase("true") || iChunUtil.config.eulaAcknowledged.equalsIgnoreCase(iChunUtil.proxy.getPlayerName())))
        {
            instance = new GuiEulaNotifier(Minecraft.getMinecraft());
            notifyUpdates();
        }
    }

    public GuiEulaNotifier(Minecraft mc)
    {
        this.mc = mc;
    }

    public static void notifyUpdates()
    {
        notificationTime = Minecraft.getSystemTime() + 5000L;
    }

    private void updateWindowScale()
    {
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        GlStateManager.clear(256);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, (double)scaledresolution.getScaledWidth(), (double)scaledresolution.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
    }

    public void updateWindow()
    {
        if(notificationTime != 0L)
        {
            double d0 = (double)(Minecraft.getSystemTime() - notificationTime) / 5000.0D;

            if(d0 < -5.0D || d0 > 1.0D)
            {
                notificationTime = 0L;
                iChunUtil.config.eulaAcknowledged = iChunUtil.proxy.getPlayerName();
                iChunUtil.config.save();
                return;
            }

            this.updateWindowScale();
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);

            GlStateManager.pushMatrix();

            double d1 = d0 * 2.0D;

            if(d1 > 1.0D)
            {
                d1 = 2.0D - d1;
            }

            d1 = d1 * 4.0D;
            d1 = 1.0D - d1;

            if(d1 < 0.0D)
            {
                d1 = 0.0D;
            }

            d1 *= d1;
            d1 *= d1;
            int i = 0;
            int j = - (int)(d1 * 36.0D);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableTexture2D();
            this.mc.getTextureManager().bindTexture(achievementBg);
            GlStateManager.disableLighting();
            this.drawTexturedModalRect(i, j, 0, 0, 160, 32);

            this.mc.fontRenderer.drawString(new TextComponentTranslation("ichunutil.eula.toast1").getFormattedText(), i + 10, j + 7, -1);
            this.mc.fontRenderer.drawString(new TextComponentTranslation("ichunutil.eula.toast2").getFormattedText(), i + 15, j + 18, -1);

            RenderHelper.enableGUIStandardItemLighting();

            GlStateManager.popMatrix();

            GlStateManager.disableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
        }
    }
}