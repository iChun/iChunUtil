package me.ichun.mods.ichunutil.client.core;

import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandlerClient
{
    public static Screen getConfigGui(Minecraft mc, Screen parentScreen) { return new WorkspaceConfigs(parentScreen); } //for mod config compat

    public int ticks;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            ticks++;
        }
    }

    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event)
    {
        if((iChunUtil.configClient.buttonOptionsShiftOpensMods || iChunUtil.isDevEnvironment()) && event.getGui() instanceof IngameMenuScreen)
        {
            String optionsText = I18n.format("menu.options");
            for(Widget widget : event.getWidgetList())
            {
                if(optionsText.equals(widget.getMessage()) && widget instanceof Button)
                {
                    Button.IPressable oriPress = ((Button)widget).onPress;
                    ((Button)widget).onPress = button -> {
                        if(iChunUtil.isDevEnvironment() && !Screen.hasControlDown())
                        {
                            if(Screen.hasShiftDown())
                            {
                                oriPress.onPress(button);
                            }
                            else
                            {
                                Minecraft.getInstance().displayGuiScreen(getConfigGui(Minecraft.getInstance(), Minecraft.getInstance().currentScreen));
                            }
                        }
                        else if(iChunUtil.configClient.buttonOptionsShiftOpensMods)
                        {
                            if(Screen.hasShiftDown())
                            {
                                Minecraft.getInstance().displayGuiScreen(new net.minecraftforge.fml.client.gui.screen.ModListScreen(Minecraft.getInstance().currentScreen));
                            }
                            else
                            {
                                oriPress.onPress(button);
                            }
                        }
                    };
                    break;
                }
            }
        }
    }
}
