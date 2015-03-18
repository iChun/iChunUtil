package us.ichun.mods.ichunutil.common.core;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import us.ichun.mods.ichunutil.common.core.config.ConfigBase;
import us.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import us.ichun.mods.ichunutil.common.core.packet.mod.PacketPatrons;
import us.ichun.mods.ichunutil.common.core.updateChecker.PacketModsList;
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
            iChunUtil.proxy.trailTicker.tellServerAsPatron = true;
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
    }

    @SubscribeEvent
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
}
