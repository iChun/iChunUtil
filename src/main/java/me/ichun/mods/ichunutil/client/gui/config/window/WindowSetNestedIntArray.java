package me.ichun.mods.ichunutil.client.gui.config.window;

import me.ichun.mods.ichunutil.client.gui.config.GuiConfigs;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.gui.window.element.Element;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementButton;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementTextInputNumber;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import me.ichun.mods.ichunutil.common.core.config.types.NestedIntArray;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class WindowSetNestedIntArray extends Window
{
    public GuiConfigs parent;
    public ConfigBase config;
    public ConfigBase.PropInfo prop;

    public WindowSetNestedIntArray(GuiConfigs parent, int w, int h, int minW, int minH, ConfigBase conf, ConfigBase.PropInfo info)
    {
        super(parent, 0, 0, w, h, minW, minH, "ichunutil.config.gui.setIntArray", true);
        this.parent = parent;
        this.config = conf;
        this.prop = info;

        elements.add(new ElementButton(this, width / 2 - 30, height - 25, 60, 16, 3, false, 2, 1, "gui.done"));

        try
        {
            NestedIntArray vals = (NestedIntArray)info.field.get(config);
            int i = 0;
            for(Map.Entry<Integer, ArrayList<Integer>> e : vals.values.entrySet())
            {
                elements.add(new ElementTextInputNumber(this, 10, i * 18 + 20, 40, 12, i * 100, prop.comment, Integer.toString(e.getKey()), false));
                ArrayList<Integer> nest = e.getValue();
                for(int j = 0; j < nest.size(); j++)
                {
                    elements.add(new ElementTextInputNumber(this, 52 + (j * 39), i * 18 + 20, 40, 12, i * 100 + (j + 1), prop.comment, Integer.toString(nest.get(j)), false));
                }
                i++;
            }
        }
        catch(Exception ignored)
        {
        }
        ;
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
        ElementTextInputNumber parentNum = null;
        boolean lastIsEmpty = false;

        ArrayList<Element> elementsToAdd = new ArrayList<>();
        ArrayList<Element> elementsToRemove = new ArrayList<>();
        for(Element e : elements)
        {
            if(e instanceof ElementTextInputNumber && e.id % 100 == 0)
            {
                if(e.id > i)
                {
                    if(parentNum != null)
                    {
                        lastIsEmpty = parentNum.textField.getText().isEmpty();
                        if(lastIsEmpty)
                        {
                            boolean remove = true;
                            for(Element e1 : elements)
                            {
                                if(e1 instanceof ElementTextInputNumber && e1.id - parentNum.id > 0 && e1.id - parentNum.id < 100 && !((ElementTextInputNumber)e1).textField.getText().isEmpty())
                                {
                                    remove = false;
                                    lastIsEmpty = false;
                                }
                            }

                            if(remove)
                            {
                                for(Element e1 : elements)
                                {
                                    if(e1 instanceof ElementTextInputNumber && e1.id - parentNum.id > 0 && e1.id - parentNum.id < 100)
                                    {
                                        elementsToRemove.add(e1);
                                    }
                                }
                            }
                        }
                    }
                    i = e.id;
                    parentNum = ((ElementTextInputNumber)e);
                }

                int j = -1;
                ElementTextInputNumber childNum = ((ElementTextInputNumber)e);
                boolean lastChildIsEmpty = false;
                for(Element e1 : elements)
                {
                    if(e1 instanceof ElementTextInputNumber && e1.id - e.id > 0 && e1.id - e.id < 100 && e1.id > j)
                    {
                        if(childNum != ((ElementTextInputNumber)e))
                        {
                            lastChildIsEmpty = childNum.textField.getText().isEmpty();
                        }

                        j = (e1.id - e.id) - 1;
                        childNum = (ElementTextInputNumber)e1;
                    }
                }

                if(!((ElementTextInputNumber)e).textField.getText().isEmpty() && (j == -1 || !childNum.textField.getText().isEmpty()))
                {
                    elementsToAdd.add(new ElementTextInputNumber(this, 52 + ((j + 1) * 39), ((i / 100)) * 18 + 20, 40, 12, ((i / 100)) * 100 + (j + 2), prop.comment, "", false));
                }
                else if(j > 0 && lastChildIsEmpty)
                {
                    elementsToRemove.add(childNum);
                }
            }
        }
        if(i == -1 || !parentNum.textField.getText().isEmpty())
        {
            elements.add(new ElementTextInputNumber(this, 10, ((i / 100) + 1) * 18 + 20, 40, 12, ((i / 100) + 1) * 100, prop.comment, "", false));
        }
        else if(i > 0 && lastIsEmpty)
        {
            elements.remove(parentNum);
        }

        elements.removeAll(elementsToRemove);
        elements.addAll(elementsToAdd);
    }

    @Override
    public void elementTriggered(Element element)
    {
        try
        {
            if(!(element instanceof ElementTextInputNumber))
            {
                TreeMap<Integer, ArrayList<ElementTextInputNumber>> elementMap = new TreeMap<>();

                for(Element e : elements)
                {
                    if(e instanceof ElementTextInputNumber)
                    {
                        ArrayList<ElementTextInputNumber> nums = elementMap.get(e.id - (e.id % 100));
                        if(nums == null)
                        {
                            nums = new ArrayList<>();
                            elementMap.put(e.id - (e.id % 100), nums);
                        }

                        String text = ((ElementTextInputNumber)e).textField.getText();
                        if(!text.isEmpty() && !(text.equals("-") || text.equals(".")))
                        {
                            nums.add((ElementTextInputNumber)e);
                        }
                    }
                }

                try
                {
                    NestedIntArray vals = (NestedIntArray)prop.field.get(config);

                    vals.values = new TreeMap<>();

                    for(Map.Entry<Integer, ArrayList<ElementTextInputNumber>> e : elementMap.entrySet())
                    {
                        ArrayList<Integer> list = new ArrayList<>();
                        int parent = Short.MIN_VALUE + 32;

                        for(ElementTextInputNumber e1 : e.getValue())
                        {
                            String text = ((ElementTextInputNumber)e1).textField.getText();
                            int val = 0;
                            if(!text.isEmpty())
                            {
                                if(!(text.equals("-") || text.equals(".")))
                                {
                                    val = Integer.parseInt(text);
                                }
                                if(e1.id % 100 == 0)
                                {
                                    parent = val;
                                }
                                else
                                {
                                    list.add(val);
                                }
                            }
                        }

                        if(parent != Short.MIN_VALUE + 32)
                        {
                            vals.values.put(parent, list);
                        }
                    }

                    ConfigProp propInfo = prop.field.getAnnotation(ConfigProp.class);
                    if(!propInfo.changeable() || propInfo.useSession())
                    {
                        parent.needsRestart();
                    }
                    config.onConfigChange(prop.field, vals);

                    parent.windowSetter.props.saveTimeout = 10;
                    parent.keyBindTimeout = 5;
                    parent.removeWindow(this, true);
                    parent.elementSelected = null;
                }
                catch(Exception ignored)
                {
                }
            }
        }
        catch(Exception ignored)
        {
        }
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
