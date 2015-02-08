package us.ichun.mods.ichunutil.client.gui.config.window;

import us.ichun.mods.ichunutil.client.gui.config.GuiConfigs;
import us.ichun.mods.ichunutil.client.gui.window.IWorkspace;
import us.ichun.mods.ichunutil.client.gui.window.Window;
import us.ichun.mods.ichunutil.client.gui.window.element.Element;
import us.ichun.mods.ichunutil.client.gui.window.element.ElementButton;
import us.ichun.mods.ichunutil.client.gui.window.element.ElementListTree;
import us.ichun.mods.ichunutil.common.core.config.ConfigBase;
import us.ichun.mods.ichunutil.common.core.config.ConfigHandler;

public class WindowConfigs extends Window
{
    public ElementListTree configs;

    public WindowConfigs(IWorkspace parent, int x, int y, int w, int h, int minW, int minH)
    {
        super(parent, x, y, w, h, minW, minH, "ichunutil.config.gui.options", true);

        elements.add(new ElementButton(this, 10, height - 22, 60, 16, -1, false, 0, 1, "gui.done"));

        configs = new ElementListTree(this, BORDER_SIZE + 1, BORDER_SIZE + 1 + 10, width - (BORDER_SIZE * 2 + 2), height - BORDER_SIZE - 22 - 16, 3, false, false);
        elements.add(configs);

        for(ConfigBase config : ConfigHandler.configs)
        {
            configs.createTree(null, config, 13, 0, false, false);
        }
    }

    @Override
    public void resized()
    {
        posX = 1;
        posY = 1;
        width = 100;
        height = workspace.height - 2;

        super.resized();
    }

    @Override
    public void elementTriggered(Element element)
    {
        if(element.id == -1)
        {
            workspace.mc.displayGuiScreen(((GuiConfigs)workspace).oriScreen);

            if(workspace.mc.currentScreen == null)
            {
                workspace.mc.setIngameFocus();
            }
        }
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
