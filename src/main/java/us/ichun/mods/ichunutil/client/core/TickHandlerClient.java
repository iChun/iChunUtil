package us.ichun.mods.ichunutil.client.core;

import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import us.ichun.mods.ichunutil.client.gui.GuiModUpdateNotification;
import us.ichun.mods.ichunutil.client.gui.config.GuiConfigBase;
import us.ichun.mods.ichunutil.client.keybind.KeyBind;
import us.ichun.mods.ichunutil.client.render.RendererHelper;
import us.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//TODO check all the tick handlers for world and player ticks to make sure that the side is only called on server/client.
public class TickHandlerClient
{

    @SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        if(event.phase == TickEvent.Phase.START)
        {
            if(screenWidth != mc.displayWidth || screenHeight != mc.displayHeight)
            {
                screenWidth = mc.displayWidth;
                screenHeight = mc.displayHeight;

                for(Framebuffer buffer : RendererHelper.frameBuffers)
                {
                    buffer.createBindFramebuffer(screenWidth, screenHeight);
                }
            }
        }
        else
        {
            if(!ConfigHandler.configs.isEmpty() && mc.currentScreen != null && mc.currentScreen.getClass().equals(GuiOptions.class))
            {
                GuiOptions gui = (GuiOptions)mc.currentScreen;
                String s = StatCollector.translateToLocalFormatted("ichun.gui.moreOptions", GameSettings.getKeyDisplayString(Keyboard.KEY_O));
                gui.drawString(mc.fontRendererObj, s, gui.width - mc.fontRendererObj.getStringWidth(s) - 2, gui.height - 10, 16777215);

                if(!optionsKeyDown && Keyboard.isKeyDown(Keyboard.KEY_O))
                {
                    mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
                    FMLClientHandler.instance().showGuiScreen(new GuiConfigBase(gui, mc.gameSettings, null));
                }

                optionsKeyDown = Keyboard.isKeyDown(Keyboard.KEY_O);
            }
            if(mc.theWorld != null)
            {
//                RendererHelper.renderTestStencil();
//                RendererHelper.renderTestSciccor();

                if(modUpdateNotification != null)
                {
                    modUpdateNotification.update();
                }
            }
        }
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            if (Minecraft.getMinecraft().theWorld != null)
            {
                worldTick(Minecraft.getMinecraft(), Minecraft.getMinecraft().theWorld);
            }
            //TODO remember to reset world in renderGlobalProxy
        }
    }

    public void worldTick(Minecraft mc, WorldClient world)
    {
        for(KeyBind bind : keyBindList)
        {
            bind.tick();
        }
        for(Map.Entry<KeyBinding, KeyBind> e : mcKeyBindList.entrySet())
        {
            if(e.getValue().keyIndex != e.getKey().getKeyCode())
            {
                e.setValue(new KeyBind(e.getKey().getKeyCode(), false, false, false, false));
            }
            e.getValue().tick();
        }
    }

    public GuiModUpdateNotification modUpdateNotification;
    
    public boolean optionsKeyDown;

    public int screenWidth = Minecraft.getMinecraft().displayWidth;
    public int screenHeight = Minecraft.getMinecraft().displayHeight;

    public ArrayList<KeyBind> keyBindList = new ArrayList<KeyBind>();
    public HashMap<KeyBinding, KeyBind> mcKeyBindList = new HashMap<KeyBinding, KeyBind>();
}
