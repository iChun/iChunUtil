package us.ichun.mods.ichunutil.client.gui.config.window;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.Property;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import us.ichun.mods.ichunutil.client.gui.Theme;
import us.ichun.mods.ichunutil.client.gui.config.GuiConfigs;
import us.ichun.mods.ichunutil.client.gui.config.window.element.ElementKeyBindHook;
import us.ichun.mods.ichunutil.client.gui.window.Window;
import us.ichun.mods.ichunutil.client.gui.window.element.Element;
import us.ichun.mods.ichunutil.common.core.config.ConfigBase;

public class WindowSetKeyBind extends Window
{
    public GuiConfigs parent;
    public ConfigBase config;
    public Property prop;

    public boolean releasedMouse;
    public int lastKeyHeld;
    public int keyHeldTime;

    public String message;

    public ElementKeyBindHook hook;

    public WindowSetKeyBind(GuiConfigs parent, int w, int h, int minW, int minH, String msg, ConfigBase conf, Property property)
    {
        super(parent, 0, 0, w, h, minW, minH, "ichun.config.gui.setKeyBind", true);
        this.parent = parent;
        this.config = conf;
        this.prop = property;
        message = msg;
        hook = new ElementKeyBindHook(this, 0,0,0,0,0,true);
    }

    @Override
    public void draw(int mouseX, int mouseY)
    {
        super.draw(mouseX, mouseY);

        workspace.getFontRenderer().drawString(StatCollector.translateToLocal(message), posX + (width - workspace.getFontRenderer().getStringWidth(StatCollector.translateToLocal(message))) / 2, posY + (height - 12 - 9) / 2, Theme.getAsHex(workspace.currentTheme.font), false);

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

                    parent.windowSetter.props.updateProperty(config, prop, sb.toString());
                    parent.windowSetter.props.saveTimeout = 10;
                    parent.keyBindTimeout = 5;
                    parent.removeWindow(this, true);
                    parent.elementSelected = null;

                    break;
                }
            }
        }
    }

    @Override
    public void update()
    {
        workspace.elementSelected = hook;
        if(GuiScreen.isShiftKeyDown() || GuiScreen.isCtrlKeyDown() || (Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU)))
        {
            keyHeldTime++;
            if(keyHeldTime >= 60)
            {
                keyHeldTime = 0;
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

                parent.windowSetter.props.updateProperty(config, prop, sb.toString());
                parent.windowSetter.props.saveTimeout = 10;
                parent.keyBindTimeout = 5;
                parent.removeWindow(this, true);
                parent.elementSelected = null;
            }
        }
    }

    @Override
    public void elementTriggered(Element element)
    {
        parent.windowSetter.props.saveTimeout = 10;
        parent.keyBindTimeout = 5;
        parent.removeWindow(this, true);
        parent.elementSelected = null;
    }

    @Override
    public boolean allowMultipleInstances()
    {
        return false;
    }

    @Override
    public boolean canBeDragged()
    {
        return false;
    }

    @Override
    public boolean canMinimize() { return false; }
}
