package us.ichun.mods.ichunutil.client.gui.config.window.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import us.ichun.mods.ichunutil.client.gui.config.window.WindowSetKeyBind;
import us.ichun.mods.ichunutil.client.gui.window.element.Element;
import us.ichun.mods.ichunutil.client.keybind.KeyBind;
import us.ichun.mods.ichunutil.common.iChunUtil;

public class ElementKeyBindHook extends Element
{
    public WindowSetKeyBind bind;
    public ElementKeyBindHook(WindowSetKeyBind window, int x, int y, int w, int h, int ID, boolean igMin)
    {
        super(window, x, y, w, h, ID, igMin);
        bind = window;
    }

    @Override
    public void draw(int mouseX, int mouseY, boolean hover)
    {
    }

    @Override
    public void keyInput(char c, int i)
    {
        if((i == Keyboard.KEY_LSHIFT || i == Keyboard.KEY_RSHIFT || (Minecraft.isRunningOnMac ? (i == 219 || i == 220) : (i == 29 || i == 157)) || i == Keyboard.KEY_LMENU || i == Keyboard.KEY_RMENU))
        {
            if(bind.lastKeyHeld != i)
            {
                bind.keyHeldTime = 0;
            }
            bind.lastKeyHeld = i;
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

            try
            {
                KeyBind bind = (KeyBind)this.bind.prop.field.get(this.bind.config);

                KeyBind newKey = iChunUtil.proxy.registerKeyBind(new KeyBind(i, GuiScreen.isShiftKeyDown(), GuiScreen.isCtrlKeyDown(), Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184), bind.ignoreHold), bind);
                this.bind.prop.field.set(this.bind.config, newKey);
                this.bind.config.onConfigChange(this.bind.prop.field, newKey);
                this.bind.elementTriggered(this);
            }
            catch(Exception ignored)
            {
            }
        }
    }
}
