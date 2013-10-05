package ichun.client.gui.config;

import ichun.core.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.client.GuiControlsScrollPanel;

public class GuiConfigBase extends GuiControls 
{
	
	private GuiConfigBaseScroll scrollPane;
	
	public GameSettings gameSets;
	
	private Config config;
	
	public GuiConfigBase(GuiScreen parentScreen, GameSettings gameSettings, Config cfg) 
	{
		super(parentScreen, gameSettings);
		gameSets = gameSettings;
		config = cfg;
	}
	
	@Override
    public void initGui()
    {
    	screenTitle = config != null ? config.modName : "Other Options";

    	this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height - 28, I18n.getString("gui.done")));
        
        scrollPane = new GuiConfigBaseScroll(this, config, mc);
        scrollPane.registerScrollButtons(7, 8);
    	
    }
    
	@Override
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        scrollPane.drawScreen(par1, par2, par3);
        drawCenteredString(fontRenderer, screenTitle, width / 2, 4, 0xffffff);

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
}
