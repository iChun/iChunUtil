package ichun.client.core;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import ichun.client.gui.GuiModUpdateNotification;
import ichun.client.gui.config.GuiConfigBase;
import ichun.client.gui.config.GuiConfigSetter;
import ichun.client.keybind.KeyBind;
import ichun.client.render.RendererHelper;
import ichun.common.core.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
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
        if(event.phase == TickEvent.Phase.END)
        {
            Minecraft mc = Minecraft.getMinecraft();
            if(!ConfigHandler.configs.isEmpty() && mc.currentScreen != null && mc.currentScreen.getClass().equals(GuiOptions.class))
            {
                GuiOptions gui = (GuiOptions)mc.currentScreen;
                String s = "Hit O to view more options";
                gui.drawString(mc.fontRenderer, s, gui.width - mc.fontRenderer.getStringWidth(s) - 2, gui.height - 10, 16777215);

                if(!optionsKeyDown && Keyboard.isKeyDown(Keyboard.KEY_O))
                {
                    mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
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
    
    public IIconRegister iconRegister;
    
    public boolean optionsKeyDown;

    public ArrayList<KeyBind> keyBindList = new ArrayList<KeyBind>();
    public HashMap<KeyBinding, KeyBind> mcKeyBindList = new HashMap<KeyBinding, KeyBind>();
}
