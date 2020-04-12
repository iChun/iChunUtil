package me.ichun.mods.ichunutil.client.gui.config.window;

import me.ichun.mods.ichunutil.client.gui.bns.window.IWindows;
import me.ichun.mods.ichunutil.client.gui.bns.window.Window;
import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import me.ichun.mods.ichunutil.client.gui.config.window.view.ViewEditList;

public class WindowEditList<M extends IWindows> extends Window<M>
{
    public final WorkspaceConfigs.ConfigInfo.ValueWrapperLocalised valueWrapper;

    public WindowEditList(M parent, WorkspaceConfigs.ConfigInfo.ValueWrapperLocalised valueWrapper)
    {
        super(parent);
        this.valueWrapper = valueWrapper;

        setView(new ViewEditList(this, valueWrapper.name, valueWrapper));
        disableDocking();
        disableDockStacking();
        disableUndocking();
    }
}
