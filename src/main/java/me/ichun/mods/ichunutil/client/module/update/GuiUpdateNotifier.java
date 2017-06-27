package me.ichun.mods.ichunutil.client.module.update;

import me.ichun.mods.ichunutil.common.module.update.UpdateChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.TreeSet;

@SideOnly(Side.CLIENT)
public class GuiUpdateNotifier extends Gui
{
    private static final ResourceLocation achievementBg = new ResourceLocation("textures/gui/achievement/achievement_background.png");
    private Minecraft mc;

    private static GuiUpdateNotifier instance;
    private static long notificationTime;
    public static boolean notifyAll;

    public static void update()
    {
        if(instance == null)
        {
            if(UpdateChecker.hasCheckedForUpdates() && !UpdateChecker.getModsWithUpdates().isEmpty())
            {
                instance = new GuiUpdateNotifier(Minecraft.getMinecraft());
            }
        }
        else
        {
            instance.updateWindow();
        }
    }

    public GuiUpdateNotifier(Minecraft mc)
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
        if(notificationTime != 0L && mc.player != null)
        {
            double d0 = (double)(Minecraft.getSystemTime() - notificationTime) / 3000.0D;

            if(d0 < -5.0D || d0 > 1.0D)
            {
                notificationTime = 0L;
                return;
            }

            this.updateWindowScale();
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);

            GlStateManager.pushMatrix();

            int count = 0;
            TreeSet<UpdateChecker.ModVersionInfo> updates = UpdateChecker.getModsWithUpdates();
            for(UpdateChecker.ModVersionInfo info : updates)
            {
                if(!notifyAll && !info.isModClientOnly)
                {
                    continue;
                }
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
                int j = (int)(32D * count * (1.0D - d1)) - (int)(d1 * 36.0D);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableTexture2D();
                this.mc.getTextureManager().bindTexture(achievementBg);
                GlStateManager.disableLighting();
                this.drawTexturedModalRect(i, j, 96, 202, 160, 32);

                this.mc.fontRenderer.drawString(I18n.translateToLocal("ichunutil.gui.newUpdate"), i + 10, j + 7, -256);
                this.mc.fontRenderer.drawString(info.modName + " - " + info.modVersionNew, i + 15, j + 18, -1);

                RenderHelper.enableGUIStandardItemLighting();

                count++;
            }

            GlStateManager.popMatrix();

            GlStateManager.disableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
        }
    }
}