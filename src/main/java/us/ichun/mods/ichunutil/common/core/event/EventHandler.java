package us.ichun.mods.ichunutil.common.core.event;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import us.ichun.mods.ichunutil.client.thread.ThreadStatistics;
import us.ichun.mods.ichunutil.common.core.config.ConfigBase;
import us.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import us.ichun.mods.ichunutil.common.core.packet.mod.PacketPatientData;
import us.ichun.mods.ichunutil.common.core.packet.mod.PacketPatrons;
import us.ichun.mods.ichunutil.common.core.updateChecker.PacketModsList;
import us.ichun.mods.ichunutil.common.grab.GrabHandler;
import us.ichun.mods.ichunutil.common.iChunUtil;

public class EventHandler
{
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

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event)
    {
        if(event.world.isRemote)
        {
            iChunUtil.proxy.effectTicker.streaks.clear();
        }
    }
}
