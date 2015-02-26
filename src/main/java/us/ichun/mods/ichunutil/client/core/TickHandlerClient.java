package us.ichun.mods.ichunutil.client.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import us.ichun.mods.ichunutil.client.gui.GuiModUpdateNotification;
import us.ichun.mods.ichunutil.client.gui.config.GuiConfigs;
import us.ichun.mods.ichunutil.client.keybind.KeyBind;
import us.ichun.mods.ichunutil.client.render.RendererHelper;
import us.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import us.ichun.mods.ichunutil.common.tracker.EntityInfo;
import us.ichun.mods.ichunutil.common.tracker.IAdditionalTrackerInfo;
import us.ichun.mods.ichunutil.common.tracker.TrackerRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//TODO check all the tick handlers for world and player ticks to make sure that the side is only called on server/client.
public class TickHandlerClient
{
    public TickHandlerClient()
    {
        buttonDummy = new GuiButton(0, 0, 0, "");
    }

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
                ScaledResolution reso = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
                GuiOptions gui = (GuiOptions)mc.currentScreen;
                String s = StatCollector.translateToLocalFormatted("ichun.gui.moreOptions", GameSettings.getKeyDisplayString(Keyboard.KEY_O));
                int width = mc.fontRendererObj.getStringWidth(s);
                int i = Mouse.getX() * reso.getScaledWidth() / mc.displayWidth;
                int j = reso.getScaledHeight() - Mouse.getY() * reso.getScaledHeight() / mc.displayHeight - 1;
                buttonDummy.xPosition = reso.getScaledWidth() - (width + 5);
                buttonDummy.yPosition = reso.getScaledHeight() - (mc.fontRendererObj.FONT_HEIGHT + 4);
                buttonDummy.width = width + 40;
                buttonDummy.height = 20;
                buttonDummy.drawButton(mc, 0, 0);
                gui.drawString(mc.fontRendererObj, s, gui.width - mc.fontRendererObj.getStringWidth(s) - 2, gui.height - 10, 16777215);

                if(!mouseLeftDown && Mouse.isButtonDown(0) && i >= buttonDummy.xPosition && i <= reso.getScaledWidth() && j >= buttonDummy.yPosition && j <= reso.getScaledHeight() || !optionsKeyDown && Keyboard.isKeyDown(Keyboard.KEY_O))
                {
                    buttonDummy.playPressSound(mc.getSoundHandler());
                    int oriScale = mc.gameSettings.guiScale;
                    mc.gameSettings.guiScale = 2;
                    //                    FMLClientHandler.instance().showGuiScreen(new GuiConfigBase(gui, mc.gameSettings, null));
                    FMLClientHandler.instance().showGuiScreen(new GuiConfigs(oriScale, mc.currentScreen));
                }

                mouseLeftDown = Mouse.isButtonDown(0);
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
    public void playerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.side.isClient() && event.phase.equals(TickEvent.Phase.END))
        {
            for(TrackerRegistry reg : trackedEntities)
            {
                if(reg.type.equals(TrackerRegistry.EnumTrackerType.PERSISTENT_PLAYER) && reg.entityToTrack.getCommandSenderName().equals(event.player.getCommandSenderName()))
                {
                    reg.entityToTrack = event.player;
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
        for(int i = trackedEntities.size() - 1; i >= 0; i--)
        {
            TrackerRegistry reg = trackedEntities.get(i);
            if(!reg.update())
            {
                trackedEntities.remove(i);
            }
        }
    }

    public ArrayList<EntityInfo> getOrRegisterEntityTracker(EntityLivingBase ent, int length, Class<? extends IAdditionalTrackerInfo> additionalInfo, boolean persist)
    {
        ArrayList<EntityInfo> info = null;

        //Check if the Tracker already exists
        for(TrackerRegistry reg : trackedEntities)
        {
            if(reg.entityToTrack == ent || reg.type.equals(TrackerRegistry.EnumTrackerType.PERSISTENT_PLAYER) && reg.entityToTrack.getCommandSenderName().equals(ent.getCommandSenderName()) && ent instanceof EntityPlayer)
            {
                if(length > reg.length)
                {
                    reg.length = length;
                }
                reg.addTracker(additionalInfo);
                info = reg.trackedInfo;
                break;
            }
        }

        //Create tracker if it doesn't exist;
        if(info == null)
        {
            TrackerRegistry reg = (new TrackerRegistry(persist && ent instanceof EntityPlayer ? TrackerRegistry.EnumTrackerType.PERSISTENT_PLAYER : TrackerRegistry.EnumTrackerType.SPECIFIC, ent, length)).addTracker(additionalInfo);
            trackedEntities.add(reg);
            info = reg.trackedInfo;
        }

        return info;
    }

    public GuiModUpdateNotification modUpdateNotification;

    public boolean optionsKeyDown;
    public boolean mouseLeftDown;
    public GuiButton buttonDummy;

    public int screenWidth = Minecraft.getMinecraft().displayWidth;
    public int screenHeight = Minecraft.getMinecraft().displayHeight;

    public ArrayList<KeyBind> keyBindList = new ArrayList<KeyBind>();
    public HashMap<KeyBinding, KeyBind> mcKeyBindList = new HashMap<KeyBinding, KeyBind>();

    public ArrayList<TrackerRegistry> trackedEntities = new ArrayList<TrackerRegistry>();
}
