package me.ichun.mods.ichunutil.client.gui.config.window;

import me.ichun.mods.ichunutil.client.gui.bns.window.Window;
import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import me.ichun.mods.ichunutil.client.gui.config.window.view.ViewValues;

public class WindowValues extends Window<WorkspaceConfigs>
{
    public WindowValues(WorkspaceConfigs parent, WorkspaceConfigs.ConfigInfo info, String category)
    {
        super(parent);
        setView(new ViewValues(this, info.config.getConfigName() + " - " + WorkspaceConfigs.getLocalizedCategory(info, category, "name"), info, category, info.categories.get(category)));
        pos(20, 20);
        size(200, 300);
        disableUndocking();
        disableDrag();
        disableBringToFront();
    }
}
