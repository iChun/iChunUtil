package me.ichun.mods.ichunutil.client.gui.config.window;

import me.ichun.mods.ichunutil.client.gui.config.GuiConfigs;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.gui.window.element.Element;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementButton;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementTextInputNumber;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;

import java.util.ArrayList;

public class WindowSetIntArray extends Window
{
    public GuiConfigs parent;
    public ConfigBase config;
    public ConfigBase.PropInfo prop;

    public WindowSetIntArray(GuiConfigs parent, int w, int h, int minW, int minH, ConfigBase conf, ConfigBase.PropInfo info)
    {
        super(parent, 0, 0, w, h, minW, minH, "ichunutil.config.gui.setIntArray", true);
        this.parent = parent;
        this.config = conf;
        this.prop = info;

        elements.add(new ElementButton(this, width / 2 - 30, height - 25, 60, 16, 3, false, 2, 1, "gui.done"));

        try
        {
            int[] vals = (int[])info.field.get(config);
            for(int i = 0; i < vals.length; i++)
            {
                elements.add(new ElementTextInputNumber(this, 10, i * 18 + 20, width - 20, 12, i, prop.comment, Integer.toString(vals[i]), false));
            }
        }
        catch(Exception ignored){};
    }

    @Override
    public void draw(int mouseX, int mouseY)
    {
        super.draw(mouseX, mouseY);
    }

    @Override
    public void update()
    {
        int i = -1;
        ElementTextInputNumber text = null;
        boolean lastIsEmpty = false;
        for(Element e : elements)
        {
            if(e instanceof ElementTextInputNumber && e.id > i)
            {
                if(text != null)
                {
                    lastIsEmpty = text.textField.getText().isEmpty();
                }
                i = e.id;
                text = ((ElementTextInputNumber)e);
            }
        }
        if(i == -1 || !text.textField.getText().isEmpty())
        {
            elements.add(new ElementTextInputNumber(this, 10, (i + 1) * 18 + 20, width - 20, 12, (i + 1), prop.comment, "", false));
        }
        else if(i > 0 && lastIsEmpty)
        {
            elements.remove(text);
        }
    }

    @Override
    public void elementTriggered(Element element)
    {
        try
        {
            if(!(element instanceof ElementTextInputNumber))
            {
                ArrayList<Integer> strings = new ArrayList<Integer>();

                for(Element e : elements)
                {
                    if(e instanceof ElementTextInputNumber)
                    {
                        String text = ((ElementTextInputNumber)e).textField.getText();
                        int val = 0;
                        if(!text.isEmpty())
                        {
                            if(!(text.equals("-") || text.equals(".")))
                            {
                                val = Integer.parseInt(text);
                            }
                            strings.add(val);
                        }
                    }
                }

                int[] array = new int[strings.size()];
                for(int i = 0; i < array.length; i++)
                {
                    array[i] = strings.get(i);
                }

                ConfigProp propInfo = prop.field.getAnnotation(ConfigProp.class);
                if(!propInfo.changeable() || propInfo.useSession())
                {
                    parent.needsRestart();
                }
                int[] ori = (int[])prop.field.get(config);
                prop.field.set(config, array);
                config.onConfigChange(prop.field, ori);

                parent.windowSetter.props.saveTimeout = 10;
                parent.keyBindTimeout = 5;
                parent.removeWindow(this, true);
                parent.elementSelected = null;
            }
        }
        catch(Exception ignored){}
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
