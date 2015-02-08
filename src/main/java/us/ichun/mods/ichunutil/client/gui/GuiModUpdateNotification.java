package us.ichun.mods.ichunutil.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import us.ichun.mods.ichunutil.common.core.updateChecker.ModVersionChecker;
import us.ichun.mods.ichunutil.common.iChunUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class GuiModUpdateNotification extends Gui
{
    private static final ResourceLocation rlAchievement = new ResourceLocation("textures/gui/achievement/achievement_background.png");
    private Minecraft mc;
    private int width;
    private int height;
    private String topText;
    private long achiTime;
    public ArrayList<String> modUpdatesPending = new ArrayList<String>();
    public ArrayList<String> modUpdates = new ArrayList<String>();
    public ArrayList<String> modUpdatesDone = new ArrayList<String>();

    public boolean shouldRender;
    public boolean pending;

    public GuiModUpdateNotification(Minecraft par1Minecraft)
    {
        this.mc = par1Minecraft;
        this.shouldRender = false;
        this.topText = StatCollector.translateToLocal("ichun.gui.newUpdate");
    }

    public void addModUpdate(String modName, String version)
    {
        boolean render = shouldRender;

        if(iChunUtil.config.versionNotificationFrequency == 3)
        {
            iChunUtil.console("[NEW UPDATE AVAILABLE] " + modName + " - " + version);
            return;
        }
        else if(iChunUtil.config.versionNotificationFrequency == 2)
        {
            for(Map.Entry<String, String> e : iChunUtil.proxy.versionChecker.entrySet())
            {
                String oldVer = iChunUtil.proxy.prevVerChecker.get(e.getKey());
                if(oldVer != null && oldVer.equals(e.getValue()))
                {
                    return;
                }
            }
        }
        else
        {
            shouldRender = iChunUtil.config.versionNotificationFrequency == 0 || iChunUtil.config.versionNotificationFrequency == 1 && ModVersionChecker.differentDay;
        }

        if(!modUpdatesPending.contains(modName + " - " + version))
        {
            pending = true;

            modUpdatesPending.add(modName + " - " + version);
            while((height == 0 || modUpdates.size() * 32D < height) && !modUpdatesPending.isEmpty())
            {
                modUpdates.add(modUpdatesPending.get(0));
                modUpdatesPending.remove(0);

                if(render)
                {
                    achiTime = Minecraft.getSystemTime() - 250L;
                }
                else
                {
                    achiTime = Minecraft.getSystemTime();
                }
                Collections.sort(modUpdates);
            }
        }
    }

    private void updateWindowScale()
    {
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        this.width = this.mc.displayWidth;
        this.height = this.mc.displayHeight;
        ScaledResolution scaledresolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        this.width = scaledresolution.getScaledWidth();
        this.height = scaledresolution.getScaledHeight();
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, (double)this.width, (double)this.height, 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
    }

    public void update()
    {
        if(pending)
        {
            pending = false;
            achiTime = Minecraft.getSystemTime() + 5000L;
        }
        if (shouldRender && this.achiTime != 0L && Minecraft.getMinecraft().thePlayer != null)
        {
            for(int k = 0; k < modUpdates.size(); k++)
            {
                double d0 = (double)(Minecraft.getSystemTime() - this.achiTime) / (3000.0D + 500D * (modUpdates.size() - 1));

                if((d0 < -5.0D || d0 > 1.0D) && k == modUpdates.size() - 1)
                {
                    shouldRender = false;
                    this.achiTime = 0L;
                    for(int l = 0; l < modUpdates.size(); l++)
                    {
                        String s = modUpdates.get(l);
                        iChunUtil.console("[NEW UPDATE AVAILABLE] " + s);
                    }
                    modUpdatesDone.addAll(modUpdates);
                    modUpdates.clear();

                    if(modUpdatesPending.isEmpty())
                    {
                        ModVersionChecker.differentDay = false;
                    }

                    while(modUpdates.size() * 32D < height && !modUpdatesPending.isEmpty())
                    {
                        modUpdates.add(modUpdatesPending.get(0));
                        modUpdatesPending.remove(0);
                        achiTime = Minecraft.getSystemTime();
                        Collections.sort(modUpdates);
                    }
                    shouldRender = true;

                    return;
                }

                this.updateWindowScale();
                GlStateManager.disableDepth();
                GlStateManager.depthMask(false);
                double d1 = d0 * 2.0D;

                if(d1 > 1.0D)
                {
                    d1 = 2.0D - d1;
                }

                d1 *= 4.0D;
                d1 = 1.0D - d1;

                if(d1 < 0.0D)
                {
                    d1 = 0.0D;
                }

                d1 *= d1;
                d1 *= d1;
                int i = 0;
                int j = (int)(32D * k * (1.0D - d1)) - (int)(d1 * 36.0D);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableTexture2D();
                this.mc.getTextureManager().bindTexture(rlAchievement);
                GlStateManager.disableLighting();
                this.drawTexturedModalRect(i, j, 96, 202, 160, 32);

                this.mc.fontRendererObj.drawString(this.topText, i + 10, j + 7, -256);
                this.mc.fontRendererObj.drawString(modUpdates.get(k), i + 15, j + 18, -1);

                RenderHelper.enableGUIStandardItemLighting();
                GlStateManager.disableLighting();
                GlStateManager.enableRescaleNormal();
                GlStateManager.enableColorMaterial();
                GlStateManager.depthMask(true);
                GlStateManager.enableDepth();
            }
        }
    }

    public void clearModUpdates()
    {
        shouldRender = false;
        ModVersionChecker.clearListOfNonSidedMods(modUpdates);
        ModVersionChecker.clearListOfNonSidedMods(modUpdatesPending);
        modUpdatesDone.clear();
    }
}