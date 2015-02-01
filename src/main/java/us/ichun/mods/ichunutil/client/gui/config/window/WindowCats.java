package us.ichun.mods.ichunutil.client.gui.config.window;

import us.ichun.mods.ichunutil.client.gui.config.GuiConfigs;
import us.ichun.mods.ichunutil.client.gui.window.Window;
import us.ichun.mods.ichunutil.client.gui.window.element.Element;
import us.ichun.mods.ichunutil.client.gui.window.element.ElementListTree;
import us.ichun.mods.ichunutil.common.core.config.Config;

public class WindowCats extends Window
{
    public ElementListTree configs;

    public GuiConfigs parent;
    public Config selectedConfig;

    public WindowCats(GuiConfigs parent, int x, int y, int w, int h, int minW, int minH)
    {
        super(parent, x, y, w, h, minW, minH, "", true);
        this.parent = parent;

        configs = new ElementListTree(this, BORDER_SIZE + 1, BORDER_SIZE + 1 + 10, width - (BORDER_SIZE * 2 + 2), height - (BORDER_SIZE * 2 + 2) - 11, 3, false, false);
        elements.add(configs);
    }

    @Override
    public void resized()
    {
        posX = 100;
        posY = 1;
        width = 120;
        height = workspace.height - 2;

        super.resized();
    }

    public void draw(int mouseX, int mouseY) //4 pixel border?
    {
        if(parent.windowConfigs.configs.selectedIdentifier.isEmpty())
        {
            return;
        }
        else
        {
            for(us.ichun.mods.ichunutil.client.gui.window.element.ElementListTree.Tree tree : parent.windowConfigs.configs.trees)
            {
                if(tree.selected)
                {
                    Config conf = (Config)tree.attachedObject;
                    if(conf != selectedConfig)
                    {
                        //NEW SELECTED CONFIG;
                        selectedConfig = conf;
                        titleLocale = selectedConfig.modName;
                        configs.selectedIdentifier = "";
                        configs.trees.clear();

                        for(String cat : selectedConfig.categoriesList)
                        {
                            configs.createTree(null, cat, 13, 0, false, false);
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
