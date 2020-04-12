package me.ichun.mods.ichunutil.client.gui.config.window;

import me.ichun.mods.ichunutil.client.gui.bns.window.Window;
import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import me.ichun.mods.ichunutil.client.gui.config.window.view.ViewConfigs;
import net.minecraft.client.resources.I18n;

public class WindowConfigs extends Window<WorkspaceConfigs>
{

    public WindowConfigs(WorkspaceConfigs parent)
    {
        super(parent);
        setView(new ViewConfigs(this, I18n.format("gui.ichunutil.configs.options")));
        pos(20, 20);
        size(120, 300);
        disableUndocking();
        disableDrag();
        disableBringToFront();
    }
}
