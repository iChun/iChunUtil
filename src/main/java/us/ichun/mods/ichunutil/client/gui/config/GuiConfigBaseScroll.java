package us.ichun.mods.ichunutil.client.gui.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import us.ichun.mods.ichunutil.common.core.config.Config;
import us.ichun.mods.ichunutil.common.core.config.ConfigHandler;

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
    protected void elementClicked(int i, boolean flag, int mouseX, int mouseY)
    {
        if (!flag)
        {
            Config oriCfg = config;
            if(config == null)
            {
                Config cfg = ConfigHandler.configs.get(i);
                if(!cfg.categories.isEmpty())
                {
                    mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
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
                if(config.categoriesList.size() > 0)
                {
                    mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
                    FMLClientHandler.instance().showGuiScreen(new GuiConfigSetter(controls, controls.gameSets, config, config.categoriesList.get(i), config.categories.get(config.categoriesList.get(i))));
                }
                else
                {
                    config = oriCfg;
                }
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
    protected void drawSlot(int index, int xPosition, int yPosition, int l, int mouseX, int mouseY)
    {
        int width = 150;
        int height = 20;
        xPosition += 32;
        boolean flag = _mouseX >= xPosition && _mouseY >= yPosition && _mouseX < xPosition + width && _mouseY < yPosition + height;
        int k = (flag ? 2 : 1);

        if(config == null && ConfigHandler.configs.get(index).categoriesList.isEmpty())
        {
            k = 0;
        }

        mc.renderEngine.bindTexture(WIDGITS);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        controls.drawTexturedModalRect(xPosition, yPosition, 0, 46 + k * 20, width / 2, height);
        controls.drawTexturedModalRect(xPosition + width / 2, yPosition, 200 - width / 2, 46 + k * 20, width / 2, height);

        String str = (k == 2 ? EnumChatFormatting.YELLOW : "") + (config != null && config.categoriesList.size() > 0 ? config.categoriesList.get(index) : ConfigHandler.configs.get(index).modName);
        controls.drawCenteredString(mc.fontRendererObj, str, xPosition + (width / 2), yPosition + (height - 8) / 2, k != 0 ? 0xFFFFFFFF : -6250336);
    }

    public boolean keyTyped(char c, int i)
    {
        return true;
    }
}
