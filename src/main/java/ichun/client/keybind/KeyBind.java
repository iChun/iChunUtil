package ichun.client.keybind;

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

    public boolean pressed;

    public int usages; //if usages == 0, deregister;

    public KeyBind(int index, boolean shift, boolean ctrl, boolean alt)
    {
        keyIndex = index;
        holdShift = shift;
        holdCtrl = ctrl;
        holdAlt = alt;
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

        pressed = isPressed(keyIndex);
        if(pressed != flag)
        {
            triggerEvent(false);
        }
    }

    public void setPulse(boolean flag, int time)
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
                    bind.pulseTime == pulseTime;
        }
        return false;
    }

    public static boolean isPressed(int key)
    {
        if(key < 0)
        {
            return Mouse.isButtonDown(key + 100);
        }
        return Keyboard.isKeyDown(key);
    }
}
