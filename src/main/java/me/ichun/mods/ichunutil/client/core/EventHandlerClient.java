package me.ichun.mods.ichunutil.client.core;

import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

public class EventHandlerClient
{
    public static Screen getConfigGui(Minecraft mc, Screen parentScreen) { return new WorkspaceConfigs(parentScreen); } //for mod config compat
}
