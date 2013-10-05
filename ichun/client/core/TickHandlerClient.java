package ichun.client.core;

import ichun.client.gui.config.GuiConfigBase;
import ichun.client.gui.config.GuiConfigSetter;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TickHandlerClient implements ITickHandler
{

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
        if (type.equals(EnumSet.of(TickType.RENDER)))
        {
            if (Minecraft.getMinecraft().theWorld != null)
            {
                preRenderTick(Minecraft.getMinecraft(), Minecraft.getMinecraft().theWorld, (Float)tickData[0]); //only ingame
            }
        }
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
        if (type.equals(EnumSet.of(TickType.CLIENT)))
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
        else if (type.equals(EnumSet.of(TickType.PLAYER)))
        {
            playerTick((World)((EntityPlayer)tickData[0]).worldObj, (EntityPlayer)tickData[0]);
        }
        else if (type.equals(EnumSet.of(TickType.RENDER)))
        {
            if (Minecraft.getMinecraft().theWorld != null)
            {
                renderTick(Minecraft.getMinecraft(), Minecraft.getMinecraft().theWorld, (Float)tickData[0]); //only ingame
            }
            if(Minecraft.getMinecraft().currentScreen instanceof GuiOptions && !(Minecraft.getMinecraft().currentScreen instanceof GuiConfigBase) && !(Minecraft.getMinecraft().currentScreen instanceof GuiConfigSetter))
            {
            	GuiOptions gui = (GuiOptions)Minecraft.getMinecraft().currentScreen;
            	String s = "Hit O to view more options";
            	gui.drawString(Minecraft.getMinecraft().fontRenderer, s, gui.width - Minecraft.getMinecraft().fontRenderer.getStringWidth(s) - 2, gui.height - 10, 16777215);
            	
            	if(!optionsKeyDown && Keyboard.isKeyDown(Keyboard.KEY_O))
            	{
            		Minecraft.getMinecraft().sndManager.playSoundFX("random.click", 1.0F, 1.0F);
            		FMLClientHandler.instance().showGuiScreen(new GuiConfigBase(gui, Minecraft.getMinecraft().gameSettings, null));
            	}
            	
                optionsKeyDown = Keyboard.isKeyDown(Keyboard.KEY_O);
            }
        }
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.CLIENT, TickType.PLAYER, TickType.RENDER);
    }

    @Override
    public String getLabel()
    {
        return "TickHandlerClient_iChunUtil";
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
    
    public void playerTick(World world, EntityPlayer player)
    {
    }

    public void preRenderTick(Minecraft mc, World world, float renderTick)
    {
    }
    
    public void renderTick(Minecraft mc, World world, float renderTick)
    {
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

    public IconRegister iconRegister;
    
    public boolean optionsKeyDown;
}
