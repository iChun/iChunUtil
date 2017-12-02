package me.ichun.mods.ichunutil.client.core.event;

import me.ichun.mods.ichunutil.client.core.EntityTrackerHandler;
import me.ichun.mods.ichunutil.client.entity.EntityLatchedRenderer;
import me.ichun.mods.ichunutil.client.gui.config.GuiConfigs;
import me.ichun.mods.ichunutil.client.keybind.KeyBind;
import me.ichun.mods.ichunutil.client.module.eula.GuiEulaNotifier;
import me.ichun.mods.ichunutil.client.module.patron.LayerPatronEffect;
import me.ichun.mods.ichunutil.client.module.patron.PatronEffectRenderer;
import me.ichun.mods.ichunutil.client.module.update.GuiUpdateNotifier;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import me.ichun.mods.ichunutil.client.render.entity.RenderLatchedRenderer;
import me.ichun.mods.ichunutil.client.render.item.ItemRenderingHelper;
import me.ichun.mods.ichunutil.client.render.world.RenderGlobalProxy;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.core.tracker.EntityTrackerRegistry;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.core.util.ObfHelper;
import me.ichun.mods.ichunutil.common.grab.GrabHandler;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.packet.mod.PacketPatronInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventHandlerClient
{
    public boolean hasShownFirstGui;
    public boolean connectingToServer;

    public int ticks;
    public float renderTick;
    public boolean hasScreen;

    public int screenWidth;
    public int screenHeight;

    public boolean mouseLeftDown;

    public ArrayList<KeyBind> keyBindList = new ArrayList<>();
    public HashMap<KeyBinding, KeyBind> mcKeyBindList = new HashMap<>();

    protected WorldClient renderGlobalWorldInstance;

    //Module stuff

    //Patron module
    public boolean patronUpdateServerAsPatron;

    //End Module Stuff

    public EventHandlerClient()
    {
        Minecraft mc = Minecraft.getMinecraft();
        screenWidth = mc.displayWidth;
        screenHeight = mc.displayHeight;

        EntityTrackerHandler.init();
    }

    @SubscribeEvent
    public void onRendererSafeCompatibility(RendererSafeCompatibilityEvent event)
    {
        for(Map.Entry<String, RenderPlayer> e : Minecraft.getMinecraft().getRenderManager().skinMap.entrySet())
        {
            e.getValue().addLayer(new LayerPatronEffect(e.getValue()));
        }
        GuiEulaNotifier.createIfRequired();
    }

    @SubscribeEvent
    public void onModelRegistry(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(iChunUtil.blockCompactPorkchop), 0, new ModelResourceLocation("ichunutil:compact_porkchop", "inventory"));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerRenderPre(RenderPlayerEvent.Pre event)
    {
        PatronEffectRenderer.onPlayerRenderPre(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerRenderPost(RenderPlayerEvent.Post event)
    {
        PatronEffectRenderer.onPlayerRenderPost(event);
    }

    @SubscribeEvent
    public void onRenderSpecificHand(RenderSpecificHandEvent event)
    {
        ItemRenderingHelper.onRenderSpecificHand(event);
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

            if(renderGlobalWorldInstance != mc.renderGlobal.world) //Assume world has changed, eg changing dimension or loading an MC world.
            {
                renderGlobalWorldInstance = mc.renderGlobal.world;

                for(RenderGlobalProxy proxy : RendererHelper.renderGlobalProxies)
                {
                    if(!proxy.released)
                    {
                        proxy.setWorldAndLoadRenderers(renderGlobalWorldInstance);
                    }
                }
            }

            ItemRenderingHelper.handlePreRender(mc);

            EntityTrackerHandler.onRenderTickStart(event);
        }
        else
        {
            ScaledResolution reso = new ScaledResolution(mc);
            GuiUpdateNotifier.update();
            GuiEulaNotifier.update();

            if(mc.currentScreen instanceof GuiControls && !keyBindList.isEmpty())
            {
                String s = I18n.translateToLocal("ichunutil.config.controls.moreKeys");
                int width = Math.round(mc.fontRenderer.getStringWidth(s) / 2F);
                GlStateManager.pushMatrix();
                GlStateManager.translate(reso.getScaledWidth() - width - 2, (reso.getScaledHeight() - (mc.fontRenderer.FONT_HEIGHT / 2D) - 2), 0);
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
                mc.fontRenderer.drawString(s, 0, 0, 0xffffff, true);
                GlStateManager.popMatrix();

                int i = Mouse.getX() * reso.getScaledWidth() / mc.displayWidth;
                int j = reso.getScaledHeight() - Mouse.getY() * reso.getScaledHeight() / mc.displayHeight - 1;

                if(!mouseLeftDown && Mouse.isButtonDown(0) && i >= (reso.getScaledWidth() - width - 2) && i <= reso.getScaledWidth() && j >= (reso.getScaledHeight() - mc.fontRenderer.FONT_HEIGHT - 2) && j <= reso.getScaledHeight())
                {
                    mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    FMLClientHandler.instance().showGuiScreen(new GuiConfigs(mc.currentScreen));
                }

                mouseLeftDown = Mouse.isButtonDown(0);
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        if(event.phase.equals(TickEvent.Phase.END))
        {
            if(mc.world != null)
            {
                if(connectingToServer)
                {
                    connectingToServer = false;
                    MinecraftForge.EVENT_BUS.post(new ServerPacketableEvent());
                }
                if(patronUpdateServerAsPatron)
                {
                    patronUpdateServerAsPatron = false;
                    iChunUtil.channel.sendToServer(new PacketPatronInfo(iChunUtil.proxy.getPlayerId(), iChunUtil.config.patronRewardType, iChunUtil.config.showPatronReward == 1));
                }
                for(KeyBind bind : keyBindList)
                {
                    bind.tick();
                }
                for(Map.Entry<KeyBinding, KeyBind> e : mcKeyBindList.entrySet())
                {
                    if(e.getValue().keyIndex != e.getKey().getKeyCode())
                    {
                        e.setValue(new KeyBind(e.getKey().getKeyCode()));
                    }
                    e.getValue().tick();
                }
                hasScreen = mc.currentScreen != null;

                if(!mc.isGamePaused())
                {
                    EntityTrackerHandler.tick();

                    GrabHandler.tick(Side.CLIENT);

                    if(!ObfHelper.obfuscated() && Minecraft.getMinecraft().getSession().getProfile().getName().equals("iChun") && mc.player.isElytraFlying() && mc.gameSettings.keyBindJump.isKeyDown())
                    {
                        mc.player.motionY += 0.05F;
                    }
                }
            }
            ticks++;
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.side.isClient() && event.phase == TickEvent.Phase.END)
        {
            Minecraft mc = Minecraft.getMinecraft();

            ItemRenderingHelper.handlePlayerTick(mc, event.player);
        }
    }

    @SubscribeEvent
    public void onClientConnection(FMLNetworkEvent.ClientConnectedToServerEvent event)
    {
        connectingToServer = true;

        if(iChunUtil.userIsPatron)
        {
            patronUpdateServerAsPatron = true;
        }

        for(ConfigBase conf : ConfigHandler.configs)
        {
            conf.storeSession();
        }
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        IThreadListener thread = Minecraft.getMinecraft();
        if(thread.isCallingFromMinecraftThread())
        {
            onClientDisconnect();
        }
        else
        {
            thread.addScheduledTask(this::onClientDisconnect);
        }
    }

    public void onClientDisconnect()
    {
        EntityTrackerHandler.onClientDisconnect();
        PatronEffectRenderer.onClientDisconnect();

        GrabHandler.grabbedEntities.get(Side.CLIENT).clear();

        for(ConfigBase conf : ConfigHandler.configs)
        {
            conf.resetSession();
        }

        EntityHelper.profileCache = null;
        EntityHelper.sessionService = null;
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

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        if(event.getWorld().isRemote)
        {
            for(GrabHandler handler : GrabHandler.grabbedEntities.get(Side.CLIENT))
            {
                handler.grabber = null;
                handler.grabbed = null;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntitySpawn(EntityJoinWorldEvent event)
    {
        EntityTrackerHandler.onEntitySpawn(event);
    }

    @SubscribeEvent
    public void onLatchedRendererUpdate(EntityLatchedRenderer.EntityLatchedRendererUpdateEvent event)
    {
        EntityTrackerHandler.onLatchedRendererUpdate(event); //patron updateWorldPortal is there
    }

    @SubscribeEvent
    public void onLatchedRendererRender(RenderLatchedRenderer.RenderLatchedRendererEvent event)
    {
        EntityTrackerHandler.onLatchedRendererRender(event); //patron render is there
    }

    @Deprecated //Use EntityTrackerHandler instead;
    public EntityTrackerRegistry getEntityTrackerRegistry()
    {
        return EntityTrackerHandler.getEntityTrackerRegistry();
    }

    public WorldClient getRenderGlobalWorldInstance()
    {
        return renderGlobalWorldInstance;
    }

    //I'm lazy okay?
    @SubscribeEvent
    public void onGuiActionPerformed(GuiScreenEvent.ActionPerformedEvent.Pre event)
    {
        if(!ObfHelper.obfuscated() && Minecraft.getMinecraft().getSession().getProfile().getName().equals("iChun") && (event.getGui().getClass() == GuiIngameMenu.class && event.getButton().id == 12 || event.getGui().getClass() == GuiMainMenu.class && event.getButton().id == 6) && !GuiScreen.isShiftKeyDown())
        {
            event.setCanceled(true);

            Minecraft.getMinecraft().displayGuiScreen(new GuiConfigs(Minecraft.getMinecraft().currentScreen));
        }
    }
}
