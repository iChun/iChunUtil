package us.ichun.mods.ichunutil.common.core.network;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;

import java.util.EnumMap;

public class PacketHandler
{
    public static void sendToAll(EnumMap<Side, FMLEmbeddedChannel> channels, AbstractPacket packet)
    {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
        channels.get(Side.SERVER).writeAndFlush(packet);
    }

    public static void sendToPlayer(EnumMap<Side, FMLEmbeddedChannel> channels, AbstractPacket packet, EntityPlayer player)
    {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        channels.get(Side.SERVER).writeAndFlush(packet);
    }

    public static void sendToAllAround(EnumMap<Side, FMLEmbeddedChannel> channels, AbstractPacket packet, NetworkRegistry.TargetPoint point)
    {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
        channels.get(Side.SERVER).writeAndFlush(packet);
    }

    public static void sendToDimension(EnumMap<Side, FMLEmbeddedChannel> channels, AbstractPacket packet, int dimension)
    {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimension);
        channels.get(Side.SERVER).writeAndFlush(packet);
    }

    public static void sendToServer(EnumMap<Side, FMLEmbeddedChannel> channels, AbstractPacket packet)
    {
        channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        channels.get(Side.CLIENT).writeAndFlush(packet);
    }

    public static void sendToAllExcept(EnumMap<Side, FMLEmbeddedChannel> channels, AbstractPacket packet, EntityPlayer player)
    {
        for(int i = 0; i < FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList.size(); i++)
        {
            EntityPlayer player1 = (EntityPlayer)FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList.get(i);

            if(player.getName().equalsIgnoreCase(player1.getName()))
            {
                continue;
            }

            PacketHandler.sendToPlayer(channels, packet, player1);
        }
    }
}
