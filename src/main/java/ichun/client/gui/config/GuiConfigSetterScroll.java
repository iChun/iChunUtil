package ichun.client.gui.config;

import ichun.client.keybind.KeyBind;
import ichun.common.core.config.Config;
import ichun.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.security.Key;
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

    private boolean settingKeybind;
    private boolean releasedMouse;
    private int lastKeyHeld;
    private int keyHeldTime;

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
        settingKeybind = false;
        releasedMouse = false;

        lastKeyHeld = 0;
        keyHeldTime = 0;
    }

    @Override
    protected int getSize()
    {
        return selectedIntArrayProp != -1 ? properties.size() + intArraySlots : properties.size();
    }

    @Override
    protected void elementClicked(int i, boolean flag, int mouseX, int mouseY)
    {
        if(settingKeybind)
        {
            return;
        }
        if (!flag)
        {
        	if(selected != -1) //update property
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
                Config.EnumPropType type = config.getPropType(prop);
            	if(type == Config.EnumPropType.INT_ARRAY || type == Config.EnumPropType.NESTED_INT_ARRAY)
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

            		if(type == Config.EnumPropType.NESTED_INT_ARRAY)
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
                else if(type == Config.EnumPropType.INT_BOOL)
                {
                    prop.set(prop.getInt() == 1 ? 0 : 1);
                    updateProperty(prop, prop.getString());

                    selected = -1;
                }
            	else
            	{
                    if(type == Config.EnumPropType.KEYBIND)
                    {
                        settingKeybind = true;
                        releasedMouse = false;
                        keyHeldTime = 0;
                    }

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
        switch(config.getPropType(prop))
        {
            case INT_BOOL:
            case INT:
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
            case KEYBIND:
            case STRING:
            {
                return true;
            }
            case INT_ARRAY:
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
            case NESTED_INT_ARRAY:
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
            default:
            {
                return false;
            }
        }
    }
    
    public boolean updateProperty(Property prop, String s)
    {
        String _default = prop.getString();

        switch(config.getPropType(prop))
        {
            case INT_BOOL:
            case INT:
            {
                try
                {
                    int i = Integer.parseInt(s);

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
            case INT_ARRAY:
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

                break;
            }
            case NESTED_INT_ARRAY:
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

                break;
            }
            case KEYBIND:
            {
                KeyBind oriKeyBind = config.keyBindMap.get(prop);

                String keyString = s;
                String[] strings = keyString.split(":");
                KeyBind bind;
                try
                {
                    bind = new KeyBind(Integer.parseInt(strings[0].trim()), keyString.contains("SHIFT"), keyString.contains("CTRL"), keyString.contains("ALT"));
                }
                catch(Exception e)
                {
                    iChunUtil.console("Error parsing key, this shouldn't happen: " + keyString, true);
                    e.printStackTrace();
                    bind = oriKeyBind;
                }

                config.keyBindMap.put(prop, iChunUtil.proxy.registerKeyBind(bind, oriKeyBind));

                prop.set(s);

                break;
            }
            case STRING:
            {
                prop.set(s);

                break;
            }
            default:
            {
                return false;
            }
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

        if(settingKeybind)
        {
            if(!releasedMouse)
            {
                releasedMouse = !Mouse.isButtonDown(0);
            }
            else
            {
                for(int i = 0; i < 16; i++)
                {
                    if(Mouse.isButtonDown(i))
                    {
                        if(Minecraft.isRunningOnMac && i == 0 && (Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157)))
                        {
                            i = 1;
                        }

                        Property prop = config.props.get(config.propNameToProp.get(propNames.get(selected)));
                        if(config.getPropType(prop) == Config.EnumPropType.KEYBIND)
                        {
                            StringBuilder sb = new StringBuilder();

                            sb.append(i - 100);

                            if(GuiScreen.isShiftKeyDown())
                            {
                                sb.append(":SHIFT");
                            }
                            if(GuiScreen.isCtrlKeyDown())
                            {
                                sb.append(":CTRL");
                            }
                            if(Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184))
                            {
                                sb.append(":ALT");
                            }

                            updateProperty(prop, sb.toString());

                            settingKeybind = false;
                            selected = -1;
                        }

                        break;
                    }
                }
            }
        }
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
	        if(config.getPropType(prop) == Config.EnumPropType.INT_ARRAY)
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

            switch(config.getPropType(prop))
            {
                case KEYBIND:
                {
                    int clr = 14737632;

                    if (k == 2)
                    {
                        clr = 16777120;

                        ArrayList<String> tooltip = new ArrayList<String>();
                        tooltip.add("These key binds allow a combination of Shift/Ctrl/Alt");
                        tooltip.add("");
                        tooltip.add("Try holding those down when setting a key bind");
                        tooltip.add("");
                        tooltip.add("If you want to bind Shift/Ctrl/Alt, hold them for 3 seconds. Last key held will be bound.");
                        controls.drawTooltip(tooltip, 0, 35);

                        mc.renderEngine.bindTexture(WIDGITS);
                    }

                    controls.drawTexturedModalRect(xPosition + width - 70, yPosition, 0, 46 + k * 20, 35, height);
                    controls.drawTexturedModalRect(xPosition + width - 35, yPosition, 200 - 35, 46 + k * 20, 35, height);

                    String keyString = prop.getString();
                    String[] strings = keyString.split(":");

                    int key = 0;
                    if(strings.length > 0)
                    {
                        key = Integer.parseInt(strings[0].trim());
                    }

                    controls.drawCenteredString(mc.fontRenderer, settingKeybind && selected == index ? ">???<" : GameSettings.getKeyDisplayString(key), xPosition + width - 35, yPosition + (height - 8) / 2, selected == index ? 16777045 : clr);

                    GL11.glPushMatrix();

                    GL11.glScalef(0.5F, 0.5F, 0.5F);
                    if(keyString.contains("SHIFT") || settingKeybind && selected == index)
                    {
                        controls.drawString(mc.fontRenderer, "Shift", (xPosition + width - 70) * 2 - 6 - 18, (yPosition + (height - 8) / 2) * 2 - 10, settingKeybind && selected == index ? GuiScreen.isShiftKeyDown() ? 16777045 : -6250336 : clr);
                        GL11.glTranslatef(0.0F, 14F, 0.0F);
                    }

                    if(keyString.contains("CTRL") || settingKeybind && selected == index)
                    {
                        controls.drawString(mc.fontRenderer, "Ctrl", (xPosition + width - 70) * 2 - 6 - 14, (yPosition + (height - 8) / 2) * 2 - 10, settingKeybind && selected == index ? GuiScreen.isCtrlKeyDown() ? 16777045 : -6250336 : clr);
                        GL11.glTranslatef(0.0F, 14F, 0.0F);
                    }

                    if(keyString.contains("ALT") || settingKeybind && selected == index)
                    {
                        controls.drawString(mc.fontRenderer, "Alt", (xPosition + width - 70) * 2 - 6 - 8, (yPosition + (height - 8) / 2) * 2 - 10, settingKeybind && selected == index ? (Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184)) ? 16777045 : -6250336 : clr);
                    }

                    GL11.glPopMatrix();

                    break;
                }
                case INT_ARRAY:
                case NESTED_INT_ARRAY:
                {
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

                    break;
                }
                case INT_BOOL:
                {
                    int clr = 14737632;


                    if (k == 2)
                    {
                        clr = 16777120;
                    }

                    controls.drawTexturedModalRect(xPosition + width - 50, yPosition, 0, 46 + k * 20, 25, height);
                    controls.drawTexturedModalRect(xPosition + width - 25, yPosition, 200 - 25, 46 + k * 20, 25, height);

                    controls.drawCenteredString(mc.fontRenderer, prop.getInt() == 1 ? "Yes" : "No", xPosition + width - 25, yPosition + (height - 8) / 2, clr);

                    break;
                }
                //TODO different case for INT_BOOL
                default:
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

                    break;
                }
            }

            String str = (k == 2 ? EnumChatFormatting.YELLOW : "") + (propNames.get(selectedIntArrayProp != -1 && index > selectedIntArrayProp ? index - intArraySlots : index));
            controls.drawString(mc.fontRenderer, str, xPosition, yPosition + (height - 8) / 2, 0xFFFFFFFF);
                        
            if(k == 2 && prop.comment != null)
            {
    	        ArrayList<String> tooltip = new ArrayList<String>();
    	        String[] comments = prop.comment.split("\n");
    	        for(int i = 0; i < comments.length; i++)
    	        {
                    if(config.getPropType(prop) == Config.EnumPropType.INT_BOOL && i > comments.length - 4)
                    {
                        continue;
                    }
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
        if(settingKeybind)
        {
            Property prop = config.props.get(config.propNameToProp.get(propNames.get(selected)));
            if(config.getPropType(prop) == Config.EnumPropType.KEYBIND)
            {
                if(i == Keyboard.KEY_LSHIFT || i == Keyboard.KEY_RSHIFT || (Minecraft.isRunningOnMac ? (i == 219 || i == 220) : (i == 29 || i == 157)) || i == Keyboard.KEY_LMENU || i == Keyboard.KEY_RMENU)
                {
                    lastKeyHeld = i;
                    keyHeldTime = 0;
                }
                else
                {
                    StringBuilder sb = new StringBuilder();

                    sb.append(i);

                    if(GuiScreen.isShiftKeyDown())
                    {
                        sb.append(":SHIFT");
                    }
                    if(GuiScreen.isCtrlKeyDown())
                    {
                        sb.append(":CTRL");
                    }
                    if(Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184))
                    {
                        sb.append(":ALT");
                    }

                    updateProperty(prop, sb.toString());

                    settingKeybind = false;
                    selected = -1;
                }
            }
            return true;
        }
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
	    		        if(config.getPropType(prop) == Config.EnumPropType.INT_ARRAY)
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
    		        if(config.getPropType(prop) == Config.EnumPropType.INT_ARRAY)
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

    public void tick()
    {
        blinker++;
        if(blinker >= 16)
        {
            blinker = 0;
        }

        if(settingKeybind)
        {
            if(GuiScreen.isShiftKeyDown() || GuiScreen.isCtrlKeyDown() || (Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU)))
            {
                keyHeldTime++;
                if(keyHeldTime >= 60)
                {
                    keyHeldTime = 0;
                    Property prop = config.props.get(config.propNameToProp.get(propNames.get(selected)));
                    if(config.getPropType(prop) == Config.EnumPropType.KEYBIND)
                    {
                        StringBuilder sb = new StringBuilder();

                        sb.append(lastKeyHeld);

                        if(GuiScreen.isShiftKeyDown() && !(lastKeyHeld == Keyboard.KEY_LSHIFT || lastKeyHeld == Keyboard.KEY_RSHIFT))
                        {
                            sb.append(":SHIFT");
                        }
                        if(GuiScreen.isCtrlKeyDown() && !(Minecraft.isRunningOnMac ? (lastKeyHeld == 219 || lastKeyHeld == 220) : (lastKeyHeld == 29 || lastKeyHeld == 157)))
                        {
                            sb.append(":CTRL");
                        }
                        if((Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184)) && !(lastKeyHeld == 56 || lastKeyHeld == 184))
                        {
                            sb.append(":ALT");
                        }

                        updateProperty(prop, sb.toString());

                        settingKeybind = false;
                        selected = -1;
                    }
                }
            }
        }
    }
}
