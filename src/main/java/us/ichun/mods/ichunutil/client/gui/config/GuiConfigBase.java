package us.ichun.mods.ichunutil.client.gui.config;

import us.ichun.mods.ichunutil.common.core.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiKeyBindingList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

import java.io.IOException;

public class GuiConfigBase extends GuiControls
{
	
	private GuiConfigBaseScroll scrollPane;
	
	public GameSettings gameSets;
	
	private Config config;
    private GuiButton selectedBtn;

    public GuiConfigBase(GuiScreen parentScreen, GameSettings gameSettings, Config cfg)
	{
		super(parentScreen, gameSettings);
		gameSets = gameSettings;
		config = cfg;
        keyBindingList = new GuiKeyBindingList(this, Minecraft.getMinecraft());
	}
	
	@Override
    public void initGui()
    {
        screenTitle = config != null ? config.modName : "Other Options";

    	this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height - 28, I18n.format("gui.done")));
        
        scrollPane = new GuiConfigBaseScroll(this, config, mc);
        scrollPane.registerScrollButtons(7, 8);
    	
    }
    
	@Override
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        scrollPane.drawScreen(par1, par2, par3);
        drawCenteredString(fontRendererObj, screenTitle, width / 2, 4, 0xffffff);

        for (int k = 0; k < this.buttonList.size(); ++k)
        {
            GuiButton guibutton = (GuiButton)this.buttonList.get(k);
            guibutton.drawButton(this.mc, par1, par2);
        }
    }

	@Override
    protected void keyTyped(char par1, int par2)
    {
        if (scrollPane.keyTyped(par1, par2))
        {
            if (par2 == 1)
            {
                this.mc.displayGuiScreen((GuiScreen)null);
                this.mc.setIngameFocus();
            }

        }
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        scrollPane.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3)
    {
        if (par3 == 0)
        {
            for (int l = 0; l < this.buttonList.size(); ++l)
            {
                GuiButton guibutton = (GuiButton)this.buttonList.get(l);

                if (guibutton.mousePressed(this.mc, par1, par2))
                {
                    this.selectedBtn = guibutton;
                    guibutton.playPressSound(this.mc.getSoundHandler());
                    try
                    {
                        this.actionPerformed(guibutton);
                    }
                    catch(IOException ignored){};
                }
            }
        }
    }

    @Override
    protected void mouseReleased(int p_146286_1_, int p_146286_2_, int p_146286_3_)
    {
        if (this.selectedBtn != null && p_146286_3_ == 0)
        {
            this.selectedBtn.mouseReleased(p_146286_1_, p_146286_2_);
            this.selectedBtn = null;
        }
    }

}
