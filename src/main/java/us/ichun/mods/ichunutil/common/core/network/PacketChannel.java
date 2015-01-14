package us.ichun.mods.ichunutil.common.core.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.EnumMap;

public class PacketChannel
{
    public static ArrayList<PacketChannel> registeredChannels = new ArrayList<PacketChannel>();

    private final String modId;
    private final EnumMap<Side, FMLEmbeddedChannel> channels;
    public final EnumMap<Side, ArrayList<AbstractPacket>> queuedPackets = new EnumMap<Side, ArrayList<AbstractPacket>>(Side.class) {{ put(Side.CLIENT, new ArrayList<AbstractPacket>()); put(Side.SERVER, new ArrayList<AbstractPacket>()); }};

    public PacketChannel(String id, EnumMap<Side, FMLEmbeddedChannel> chans)
    {
        modId = id;
        channels = chans;
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
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
        channels.get(Side.SERVER).writeAndFlush(packet);
    }

    public void sendToPlayer(AbstractPacket packet, EntityPlayer player)
    {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        channels.get(Side.SERVER).writeAndFlush(packet);
    }

    public void sendToAllAround(AbstractPacket packet, NetworkRegistry.TargetPoint point)
    {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
        channels.get(Side.SERVER).writeAndFlush(packet);
    }

    public void sendToDimension(AbstractPacket packet, int dimension)
    {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimension);
        channels.get(Side.SERVER).writeAndFlush(packet);
    }

    public void sendToServer(AbstractPacket packet)
    {
        channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        channels.get(Side.CLIENT).writeAndFlush(packet);
    }

    public void sendToAllExcept(AbstractPacket packet, EntityPlayer player)
    {
        for(int i = 0; i < FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList.size(); i++)
        {
            EntityPlayer player1 = (EntityPlayer)FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList.get(i);

            if(player.getName().equalsIgnoreCase(player1.getName()))
            {
                continue;
            }

            sendToPlayer(packet, player1);
        }
    }
}
