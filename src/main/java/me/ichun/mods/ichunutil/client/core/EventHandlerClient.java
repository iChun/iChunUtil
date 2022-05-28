package me.ichun.mods.ichunutil.client.core;

import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.util.ObfHelper;
import me.ichun.mods.ichunutil.loader.LoaderHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;

import java.lang.reflect.InvocationTargetException;

public class EventHandlerClient
{
    public static Screen getConfigGui(Minecraft mc, Screen parentScreen) { return new WorkspaceConfigs(parentScreen); } //for mod config compat

    public int ticks;

    public float partialTick;

    public void onClientTickEnd(Minecraft mc)
    {
        ticks++;
    }

    public void onRenderTickStart(float partialTick)
    {
        this.partialTick = partialTick;
    }

    public void onPostInitScreen(Minecraft mc, Screen screen)
    {
        if((LoaderHandler.getEnv().equals(LoaderHandler.Env.FORGE) && iChunUtil.configClient.buttonOptionsShiftOpensMods || ObfHelper.isDevEnvironment()) && screen instanceof PauseScreen)
        {
            for(GuiEventListener widget : screen.children())
            {
                if(widget instanceof Button && ((Button)widget).getMessage() instanceof TranslatableComponent && ((TranslatableComponent)((Button)widget).getMessage()).getKey().equals("menu.options"))
                {
                    Button.OnPress oriPress = ((Button)widget).onPress;
                    ((Button)widget).onPress = button -> {
                        if(ObfHelper.isDevEnvironment() && !Screen.hasControlDown())
                        {
                            if(Screen.hasShiftDown())
                            {
                                oriPress.onPress(button);
                            }
                            else
                            {
                                mc.setScreen(getConfigGui(mc, mc.screen));
                            }
                        }
                        else if(iChunUtil.configClient.buttonOptionsShiftOpensMods)
                        {
                            if(Screen.hasShiftDown())
                            {
                                try
                                {
                                    Class clz = Class.forName("net.minecraftforge.client.gui.ModListScreen");
                                    Screen modsScreen = (Screen)clz.getDeclaredConstructor(Screen.class).newInstance(mc.screen);
                                    mc.setScreen(modsScreen);
                                }
                                catch(ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e)
                                {
                                    iChunUtil.LOGGER.error("Error creating FML Mods screen, please report this to iChun!", e);
                                }
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
