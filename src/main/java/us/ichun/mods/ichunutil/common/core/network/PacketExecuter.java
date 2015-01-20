package us.ichun.mods.ichunutil.common.core.network;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketExecuter
{
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event)
    {
        if(event.phase == TickEvent.Phase.START)
        {
            for(PacketChannel channel : PacketChannel.registeredChannels)
            {
                synchronized(channel.queuedPackets)
                {
                    for(int i = 0; i< channel.queuedPackets.get(Side.SERVER).size(); i++)
                    {
                        AbstractPacket packet = channel.queuedPackets.get(Side.SERVER).get(i);
                        packet.execute(Side.SERVER, packet.playerServer);
                    }
                    channel.queuedPackets.get(Side.SERVER).clear();
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientRenderTick(TickEvent.RenderTickEvent event)
    {
        if(event.phase == TickEvent.Phase.START)
        {
            for(PacketChannel channel : PacketChannel.registeredChannels)
            {
                synchronized(channel.queuedPackets)
                {
                    for(int i = 0; i< channel.queuedPackets.get(Side.CLIENT).size(); i++)
                    {
                        AbstractPacket packet = channel.queuedPackets.get(Side.CLIENT).get(i);
                        packet.execute(Side.CLIENT, packet.playerClient);
                    }
                    channel.queuedPackets.get(Side.CLIENT).clear();
                }
            }
        }
    }
}
