package me.ichun.mods.ichunutil.client.core;

import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.util.ObfHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.shader.Framebuffer;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandlerClient
{
    public static Screen getConfigGui(Minecraft mc, Screen parentScreen) { return new WorkspaceConfigs(parentScreen); } //for mod config compat

    public int ticks;

    public float partialTick;

    public int screenWidth;
    public int screenHeight;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            ticks++;
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if(event.phase == TickEvent.Phase.START)
        {
            Minecraft mc = Minecraft.getInstance();

            partialTick = event.renderTickTime;

            if(screenWidth != mc.getMainWindow().getFramebufferWidth() || screenHeight != mc.getMainWindow().getFramebufferHeight())
            {
                screenWidth = mc.getMainWindow().getFramebufferWidth();
                screenHeight = mc.getMainWindow().getFramebufferHeight();

                for(Framebuffer buffer : RenderHelper.frameBuffers)
                {
                    buffer.resize(screenWidth, screenHeight, Minecraft.IS_RUNNING_ON_MAC);
                }
            }
        }
    }

    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event)
    {
        if((iChunUtil.configClient.buttonOptionsShiftOpensMods || ObfHelper.isDevEnvironment()) && event.getGui() instanceof IngameMenuScreen)
        {
            String optionsText = I18n.format("menu.options");
            for(Widget widget : event.getWidgetList())
            {
                if(optionsText.equals(widget.getMessage().getUnformattedComponentText()) && widget instanceof Button)
                {
                    Button.IPressable oriPress = ((Button)widget).onPress;
                    ((Button)widget).onPress = button -> {
                        if(ObfHelper.isDevEnvironment() && !Screen.hasControlDown())
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
