package us.ichun.mods.ichunutil.client.gui.config.window;

import net.minecraftforge.common.config.Property;
import us.ichun.mods.ichunutil.client.gui.config.GuiConfigs;
import us.ichun.mods.ichunutil.client.gui.config.window.element.ElementPropSetter;
import us.ichun.mods.ichunutil.client.gui.window.Window;
import us.ichun.mods.ichunutil.client.gui.window.element.Element;
import us.ichun.mods.ichunutil.common.core.config.ConfigBase;

import java.util.Map;
import java.util.TreeMap;

public class WindowSetter extends Window
{
    public ElementPropSetter props;

    public GuiConfigs parent;
    public ConfigBase.CategoryInfo selectedCat;

    public WindowSetter(GuiConfigs parent, int x, int y, int w, int h, int minW, int minH)
    {
        super(parent, x, y, w, h, minW, minH, "", true);
        this.parent = parent;

        props = new ElementPropSetter(this, BORDER_SIZE + 1, BORDER_SIZE + 1 + 10, width - (BORDER_SIZE * 2 + 2), height - (BORDER_SIZE * 2 + 2) - 11, 0);
        elements.add(props);
    }

    @Override
    public void resized()
    {
        posX = 219;
        posY = 1;
        width = workspace.width - 1 - posX;
        height = workspace.height - 2;

        super.resized();
    }

    public void draw(int mouseX, int mouseY) //4 pixel border?
    {
        if(parent.windowCats.configs.selectedIdentifier.isEmpty())
        {
            return;
        }
        else
        {
            for(us.ichun.mods.ichunutil.client.gui.window.element.ElementListTree.Tree tree : parent.windowCats.configs.trees)
            {
                if(tree.selected)
                {
                    ConfigBase.CategoryInfo cat = (ConfigBase.CategoryInfo)tree.attachedObject;
                    if(!cat.equals(selectedCat) && parent.windowCats.selectedConfig != null)
                    {
                        props.saveTimeout = 0;
                        props.save();

                        selectedCat = cat;
                        titleLocale = cat.name;

                        props.trees.clear();
                        props.sliderProg = 0.0D;
                        for(ConfigBase.PropInfo prop : parent.windowCats.selectedConfig.categories.get(selectedCat))
                        {
                            props.createTree(parent.windowCats.selectedConfig, prop, 17);
                        }
                    }
                }
            }
        }
        super.draw(mouseX, mouseY);
    }

    @Override
    public void elementTriggered(Element element)
    {
    }

    @Override
    public boolean canBeDragged()
    {
        return false;
    }

    @Override
    public boolean isStatic()
    {
        return true;
    }

    @Override
    public boolean canMinimize() { return false; }
}
