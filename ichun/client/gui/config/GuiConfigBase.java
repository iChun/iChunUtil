package ichun.client.gui.config;

import ichun.core.config.Config;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

//TODO make sure this GUI still works properly.
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
	}
	
	@Override
    public void initGui()
    {
    	field_146495_a = config != null ? config.modName : "Other Options";

    	this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height - 28, I18n.format("gui.done", new Object[0])));
        
        scrollPane = new GuiConfigBaseScroll(this, config, mc);
        scrollPane.registerScrollButtons(7, 8);
    	
    }
    
	@Override
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        scrollPane.drawScreen(par1, par2, par3);
        drawCenteredString(fontRendererObj, field_146495_a, width / 2, 4, 0xffffff);

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
                    guibutton.func_146113_a(this.mc.getSoundHandler());
                    this.actionPerformed(guibutton);
                }
            }
        }
    }

    @Override
    protected void mouseMovedOrUp(int p_146286_1_, int p_146286_2_, int p_146286_3_)
    {
        if (this.selectedBtn != null && p_146286_3_ == 0)
        {
            this.selectedBtn.mouseReleased(p_146286_1_, p_146286_2_);
            this.selectedBtn = null;
        }
    }

}
