package me.ichun.mods.ichunutil.client.core.event;

import me.ichun.mods.ichunutil.client.keybind.KeyBind;
import me.ichun.mods.ichunutil.client.module.eula.WindowAnnoy;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.commons.lang3.RandomStringUtils;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class EventHandlerClient
{
    public boolean hasShownFirstGui;
    public boolean connectingToServer;

    public int ticks;
    public float renderTick;

    public int screenWidth;
    public int screenHeight;

    public ArrayList<KeyBind> keyBindList = new ArrayList<KeyBind>();
    public HashMap<KeyBinding, KeyBind> mcKeyBindList = new HashMap<KeyBinding, KeyBind>();

    //Module stuff
    public boolean dingPlayedSound;

    public boolean eulaDrawEulaNotice = !iChunUtil.config.eulaAcknowledged.equals(RandomStringUtils.random(20, 32, 127, false, false, null, (new Random(Math.abs(Minecraft.getMinecraft().getSession().getPlayerID().replaceAll("-", "").hashCode() + (Math.abs("iChunUtilEULA".hashCode())))))));
    public WindowAnnoy eulaWindow = new WindowAnnoy();
    //End Module Stuff

    public EventHandlerClient()
    {
        Minecraft mc = Minecraft.getMinecraft();
        screenWidth = mc.displayWidth;
        screenHeight = mc.displayHeight;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        if(event.phase.equals(TickEvent.Phase.END))
        {
            if(mc.theWorld != null)
            {
                if(connectingToServer)
                {
                    connectingToServer = false;
                    MinecraftForge.EVENT_BUS.post(new ServerPacketableEvent());
                }
            }
            ticks++;
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();

        renderTick = event.renderTickTime;
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
            if(eulaDrawEulaNotice)
            {
                ScaledResolution reso = new ScaledResolution(mc);
                int i = Mouse.getX() * reso.getScaledWidth() / mc.displayWidth;
                int j = reso.getScaledHeight() - Mouse.getY() * reso.getScaledHeight() / mc.displayHeight - 1;

                eulaWindow.posX = reso.getScaledWidth() - eulaWindow.width;
                eulaWindow.posY = 0;
                if(eulaWindow.workspace.getFontRenderer() == null)
                {
                    eulaWindow.workspace.setWorldAndResolution(mc, mc.displayWidth, mc.displayHeight);
                    eulaWindow.workspace.initGui();
                }
                eulaWindow.draw(i - eulaWindow.posX, j - eulaWindow.posY);
                if(Mouse.isButtonDown(0))
                {
                    eulaWindow.onClick(i - eulaWindow.posX, j - eulaWindow.posY, 0);
                }
            }
        }
    }

    @SubscribeEvent
    public void onClientConnection(FMLNetworkEvent.ClientConnectedToServerEvent event)
    {
        connectingToServer = true;

        for(ConfigBase conf : ConfigHandler.configs)
        {
            conf.storeSession();
        }
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        for(ConfigBase conf : ConfigHandler.configs)
        {
            conf.resetSession();
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event)
    {
        if(event.gui instanceof GuiMainMenu && !dingPlayedSound)
        {
            dingPlayedSound = true;
            if(iChunUtil.config.dingEnabled == 1 && !Loader.isModLoaded("Ding"))
            {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation(iChunUtil.config.dingSoundName), (iChunUtil.config.dingSoundPitch / 100F)));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInitGuiPost(GuiScreenEvent.InitGuiEvent.Post event)
    {
        if(!hasShownFirstGui)
        {
            hasShownFirstGui = true;
            MinecraftForge.EVENT_BUS.post(new RendererSafeCompatibilityEvent());
        }
    }
}
