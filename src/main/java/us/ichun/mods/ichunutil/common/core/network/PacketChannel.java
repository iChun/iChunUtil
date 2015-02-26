package us.ichun.mods.ichunutil.common.core.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class PacketChannel
{
    public static ArrayList<PacketChannel> registeredChannels = new ArrayList<PacketChannel>();

    private final String modId;
    private final EnumMap<Side, FMLEmbeddedChannel> channels;
    private final Class<? extends AbstractPacket>[] packetTypes;
    public final Map<Side, ArrayList<AbstractPacket>> queuedPackets = Collections.synchronizedMap(new EnumMap<Side, ArrayList<AbstractPacket>>(Side.class) {{ put(Side.CLIENT, new ArrayList<AbstractPacket>()); put(Side.SERVER, new ArrayList<AbstractPacket>()); }});

    protected PacketChannel(String id, EnumMap<Side, FMLEmbeddedChannel> chans, Class<? extends AbstractPacket>[] types)
    {
        modId = id;
        channels = chans;
        packetTypes = types;
    }

    public EnumMap<Side, FMLEmbeddedChannel> getChannels()
    {
        return channels;
    }

    public String getModId()
    {
        return modId;
    }

    public void sendToAll(AbstractPacket packet)
    {
        validatePacket(packet);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
        channels.get(Side.SERVER).writeAndFlush(packet);
    }

    public void sendToPlayer(AbstractPacket packet, EntityPlayer player)
    {
        validatePacket(packet);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        channels.get(Side.SERVER).writeAndFlush(packet);
    }

    public void sendToAllAround(AbstractPacket packet, NetworkRegistry.TargetPoint point)
    {
        validatePacket(packet);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
        channels.get(Side.SERVER).writeAndFlush(packet);
    }

    public void sendToDimension(AbstractPacket packet, int dimension)
    {
        validatePacket(packet);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimension);
        channels.get(Side.SERVER).writeAndFlush(packet);
    }

    public void sendToServer(AbstractPacket packet)
    {
        validatePacket(packet);
        channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        channels.get(Side.CLIENT).writeAndFlush(packet);
    }

    public void sendToAllExcept(AbstractPacket packet, EntityPlayer player)
    {
        validatePacket(packet);
        for(int i = 0; i < FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList.size(); i++)
        {
            EntityPlayer player1 = (EntityPlayer)FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList.get(i);

            if(player.getCommandSenderName().equalsIgnoreCase(player1.getCommandSenderName()))
            {
                continue;
            }

            sendToPlayer(packet, player1);
        }
    }

    private void validatePacket(AbstractPacket packet)
    {
        boolean has = false;
        Class<? extends  AbstractPacket> clz = packet.getClass();
        for(Class<? extends AbstractPacket> type : packetTypes)
        {
            if(type == clz)
            {
                has = true;
                break;
            }
        }
        if(!has)
        {
            throw new RuntimeException("Sending a packet that is not registered!");
        }
    }
}
