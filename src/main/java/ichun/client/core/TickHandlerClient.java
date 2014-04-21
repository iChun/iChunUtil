package ichun.client.core;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import ichun.client.gui.config.GuiConfigBase;
import ichun.client.gui.config.GuiConfigSetter;
import ichun.client.keybind.KeyBind;
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

//TODO check all the tick handlers for world and player ticks to make sure that the side is only called on server/client. Done: BackTools
public class TickHandlerClient
{

    @SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            Minecraft mc = Minecraft.getMinecraft();
            if(!ConfigHandler.configs.isEmpty() && mc.currentScreen instanceof GuiOptions && !(mc.currentScreen instanceof GuiConfigBase) && !(mc.currentScreen instanceof GuiConfigSetter))
            {
                GuiOptions gui = (GuiOptions)mc.currentScreen;
                String s = "Hit O to view more options";
                gui.drawString(mc.fontRenderer, s, gui.width - mc.fontRenderer.getStringWidth(s) - 2, gui.height - 10, 16777215);

                //TODO see if this keybind can be replaced.
                if(!optionsKeyDown && Keyboard.isKeyDown(Keyboard.KEY_O))
                {
                    mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
                    FMLClientHandler.instance().showGuiScreen(new GuiConfigBase(gui, mc.gameSettings, null));
                }

                optionsKeyDown = Keyboard.isKeyDown(Keyboard.KEY_O);
            }
            if(mc.theWorld != null)
            {
                /******************/

                                //Basic stencil test
//                                ScaledResolution reso1 = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
//
//                                GL11.glEnable(GL11.GL_STENCIL_TEST);
//
//                                GL11.glColorMask(false, false, false, false);
//                                GL11.glDepthMask(false);
//
//                                GL11.glStencilFunc(GL11.GL_NEVER, 1, 0xFF);
//                                GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_KEEP);
//
//                                GL11.glStencilMask(0xFF);
//                                GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
//
//                                RendererHelper.drawColourOnScreen(0xffffff, 255, 0, 0, 60, 60, 0);
//
//                                GL11.glColorMask(true, true, true, true);
//                                GL11.glDepthMask(true);
//
//                                GL11.glStencilMask(0x00);
//
//                                GL11.glStencilFunc(GL11.GL_EQUAL, 0, 0xFF);
//
//                                GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
//
//                                RendererHelper.drawColourOnScreen(0xffffff, 255, 0, 0, reso1.getScaledWidth_double(), reso1.getScaledHeight_double(), 0);
//
//                                GL11.glDisable(GL11.GL_STENCIL_TEST);


                /******************/

                // Basic scissor test

//                ScaledResolution reso1 = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
//
//                RendererHelper.startGlScissor(reso1.getScaledWidth() / 2 - 50, reso1.getScaledHeight() / 2 - 50, 100, 100);
//
//                GL11.glPushMatrix();
//
//                GL11.glTranslatef(-15F, 15F, 0F);
//
//                RendererHelper.drawColourOnScreen(0xffffff, 255, 0, 0, reso1.getScaledWidth_double(), reso1.getScaledHeight_double(), 0);
//
//                GL11.glPopMatrix();;
//
//                RendererHelper.endGlScissor();

                /*****************/
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

    public ArrayList<KeyBind> keyBindList = new ArrayList<KeyBind>();
    public HashMap<KeyBinding, KeyBind> mcKeyBindList = new HashMap<KeyBinding, KeyBind>();
}
