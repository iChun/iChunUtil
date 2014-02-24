package ichun.client.core;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import ichun.client.gui.config.GuiConfigBase;
import ichun.client.gui.config.GuiConfigSetter;
import ichun.core.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public class TickHandlerClient
{

    @SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            if(!ConfigHandler.configs.isEmpty() && Minecraft.getMinecraft().currentScreen instanceof GuiOptions && !(Minecraft.getMinecraft().currentScreen instanceof GuiConfigBase) && !(Minecraft.getMinecraft().currentScreen instanceof GuiConfigSetter))
            {
                GuiOptions gui = (GuiOptions)Minecraft.getMinecraft().currentScreen;
                String s = "Hit O to view more options";
                gui.drawString(Minecraft.getMinecraft().fontRenderer, s, gui.width - Minecraft.getMinecraft().fontRenderer.getStringWidth(s) - 2, gui.height - 10, 16777215);

                //TODO see if this keybind can be replaced.
                if(!optionsKeyDown && Keyboard.isKeyDown(Keyboard.KEY_O))
                {
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
                    FMLClientHandler.instance().showGuiScreen(new GuiConfigBase(gui, Minecraft.getMinecraft().gameSettings, null));
                }

                optionsKeyDown = Keyboard.isKeyDown(Keyboard.KEY_O);
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
            else if(worldInstance != null)
            {
                resetWorld();
            }
        }
    }

    public void worldTick(Minecraft mc, WorldClient world)
    {
    	if(worldInstance != world)
    	{
    		worldInstance = world;
    		
            if (renderGlobalProxy == null)
            {
                renderGlobalProxy = new RenderGlobalProxy(mc);
            }
            
            renderGlobalProxy.setWorldAndLoadRenderers(world);
            
            if(iconRegister != null)
            {
            	renderGlobalProxy.registerDestroyBlockIcons(iconRegister);
            	iconRegister = null;
            }
    	}
    }
    

    public void resetWorld()
    {
        worldInstance = null;
        renderGlobalProxy.setWorldAndLoadRenderers(null);

//        if (sneakyProxy instanceof RenderGlobal && !(sneakyProxy instanceof RenderGlobalProxy))
//        {
//            sneakyProxy.setWorldAndLoadRenderers(null);
//            RenderGlobal temp = Minecraft.getMinecraft().renderGlobal;
//            Minecraft.getMinecraft().renderGlobal = sneakyProxy;
//
//            if (temp instanceof RenderGlobalProxy)
//            {
//                sneakyProxy = temp;
//                sneakyProxy.setWorldAndLoadRenderers(null);
//            }
//            else
//            {
//                sneakyProxy = null;
//            }
//        }
    }
	
    public WorldClient worldInstance;
    public RenderGlobalProxy renderGlobalProxy;

    public IIconRegister iconRegister;
    
    public boolean optionsKeyDown;
}
