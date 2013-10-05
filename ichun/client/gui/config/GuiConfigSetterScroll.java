package ichun.client.gui.config;

import ichun.core.config.Config;
import ichun.core.config.ConfigHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Property;
import net.minecraftforge.common.Property.Type;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;

public class GuiConfigSetterScroll extends GuiSlot
{
    protected static final ResourceLocation WIDGITS = new ResourceLocation("textures/gui/widgets.png");
    private GuiConfigSetter controls;
    private Minecraft mc;
    private Config config;
    private ArrayList<Property> properties;
    private ArrayList<String> propNames;
    private String[] message;
    private int _mouseX;
    private int _mouseY;
    private int selected = -1;
    private String selectedText;
	public byte blinker;
	private GuiTextField textDummy;
	private boolean needRestart;

    public GuiConfigSetterScroll(GuiConfigSetter guiConfigSetter, Config cfg, ArrayList<Property> props, Minecraft mc)
    {
        super(mc, guiConfigSetter.width, guiConfigSetter.height, 16, (guiConfigSetter.height - 32) + 4, 25);
        this.controls = guiConfigSetter;
        this.mc = mc;
        this.config = cfg;
        this.properties = props;
        blinker = 0;
        textDummy = new GuiTextField(Minecraft.getMinecraft().fontRenderer, controls.width, controls.height, 150, 20);
        textDummy.setVisible(false);
        textDummy.setMaxStringLength(7);
        
        propNames = new ArrayList<String>();
        
        for(Property p : properties)
        {
        	propNames.add(config.propName.get(p));
        }
        Collections.sort(propNames);
        
        needRestart = false;
    }

    @Override
    protected int getSize()
    {
        return properties.size();
    }

    @Override
    protected void elementClicked(int i, boolean flag)
    {
        if (!flag)
        {
        	if(selected != -1)
        	{
        		Property prop = config.props.get(config.propNameToProp.get(propNames.get(selected)));
        		updateProperty(prop, selectedText);
        	}
        	
        	Property prop = config.props.get(config.propNameToProp.get(propNames.get(i)));
    		selected = i;
    		selectedText = (prop.getType() == Type.INTEGER ? Integer.toString(prop.getInt()) : prop.getString());
    		textDummy.setText(selectedText);
    		textDummy.setCursorPositionEnd();
        }
    }
    
    public boolean isValidValue(Property prop, String s)
    {
    	if(prop.getType() == Type.INTEGER)
    	{
    		try
    		{
    			int i = Integer.parseInt(s);
    			int[] minmax = config.minmax.get(prop);
    			if(!(i >= minmax[0] && i <= minmax[1]))
    			{
    				return false;
    			}
    			return true;
    		}
    		catch(NumberFormatException e)
    		{
    			return false;
    		}
    	}
    	else if(prop.getType() == Type.STRING)
    	{
    		return true;
    	}
    	return false;
    }
    
    public boolean updateProperty(Property prop, String s)
    {
    	if(prop.getType() == Type.INTEGER)
    	{
    		try
    		{
    			int i = Integer.parseInt(s);
    			
    			int _default = prop.getInt();
    			
    			int[] minmax = config.minmax.get(prop);
    			if(!(i >= minmax[0] && i <= minmax[1]))
    			{
    				return false;
    			}

    			prop.set(i);
    			if(config.parent.onConfigChange(config, prop))
    			{
    				config.config.save();
    				
	    			if(config.propNeedsRestart.contains(prop))
	    			{
	    				needRestart = true;
	    			}
    			}
    			else
    			{
    				prop.set(_default);
    				return false;
    			}
    			return true;
    		}
    		catch(NumberFormatException e)
    		{
    			return false;
    		}
    	}
    	else if(prop.getType() == Type.STRING)
    	{
    		prop.set(s);
    		
    		String _default = prop.getString();
    		
    		if(config.parent.onConfigChange(config, prop))
    		{
    			config.config.save();

    			if(config.propNeedsRestart.contains(prop))
				{
					needRestart = true;
				}
    		}
    		else
    		{
    			prop.set(_default);
    			return false;
    		}
    		return true;
    	}
    	return false;
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
    protected int getScrollBarX()
    {
        return controls.width / 2 + 102;
    }

    @Override
    protected void drawSlot(int index, int xPosition, int yPosition, int l, Tessellator tessellator)
    {
        int width = 200;
        int height = 20;
        xPosition += 8;
        boolean flag = _mouseX >= xPosition && _mouseY >= yPosition && _mouseX < xPosition + width && _mouseY < yPosition + height;
        int k = (flag ? 2 : 1);

        mc.renderEngine.bindTexture(WIDGITS);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawRect(xPosition + width - 50, yPosition, xPosition + width, yPosition + height, -6250336);
        Gui.drawRect(xPosition + width - 50 + 1, yPosition + 1, xPosition + width - 1, yPosition + height - 1, -16777216);

        Property prop = config.props.get(config.propNameToProp.get(propNames.get(index)));
        
        String str = (k == 2 ? EnumChatFormatting.YELLOW : "") + (propNames.get(index));
        controls.drawString(mc.fontRenderer, str, xPosition, yPosition + (height - 8) / 2, 0xFFFFFFFF);
        
        String value = (selected == index ? isValidValue(prop, selectedText) ? EnumChatFormatting.YELLOW : EnumChatFormatting.RED : "") + (selected == index ? selectedText : (prop.getType() == Type.INTEGER ? Integer.toString(prop.getInt()) : prop.getString())) + (selected == index && blinker > 8 ? "_" : "");
        
        controls.drawString(mc.fontRenderer, value, xPosition + width - 47, yPosition + (height - 8) / 2, 0xFFFFFFFF);
        
        if(k == 2 && prop.comment != null)
        {
	        ArrayList<String> tooltip = new ArrayList<String>();
	        String[] comments = prop.comment.split("\n");
	        for(int i = 0; i < comments.length; i++)
	        {
	        	tooltip.add(comments[i]);
	        }
	        controls.drawTooltip(tooltip, xPosition + width, 35);
        }
        
        if(needRestart)
        {
        	controls.drawString(mc.fontRenderer, EnumChatFormatting.RED + "Some changes", 4, height, 0xFFFFFFFF);
        	controls.drawString(mc.fontRenderer, EnumChatFormatting.RED + "require a client", 4, height + 12, 0xFFFFFFFF);
        	controls.drawString(mc.fontRenderer, EnumChatFormatting.RED + "restart.", 4, height + 26, 0xFFFFFFFF);
        	controls.drawString(mc.fontRenderer, EnumChatFormatting.RED + "Some changes", 4, height + 54, 0xFFFFFFFF);
        	controls.drawString(mc.fontRenderer, EnumChatFormatting.RED + "may not take", 4, height + 68, 0xFFFFFFFF);
        	controls.drawString(mc.fontRenderer, EnumChatFormatting.RED + "effect if they", 4, height + 82, 0xFFFFFFFF);
        	controls.drawString(mc.fontRenderer, EnumChatFormatting.RED + "are serverside", 4, height + 96, 0xFFFFFFFF);
        }
    }

    public boolean keyTyped(char c, int i)
    {
    	if(selected != -1)
    	{
    		if(i == Keyboard.KEY_RETURN)
    		{
           		Property prop = config.props.get(config.propNameToProp.get(propNames.get(selected)));
        		updateProperty(prop, selectedText);
       			selected = -1;
    		}
    		else
    		{
    			textDummy.setCursorPositionEnd();
    			textDummy.setEnabled(true);
    			textDummy.setFocused(true);
    			textDummy.textboxKeyTyped(c, i);
    			textDummy.setEnabled(false);
    			textDummy.setFocused(false);
    			textDummy.setCursorPositionEnd();
    			selectedText = textDummy.getText();
    		}
    	}
        return true;
    }
}
