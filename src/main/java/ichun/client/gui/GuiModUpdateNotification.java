package ichun.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@SideOnly(Side.CLIENT)
public class GuiModUpdateNotification extends Gui
{
    private static final ResourceLocation rlAchievement = new ResourceLocation("textures/gui/achievement/achievement_background.png");
    private Minecraft mc;
    private int width;
    private int height;
    private String topText;
    private long achiTime;
    private ArrayList<String> modUpdatesPending = new ArrayList<String>();
    private ArrayList<String> modUpdates = new ArrayList<String>();

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
        pending = true;
        modUpdatesPending.add(modName + " | " + version);
        while((height == 0 || modUpdates.size() * 32D < height) && !modUpdatesPending.isEmpty())
        {
            modUpdates.add(modUpdatesPending.get(0));
            modUpdatesPending.remove(0);

            if(shouldRender)
            {
                achiTime = Minecraft.getSystemTime() - 250L;
            }
            else
            {
                achiTime = Minecraft.getSystemTime();
            }
            Collections.sort(modUpdates);
        }
        shouldRender = true;
    }

    private void updateWindowScale()
    {
        GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        this.width = this.mc.displayWidth;
        this.height = this.mc.displayHeight;
        ScaledResolution scaledresolution = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
        this.width = scaledresolution.getScaledWidth();
        this.height = scaledresolution.getScaledHeight();
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, (double)this.width, (double)this.height, 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
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

                if(d0 < -5.0D || d0 > 1.0D)
                {
                    shouldRender = false;
                    this.achiTime = 0L;
                    modUpdates.clear();

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
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glDepthMask(false);
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
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                this.mc.getTextureManager().bindTexture(rlAchievement);
                GL11.glDisable(GL11.GL_LIGHTING);
                this.drawTexturedModalRect(i, j, 96, 202, 160, 32);

                this.mc.fontRenderer.drawString(this.topText, i + 10, j + 7, -256);
                this.mc.fontRenderer.drawString(modUpdates.get(k), i + 15, j + 18, -1);

                RenderHelper.enableGUIStandardItemLighting();
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                GL11.glEnable(GL11.GL_COLOR_MATERIAL);
                GL11.glDepthMask(true);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }
        }
    }
}