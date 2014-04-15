package ichun.client.gui.config;

import ichun.common.core.config.Config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.common.config.Property;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiConfigSetter extends GuiControls 
{
	
	private GuiConfigSetterScroll scrollPane;
	
	public GameSettings gameSets;
	
	private Config config;
	
	public ArrayList<Property> properties;
    private GuiButton selectedBtn;

    public GuiConfigSetter(GuiScreen parentScreen, GameSettings gameSettings, Config cfg, String title, ArrayList<Property> cat)
	{
		super(parentScreen, gameSettings);
		gameSets = gameSettings;
		config = cfg;
        field_146495_a = cfg.modName + " - " + title;
		properties = cat;
	}
	
	@Override
    public void initGui()
    {
    	this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height - 28, I18n.format("gui.done")));
        
        scrollPane = new GuiConfigSetterScroll(this, config, properties, mc);
        scrollPane.registerScrollButtons(7, 8);
    	
    }
	
	@Override
    public void updateScreen()
    {
        scrollPane.tick();
    }
	
	@Override
	public void onGuiClosed() 
	{
		scrollPane.keyTyped((char) 42, Keyboard.KEY_RETURN);
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

    public void drawTooltip(List par1List, int par2, int par3)
    {
        if (!par1List.isEmpty())
        {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int k = 0;
            
        	boolean shouldBreak = false;
            while(!shouldBreak)
            {
            	for(int i = 0; i < par1List.size(); i++)
            	{
            		String s = (String)par1List.get(i);
            		if(s.length() > 17)
            		{
            			par1List.remove(i);
            			String temp = s.substring(0, 17);
            			int lastIndex = temp.lastIndexOf(" ");
            			if(lastIndex == -1)
            			{
            				lastIndex = 17;
            			}
            			else
            			{
            				lastIndex++;
            			}
            			par1List.add(i, s.substring(0, lastIndex));
            			par1List.add(i + 1, s.substring(lastIndex, s.length()));
            			break;
            		}
            		if(i == par1List.size() - 1)
            		{
            			shouldBreak = true;
            		}
            	}
            }
            
            Iterator iterator = par1List.iterator();

            while (iterator.hasNext())
            {
                String s = (String)iterator.next();
                int l = this.fontRendererObj.getStringWidth(s);

                if (l > k)
                {
                    k = l;
                }
            }
            
            int i1 = par2 + 12;
            int j1 = par3 - 12;
            int k1 = 8;

            if (par1List.size() > 1)
            {
                k1 += 2 + (par1List.size() - 1) * 10;
            }

            if (i1 + k > this.width)
            {
                i1 -= 28 + k;
            }

            if (j1 + k1 + 6 > this.height)
            {
                j1 = this.height - k1 - 6;
            }

            this.zLevel = 300.0F;
            int l1 = -267386864;
            this.drawGradientRect(i1 - 3, j1 - 4, i1 + k + 3, j1 - 3, l1, l1);
            this.drawGradientRect(i1 - 3, j1 + k1 + 3, i1 + k + 3, j1 + k1 + 4, l1, l1);
            this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 + k1 + 3, l1, l1);
            this.drawGradientRect(i1 - 4, j1 - 3, i1 - 3, j1 + k1 + 3, l1, l1);
            this.drawGradientRect(i1 + k + 3, j1 - 3, i1 + k + 4, j1 + k1 + 3, l1, l1);
            int i2 = 1347420415;
            int j2 = (i2 & 16711422) >> 1 | i2 & -16777216;
            this.drawGradientRect(i1 - 3, j1 - 3 + 1, i1 - 3 + 1, j1 + k1 + 3 - 1, i2, j2);
            this.drawGradientRect(i1 + k + 2, j1 - 3 + 1, i1 + k + 3, j1 + k1 + 3 - 1, i2, j2);
            this.drawGradientRect(i1 - 3, j1 - 3, i1 + k + 3, j1 - 3 + 1, i2, i2);
            this.drawGradientRect(i1 - 3, j1 + k1 + 2, i1 + k + 3, j1 + k1 + 3, j2, j2);

            for (int k2 = 0; k2 < par1List.size(); ++k2)
            {
                String s1 = (String)par1List.get(k2);
                this.fontRendererObj.drawStringWithShadow(s1, i1, j1, -1);

                if (k2 == 0)
                {
                    j1 += 2;
                }

                j1 += 10;
            }

            this.zLevel = 0.0F;
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
    }

}
