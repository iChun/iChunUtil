package ichun.client.keybind;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class KeyBind
{
    public final int keyIndex;

    public final boolean holdShift;
    public final boolean holdCtrl;
    public final boolean holdAlt;

    public boolean canPulse;
    public int pulseTime;
    public int pulseTimer;

    public boolean ignoreHold;
    private boolean pressed;

    public int usages; //if usages == 0, deregister;

    public KeyBind(int index, boolean shift, boolean ctrl, boolean alt, boolean ignoreHolding)//ignoreHold will allow the keybind to trigger as long as the keybind is hit regardless of SHIFT/CTRL/ALT state
    {
        keyIndex = index;
        holdShift = shift;
        holdCtrl = ctrl;
        holdAlt = alt;
        ignoreHold = ignoreHolding;
    }

    public void tick()
    {
        if(canPulse)
        {
            if(pressed)
            {
                pulseTimer--;
                if(pulseTimer == 0)
                {
                    triggerEvent(true);
                    pulseTimer = pulseTime;
                }
            }
            else
            {
                pulseTimer = pulseTime;
            }
        }

        boolean flag = pressed;

        pressed = checkPressed();
        if(pressed != flag)
        {
            triggerEvent(false);
        }
    }

    @SideOnly(Side.CLIENT)
    private boolean checkPressed()
    {
        boolean stateShift = holdShift && GuiScreen.isShiftKeyDown() || !holdShift && (ignoreHold || !GuiScreen.isShiftKeyDown() || keyIndex == 42 || keyIndex == 54);
        boolean stateCtrl = holdCtrl && GuiScreen.isCtrlKeyDown() || !holdCtrl && (ignoreHold || !GuiScreen.isCtrlKeyDown() || keyIndex == (Minecraft.isRunningOnMac ? 219 : 29) || keyIndex == (Minecraft.isRunningOnMac ? 220 : 157));
        boolean stateAlt = holdAlt && (Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184)) || !holdAlt && (ignoreHold || !(Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184)) || keyIndex == 56 || keyIndex == 184);
        if(!stateShift || !stateCtrl || !stateAlt)
        {
            return false;
        }
        return isPressed(keyIndex);
    }

    public KeyBind setPulse(boolean flag, int time)
    {
        if(flag)
        {
            canPulse = true;
            pulseTime = time;
            pulseTimer = pulseTime;
        }
        else
        {
            canPulse = false;
        }
        return this;
    }

    public void triggerEvent(boolean pulse)
    {
        MinecraftForge.EVENT_BUS.post(new KeyEvent(this, pulse));
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof KeyBind)
        {
            KeyBind bind = (KeyBind)obj;
            return bind.keyIndex == keyIndex &&
                    bind.holdShift == holdShift &&
                    bind.holdCtrl == holdCtrl &&
                    bind.holdAlt == holdAlt &&
                    bind.canPulse == canPulse &&
                    bind.pulseTime == pulseTime &&
                    bind.ignoreHold == ignoreHold;
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    public static boolean isPressed(int key)
    {
        if(key < 0)
        {
            return Mouse.isButtonDown(key + 100);
        }
        return Keyboard.isKeyDown(key);
    }

    public boolean isPressed()
    {
        return pressed;
    }
}
