package ichun.client.gui.config;

import ichun.core.config.Config;
import ichun.core.config.ConfigHandler;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class GuiConfigBaseScroll extends GuiSlot
{
    protected static final ResourceLocation WIDGITS = new ResourceLocation("textures/gui/widgets.png");
    private GuiConfigBase controls;
    private Minecraft mc;
    private Config config;
    private String[] message;
    private int _mouseX;
    private int _mouseY;
    private int selected = -1;

    public GuiConfigBaseScroll(GuiConfigBase controls, Config cfg, Minecraft mc)
    {
        super(mc, controls.width, controls.height, 16, (controls.height - 32) + 4, 25);
        this.controls = controls;
        this.mc = mc;
        this.config = cfg;
    }

    @Override
    protected int getSize()
    {
        return config != null ? config.categoriesList.size() : ConfigHandler.configs.size();
    }

    @Override
    protected void elementClicked(int i, boolean flag)
    {
        if (!flag)
        {
        	if(config == null)
        	{
        		Config cfg = ConfigHandler.configs.get(i);
        		if(!cfg.categories.isEmpty())
        		{
        			mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
        			FMLClientHandler.instance().showGuiScreen(new GuiConfigBase(controls, controls.gameSets, cfg));
        			return;
        		}
        		else
        		{
        			config = cfg;
        		}
        	}
        	if(config != null)
        	{
        		mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
        		FMLClientHandler.instance().showGuiScreen(new GuiConfigSetter(controls, controls.gameSets, config, config.categoriesList.get(i), config.categories.get(config.categoriesList.get(i))));
        	}
        	//load configs
        }
    }

    @Override
    protected boolean isSelected(int i)
    {
        return false;
    }

    @Override
    protected void drawBackground() {}

    @Override
    public void drawScreen(int mX, int mY, float f)
    {
        _mouseX = mX;
        _mouseY = mY;

        super.drawScreen(mX, mY, f);
    }

    @Override
    protected void drawSlot(int index, int xPosition, int yPosition, int l, Tessellator tessellator)
    {
        int width = 150;
        int height = 20;
        xPosition += 32;
        boolean flag = _mouseX >= xPosition && _mouseY >= yPosition && _mouseX < xPosition + width && _mouseY < yPosition + height;
        int k = (flag ? 2 : 1);

        mc.renderEngine.bindTexture(WIDGITS);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        controls.drawTexturedModalRect(xPosition, yPosition, 0, 46 + k * 20, width / 2, height);
        controls.drawTexturedModalRect(xPosition + width / 2, yPosition, 200 - width / 2, 46 + k * 20, width / 2, height);

        String str = (k == 2 ? EnumChatFormatting.YELLOW : "") + (config != null ? config.categoriesList.get(index) : ConfigHandler.configs.get(index).modName);
        controls.drawCenteredString(mc.fontRenderer, str, xPosition + (width / 2), yPosition + (height - 8) / 2, 0xFFFFFFFF);
    }

    public boolean keyTyped(char c, int i)
    {
        return true;
    }
}
