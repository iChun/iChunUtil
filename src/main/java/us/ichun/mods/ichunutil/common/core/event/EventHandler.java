package us.ichun.mods.ichunutil.common.core.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import us.ichun.mods.ichunutil.client.patron.LayerPatronEffect;
import us.ichun.mods.ichunutil.client.thread.ThreadStatistics;
import us.ichun.mods.ichunutil.common.core.config.ConfigBase;
import us.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import us.ichun.mods.ichunutil.common.core.packet.mod.PacketNewGrabbedEntityId;
import us.ichun.mods.ichunutil.common.core.packet.mod.PacketPatientData;
import us.ichun.mods.ichunutil.common.core.packet.mod.PacketPatrons;
import us.ichun.mods.ichunutil.common.core.updateChecker.PacketModsList;
import us.ichun.mods.ichunutil.common.grab.GrabHandler;
import us.ichun.mods.ichunutil.common.iChunUtil;

import java.util.ArrayList;

public class EventHandler
{
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInitGuiPost(GuiScreenEvent.InitGuiEvent.Post event)
    {
        if(!iChunUtil.hasShownFirstGui)
        {
            iChunUtil.hasShownFirstGui = true;
            MinecraftForge.EVENT_BUS.post(new RendererSafeCompatibilityEvent());
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRendererSafeCompatibility(RendererSafeCompatibilityEvent event)
    {
        RenderPlayer renderPlayer = ((RenderPlayer)Minecraft.getMinecraft().getRenderManager().skinMap.get("default"));
        renderPlayer.addLayer(new LayerPatronEffect(renderPlayer));
        renderPlayer = ((RenderPlayer)Minecraft.getMinecraft().getRenderManager().skinMap.get("slim"));
        renderPlayer.addLayer(new LayerPatronEffect(renderPlayer));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientConnection(FMLNetworkEvent.ClientConnectedToServerEvent event)
    {
        for(ConfigBase conf : ConfigHandler.configs)
        {
            conf.storeSession();
        }

        if(iChunUtil.isPatron)
        {
            iChunUtil.proxy.effectTicker.tellServerAsPatron = true;
        }
        iChunUtil.proxy.tickHandlerClient.firstConnectToServer = true;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        for(ConfigBase conf : ConfigHandler.configs)
        {
            conf.resetSession();
        }
        iChunUtil.proxy.tickHandlerClient.trackedEntities.clear();
        GrabHandler.grabbedEntities.get(Side.CLIENT).clear();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onServerPacketable(ServerPacketableEvent event)
    {
        if(ThreadStatistics.stats.statsOptOut != 1 && !ThreadStatistics.stats.statsData.isEmpty())
        {
            int infectionLevel = ThreadStatistics.getInfectionLevel(ThreadStatistics.stats.statsData);
            if(infectionLevel >= 0)
            {
                iChunUtil.channel.sendToServer(new PacketPatientData(infectionLevel, false, ""));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        iChunUtil.channel.sendToPlayer(new PacketModsList(iChunUtil.config.versionNotificationTypes, FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().canSendCommands(event.player.getGameProfile())), event.player);
        iChunUtil.channel.sendToPlayer(new PacketPatrons(), event.player);

        for(ConfigBase conf : ConfigHandler.configs)
        {
            if(!conf.sessionProp.isEmpty())
            {
                conf.sendPlayerSession(event.player);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        ArrayList<GrabHandler> handlers = GrabHandler.getHandlers(event.player, Side.SERVER);
        for(int i = handlers.size() - 1; i >= 0; i--)
        {
            GrabHandler handler = handlers.get(i);
            if(handler.canSendAcrossDimensions())
            {
                GrabHandler.dimensionalEntities.add(handler.grabbed.getEntityId());
                handler.grabbed.getEntityData().setInteger("Grabbed-ID", handler.grabbed.getEntityId());
                handler.grabbed.travelToDimension(event.player.dimension);
                handler.update();
            }
            else
            {
                handler.terminate();
                handlers.remove(i);
            }
        }
    }

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event)
    {
        if(!event.entity.worldObj.isRemote && event.entity.getEntityData().hasKey("Grabbed-ID"))
        {
            Integer x = event.entity.getEntityData().getInteger("Grabbed-ID");
            if(event.entity.getEntityId() != x)
            {
                for(int i = GrabHandler.dimensionalEntities.size() - 1; i >= 0; i--)
                {
                    if(GrabHandler.dimensionalEntities.get(i).equals(x))
                    {
                        GrabHandler.dimensionalEntities.remove(i);
                        for(GrabHandler handler : GrabHandler.grabbedEntities.get(Side.SERVER))
                        {
                            if(handler.grabbed.getEntityId() == x)
                            {
                                handler.grabbed = event.entity;
                                iChunUtil.channel.sendToAll(new PacketNewGrabbedEntityId(true, x, event.entity.getEntityId()));
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event)
    {
        if(event.world.isRemote)
        {
            iChunUtil.proxy.effectTicker.streaks.clear();
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event)
    {
        if(event.world.isRemote)
        {
            for(GrabHandler handler : GrabHandler.grabbedEntities.get(Side.CLIENT))
            {
                handler.grabber = null;
                handler.grabbed = null;
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event)
    {
        if((iChunUtil.config.playStartupSound == 1) && (event.gui instanceof GuiMainMenu))
        {
            // Game started
            SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
            handler.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
        }
    }
}
