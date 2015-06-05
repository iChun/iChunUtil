package us.ichun.mods.ichunutil.client.gui.config.window;

import us.ichun.mods.ichunutil.client.gui.config.GuiConfigs;
import us.ichun.mods.ichunutil.client.gui.window.Window;
import us.ichun.mods.ichunutil.client.gui.window.element.Element;
import us.ichun.mods.ichunutil.client.gui.window.element.ElementButton;
import us.ichun.mods.ichunutil.client.gui.window.element.ElementTextInput;
import us.ichun.mods.ichunutil.common.core.config.ConfigBase;
import us.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;

import java.util.ArrayList;

public class WindowSetStringArray extends Window
{
    public GuiConfigs parent;
    public ConfigBase config;
    public ConfigBase.PropInfo prop;

    public WindowSetStringArray(GuiConfigs parent, int w, int h, int minW, int minH, ConfigBase conf, ConfigBase.PropInfo info)
    {
        super(parent, 0, 0, w, h, minW, minH, "ichunutil.config.gui.setStringArray", true);
        this.parent = parent;
        this.config = conf;
        this.prop = info;

        elements.add(new ElementButton(this, width / 2 - 30, height - 25, 60, 16, 3, false, 2, 1, "gui.done"));

        try
        {
            String[] vals = (String[])info.field.get(config);
            for(int i = 0; i < vals.length; i++)
            {
                elements.add(new ElementTextInput(this, 10, i * 18 + 20, width - 20, 12, i, prop.comment, vals[i]));
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
        ElementTextInput text = null;
        boolean lastIsEmpty = false;
        for(Element e : elements)
        {
            if(e instanceof ElementTextInput && e.id > i)
            {
                if(text != null)
                {
                    lastIsEmpty = text.textField.getText().isEmpty();
                }
                i = e.id;
                text = ((ElementTextInput)e);
            }
        }
        if(i == -1 || !text.textField.getText().isEmpty())
        {
            elements.add(new ElementTextInput(this, 10, (i + 1) * 18 + 20, width - 20, 12, (i + 1), prop.comment, ""));
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
            if(!(element instanceof ElementTextInput))
            {
                ArrayList<String> strings = new ArrayList<String>();

                for(Element e : elements)
                {
                    if(e instanceof ElementTextInput && !((ElementTextInput)e).textField.getText().isEmpty())
                    {
                        strings.add(((ElementTextInput)e).textField.getText());
                    }
                }

                String[] array = new String[strings.size()];
                for(int i = 0; i < array.length; i++)
                {
                    array[i] = strings.get(i);
                }

                ConfigProp propInfo = prop.field.getAnnotation(ConfigProp.class);
                if(!propInfo.changeable() || propInfo.useSession())
                {
                    parent.needsRestart();
                }
                String[] ori = (String[])prop.field.get(config);
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
