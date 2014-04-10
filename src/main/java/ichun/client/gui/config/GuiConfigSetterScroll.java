package ichun.client.gui.config;

import ichun.common.core.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.*;

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
    private int selectedIntArrayProp = -1;
    private int intArraySlots = 0;
    private String selectedText;
	public byte blinker;
	public GuiTextField textDummy;
	private boolean needRestart;
	private ArrayList<Integer> intArrayList = new ArrayList<Integer>();
	private LinkedHashMap<Integer, ArrayList<Integer>> nestedIntArrayList = new LinkedHashMap<Integer, ArrayList<Integer>>();

    public GuiConfigSetterScroll(GuiConfigSetter guiConfigSetter, Config cfg, ArrayList<Property> props, Minecraft mc)
    {
        super(mc, guiConfigSetter.width, guiConfigSetter.height, 16, (guiConfigSetter.height - 32) + 4, 25);
        this.controls = guiConfigSetter;
        this.mc = mc;
        this.config = cfg;
        this.properties = props;
        blinker = 0;
        selectedText = "";
        textDummy = new GuiTextField(Minecraft.getMinecraft().fontRenderer, 3, 6, 45, 20);
        textDummy.setEnableBackgroundDrawing(false);
        textDummy.setMaxStringLength(80);

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
        return selectedIntArrayProp != -1 ? properties.size() + intArraySlots : properties.size();
    }

    @Override
    protected void elementClicked(int i, boolean flag, int mouseX, int mouseY)
    {
        if (!flag)
        {
        	if(selected != -1)
        	{
                boolean selectedIntArraySlot = selectedIntArrayProp != -1 && selected > selectedIntArrayProp && selected <= selectedIntArrayProp + intArraySlots;
                Property prop;
                if(selectedIntArraySlot)
                {
                	prop = config.props.get(config.propNameToProp.get(propNames.get(selectedIntArrayProp)));
                }
                else
                {
                	prop = config.props.get(config.propNameToProp.get(propNames.get(selectedIntArrayProp != -1 && selected > selectedIntArrayProp ? selected - intArraySlots : selected)));
                }
                
           		if(isValidValue(prop, selectedText))
           		{
           			updateProperty(prop, selectedText);
           		}
        	}

    		selected = i;
    		
    		boolean isIntArraySlot = selectedIntArrayProp != -1 && i > selectedIntArrayProp && i <= selectedIntArrayProp + intArraySlots;
        	
    		if(isIntArraySlot)
    		{
    			selectedText = "";
    		}
    		else
    		{
    			intArrayList.clear();
    			nestedIntArrayList.clear();
    			
            	Property prop = config.props.get(config.propNameToProp.get(propNames.get(selectedIntArrayProp != -1  && i > selectedIntArrayProp ? i - intArraySlots : i)));
            	
            	if(config.nestedIntArrayList.contains(prop) || config.intArrayList.contains(prop))
            	{
            		if((selectedIntArrayProp != -1 && i > selectedIntArrayProp ? i - intArraySlots : i) > selectedIntArrayProp)
            		{
            			selected = selectedIntArrayProp = i - intArraySlots;
            		}
            		else
            		{
            			selected = selectedIntArrayProp = i;
            		}
            		
            		String value = prop.getString();
            		
            		if(config.nestedIntArrayList.contains(prop))
            		{
            			nestedIntArrayList = config.parseNestedIntArray(value);
            			intArraySlots = nestedIntArrayList.size() + 1;
            		}
            		else
            		{
            			intArrayList = config.parseIntArray(value);
            			intArraySlots = 1;
            		}
            	}
            	else
            	{
            		selected = selectedIntArrayProp != -1  && i > selectedIntArrayProp ? i - intArraySlots : i;
            		intArraySlots = 0;
            		selectedIntArrayProp = -1;
            		selectedText = (prop.getType() == Type.INTEGER ? Integer.toString(prop.getInt()) : prop.getString());    			
            	}
    		}
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
    		if(!config.nestedIntArrayList.contains(prop) && !config.intArrayList.contains(prop))
    		{
    			return true;    			
    		}
    		else
    		{
    			if(config.intArrayList.contains(prop))
    			{
    	    		try
    	    		{
    	    			int i = Integer.parseInt(s);
    	    			int[] minmax = config.minmax.get(prop);
    	    			if(!(i >= minmax[0] && i <= minmax[1]))
    	    			{
    	    				return false;
    	    			}
    	    			if(intArrayList.contains(Integer.parseInt(s)))
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
    			else
    			{
    				if(selected == selectedIntArrayProp + intArraySlots)
    				{
        	    		try
        	    		{
        	    			int i = Integer.parseInt(s);
        	    			int[] minmax = config.minmax.get(prop);
        	    			if(!(i >= minmax[0] && i <= minmax[1]))
        	    			{
        	    				return false;
        	    			}
        	    			if(nestedIntArrayList.containsKey(Integer.parseInt(s)))
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
    				else
    				{
        	    		try
        	    		{
        	    			int i = Integer.parseInt(s);
        	    			int[] minmax = config.nestedMinmax.get(prop);
        	    			if(!(i >= minmax[0] && i <= minmax[1]))
        	    			{
        	    				return false;
        	    			}
        	    			
        		        	int m = 0;
        		        	Iterator<Map.Entry<Integer, ArrayList<Integer>>> ite = nestedIntArrayList.entrySet().iterator();
        		        	while(ite.hasNext())
        		        	{
        		        		Map.Entry<Integer, ArrayList<Integer>> e = ite.next();
        		        		m++;
        		        		if(selected - selectedIntArrayProp == m)
        		        		{
        		        			if(e.getValue().contains(Integer.parseInt(s)))
        		        			{
        		        				return false;
        		        			}
        		        			break;
        		        		}
        		        	}

        	    			return true;
        	    		}
        	    		catch(NumberFormatException e)
        	    		{
        	    			return false;
        	    		}
    				}
    			}
    		}
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
    		String _default = prop.getString();
    		
    		if(config.intArrayList.contains(prop))
    		{
    			StringBuilder sb = new StringBuilder();
    			
	        	for(int i = 0; i < intArrayList.size(); i++)
	        	{
	        		sb.append(intArrayList.get(i));
	        		if(i < intArrayList.size() - 1)
	        		{
	        			sb.append(", ");
	        		}
	        	}
	        	prop.set(sb.toString());
    		}
    		else if(config.nestedIntArrayList.contains(prop))
    		{
    			StringBuilder sb = new StringBuilder();
	        	int i = 0;
	        	for(Map.Entry<Integer, ArrayList<Integer>> e : nestedIntArrayList.entrySet())
	        	{
        			sb.append(e.getKey());
        			for(int m = 0; m < e.getValue().size(); m++)
        			{
        				if(m == 0)
        				{
    	        			sb.append(": ");
        				}
    	        		sb.append(e.getValue().get(m));
    	        		if(m < e.getValue().size() - 1)
    	        		{
    	        			sb.append(": ");
    	        		}
        			}
        			if(i < nestedIntArrayList.size() - 1 || e.getValue().isEmpty() && i < nestedIntArrayList.size() - 1)
        			{
        				sb.append(", ");
        			}
        			i++;
	        	}
	        	prop.set(sb.toString());
    		}
    		else
    		{
    			prop.set(s);
    		}
    		
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
    protected void drawSlot(int index, int xPosition, int yPosition, int l, Tessellator tessellator, int mouseX, int mouseY)
    {
    	if(index >= getSize())
    	{
    		return;
    	}
        //If you're reading this, I know this GUI is bad and I should feel bad.. I'm sorry :( -iChun
        String textDummyString = textDummy.getText();
        int textDummyCursorPosition = textDummy.getCursorPosition();

        int width = 200;
        int height = 20;
        xPosition += 8;
        boolean flag = _mouseX >= xPosition && _mouseY >= yPosition && _mouseX < xPosition + width && _mouseY < yPosition + height;
        int k = (flag ? 2 : 1);

        mc.renderEngine.bindTexture(WIDGITS);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        boolean isIntArraySlot = selectedIntArrayProp != -1 && index > selectedIntArrayProp && index <= selectedIntArrayProp + intArraySlots;
        if(isIntArraySlot)
        {
        	//draw Int Array Slot
	        Gui.drawRect(xPosition + width - 45, yPosition, xPosition + width - 5, yPosition + height, -6250336);
	        Gui.drawRect(xPosition + width - 45 + 1, yPosition + 1, xPosition + width - 5 - 1, yPosition + height - 1, -16777216);
	        
	        Property prop = config.props.get(config.propNameToProp.get(propNames.get(selectedIntArrayProp)));
	        
	        StringBuilder sb = new StringBuilder("> ");
	        if(config.intArrayList.contains(prop))
	        {
	        	for(int i = 0; i < intArrayList.size(); i++)
	        	{
	        		sb.append(intArrayList.get(i));
	        		if(i < intArrayList.size() - 1)
	        		{
	        			sb.append(", ");
	        		}
	        	}
	        }
	        else
	        {
	        	int i = 0;
	        	for(Map.Entry<Integer, ArrayList<Integer>> e : nestedIntArrayList.entrySet())
	        	{
	        		i++;
	        		if(index - selectedIntArrayProp == i)
	        		{
	        			sb.append(e.getKey());
	        			for(int m = 0; m < e.getValue().size(); m++)
	        			{
	        				if(m == 0)
	        				{
	    	        			sb.append(": ");
	        				}
	    	        		sb.append(e.getValue().get(m));
	    	        		if(m < e.getValue().size() - 1)
	    	        		{
	    	        			sb.append(": ");
	    	        		}
	        			}
	        			break;
	        		}
	        	}
	        }
	        controls.drawString(mc.fontRenderer, sb.toString(), xPosition + 5, yPosition + (height - 8) / 2, k == 2 ? 14737632 : -6250336);
	        
	        if(selected == index)
	        {
	        	String value = selectedText + (blinker > 8 ? "_" : "");

                textDummy.setEnabled(true);
                textDummy.setText(value);
                if(selected == index)
                {
                    if(selectedText.equals("") || isValidValue(prop, selectedText))
                    {
                        textDummy.setTextColor(16777045);
                    }
                    else
                    {
                        textDummy.setTextColor(16733525);
                    }
                }
                else
                {
                    textDummy.setTextColor(16777215);
                    textDummy.setCursorPosition(0);
                }

                GL11.glPushMatrix();
                GL11.glTranslatef(xPosition + width - 45, yPosition, 0F);
                textDummy.drawTextBox();

                textDummy.setEnabled(false);

                GL11.glPopMatrix();

//                controls.drawString(mc.fontRenderer, value, xPosition + width, yPosition + (height - 8) / 2, 0xFFFFFFFF);
	        }
        }
        else
        {
        	Property prop = config.props.get(config.propNameToProp.get(propNames.get(selectedIntArrayProp != -1 && index > selectedIntArrayProp ? index - intArraySlots : index)));
        
            if(!config.intArrayList.contains(prop) && !config.nestedIntArrayList.contains(prop))
            {
                //draw fake text box
    	        Gui.drawRect(xPosition + width - 50, yPosition, xPosition + width, yPosition + height, -6250336);
    	        Gui.drawRect(xPosition + width - 50 + 1, yPosition + 1, xPosition + width - 1, yPosition + height - 1, -16777216);
    	        
    	        String value = (selected == index ? selectedText : (prop.getType() == Type.INTEGER ? Integer.toString(prop.getInt()) : prop.getString())) + (selected == index && blinker > 8 ? "_" : " ");
//                String value = (selected == index ? isValidValue(prop, selectedText) ? EnumChatFormatting.YELLOW : EnumChatFormatting.RED : EnumChatFormatting.WHITE) + (selected == index ? selectedText : (prop.getType() == Type.INTEGER ? Integer.toString(prop.getInt()) : prop.getString())) + (selected == index && blinker > 8 ? "_" : "");

                textDummy.setEnabled(true);
                textDummy.setText(value);
                if(selected == index)
                {
                    if(isValidValue(prop, selectedText))
                    {
                        textDummy.setTextColor(16777045);
                    }
                    else
                    {
                        textDummy.setTextColor(16733525);
                    }
                }
                else
                {
                    textDummy.setTextColor(16777215);
                    textDummy.setCursorPosition(0);
                }

                GL11.glPushMatrix();
                GL11.glTranslatef(xPosition + width - 50, yPosition, 0F);
                textDummy.drawTextBox();

                textDummy.setEnabled(false);

                GL11.glPopMatrix();
//    	        controls.drawString(mc.fontRenderer, value, xPosition + width, yPosition + (height - 8) / 2, 0xFFFFFFFF);
            }
            else
            {
            	//else, if int arrays.
                int clr = 14737632;

                int kk = k;
                
                if (selectedIntArrayProp == index)
                {
                    clr = -6250336;
                    kk = 0;
                }
                else if (k == 2)
                {
                    clr = 16777120;
                }
                
            	controls.drawTexturedModalRect(xPosition + width - 50, yPosition, 0, 46 + kk * 20, 25, height);
            	controls.drawTexturedModalRect(xPosition + width - 25, yPosition, 200 - 25, 46 + kk * 20, 25, height);

                controls.drawCenteredString(mc.fontRenderer, selectedIntArrayProp == index ? "v" : ">>", xPosition + width - 25, yPosition + (height - 8) / 2, clr);
            }
            
            String str = (k == 2 ? EnumChatFormatting.YELLOW : "") + (propNames.get(selectedIntArrayProp != -1 && index > selectedIntArrayProp ? index - intArraySlots : index));
            controls.drawString(mc.fontRenderer, str, xPosition, yPosition + (height - 8) / 2, 0xFFFFFFFF);
                        
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
        }

        textDummy.setText(textDummyString);
        textDummy.setCursorPosition(textDummyCursorPosition);
        
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
    			boolean isIntArraySlot = selectedIntArrayProp != -1 && selected > selectedIntArrayProp && selected <= selectedIntArrayProp + intArraySlots;
    			
    			if(isIntArraySlot)
    			{
    				Property prop = config.props.get(config.propNameToProp.get(propNames.get(selectedIntArrayProp)));
	           		if(isValidValue(prop, selectedText))
	           		{
	    		        if(config.intArrayList.contains(prop))
	    		        {
	    		        	intArrayList.add(Integer.parseInt(selectedText));
	    		        }
	    		        else
	    		        {
	    		        	if(selected == selectedIntArrayProp + intArraySlots)
	    		        	{
	    		        		nestedIntArrayList.put(Integer.parseInt(selectedText), new ArrayList<Integer>());
	    		        		intArraySlots++;
	    		        	}
	    		        	else
	    		        	{
	        		        	int m = 0;
	        		        	Iterator<Map.Entry<Integer, ArrayList<Integer>>> ite = nestedIntArrayList.entrySet().iterator();
	        		        	while(ite.hasNext())
	        		        	{
	        		        		Map.Entry<Integer, ArrayList<Integer>> e = ite.next();
	        		        		m++;
	        		        		if(selected - selectedIntArrayProp == m)
	        		        		{
	        		        			e.getValue().add(Integer.parseInt(selectedText));
	        		        			break;
	        		        		}
	        		        	}
	    		        	}
	    		        }
	    		        textDummy.setText("");
	        			textDummy.setCursorPositionEnd();
	        			textDummy.setEnabled(true);
	        			textDummy.setFocused(true);
	        			textDummy.textboxKeyTyped(c, i);
	        			textDummy.setEnabled(false);
	        			textDummy.setFocused(false);
	        			textDummy.setCursorPositionEnd();
	        			selectedText = textDummy.getText();
	        			
	        			updateProperty(prop, selectedText);
	           		}
    			}
    			else
    			{
	           		Property prop = config.props.get(config.propNameToProp.get(propNames.get(selected)));
	           		if(isValidValue(prop, selectedText))
	           		{
	           			updateProperty(prop, selectedText);
	           		}
	       			selected = -1;
    			}
    		}
    		else
    		{
    			if(selectedIntArrayProp != -1 && selectedText.equalsIgnoreCase("") && i == Keyboard.KEY_BACK)
    			{
    				Property prop = config.props.get(config.propNameToProp.get(propNames.get(selectedIntArrayProp)));
    		        if(config.intArrayList.contains(prop))
    		        {
    		        	if(intArrayList.size() > 0)
    		        	{
    		        		textDummy.setText(intArrayList.get(intArrayList.size() - 1).toString() + " ");
    		        		intArrayList.remove(intArrayList.size() - 1);
    		        		updateProperty(prop, selectedText);
    		        	}
    		        }
    		        else
    		        {
    		        	if(nestedIntArrayList.size() > 0)
    		        	{
        		        	int m = 0;
        		        	Iterator<Map.Entry<Integer, ArrayList<Integer>>> ite = nestedIntArrayList.entrySet().iterator();
        		        	while(ite.hasNext())
        		        	{
        		        		Map.Entry<Integer, ArrayList<Integer>> e = ite.next();
        		        		m++;
        		        		if(selected - selectedIntArrayProp == m)
        		        		{
        		        			if(e.getValue().size() > 0)
        		        			{
        		        				textDummy.setText(e.getValue().get(e.getValue().size() - 1).toString() + " ");
        		        				e.getValue().remove(e.getValue().size() - 1);
        		        			}
        		        			else
        		        			{
        		        				textDummy.setText(e.getKey().toString() + " ");
        		        				ite.remove();
        		        				intArraySlots--;
        		        			}
        		        			updateProperty(prop, selectedText);
        		        			break;
        		        		}
        		        	}
    		        	}
    		        }
    			}
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
