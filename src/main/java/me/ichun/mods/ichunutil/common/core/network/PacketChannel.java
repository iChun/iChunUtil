package me.ichun.mods.ichunutil.common.core.network;

import com.google.common.base.Throwables;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ReportedException;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLIndexedMessageToMessageCodec;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumMap;

/**
 *
 * Modification of SimpleNetworkWrapper from FML done by cpw.
 *
 * @author cpw, iChun
 *
 */
public class PacketChannel
{
    private EnumMap<Side, FMLEmbeddedChannel> channels;
    private AbstractPacketCodec packetCodec;
    private static Class<?> defaultChannelPipeline;
    private static Method generateName;
    {
        try
        {
            defaultChannelPipeline = Class.forName("io.netty.channel.DefaultChannelPipeline");
            generateName = defaultChannelPipeline.getDeclaredMethod("generateName", ChannelHandler.class);
            generateName.setAccessible(true);
        }
        catch (Exception e)
        {
            // How is this possible?
            FMLLog.log(Level.FATAL, e, "What? Netty isn't installed, what magic is this?");
            throw Throwables.propagate(e);
        }
    }

    public class AbstractPacketCodec extends FMLIndexedMessageToMessageCodec<AbstractPacket>
    {
        @Override
        public void encodeInto(ChannelHandlerContext ctx, AbstractPacket msg, ByteBuf target) throws Exception
        {
            msg.writeTo(target);
        }

        @Override
        public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, AbstractPacket msg)
        {
            msg.readFrom(source);
        }
    }


    private String generateName(ChannelPipeline pipeline, ChannelHandler handler)
    {
        try
        {
            return (String)generateName.invoke(defaultChannelPipeline.cast(pipeline), handler);
        }
        catch (Exception e)
        {
            FMLLog.log(Level.FATAL, e, "It appears we somehow have a not-standard pipeline. Huh");
            throw Throwables.propagate(e);
        }
    }

    public PacketChannel(String modId, Class<? extends AbstractPacket>...packetTypes)
    {
        if(packetTypes.length == 0)
        {
            throw new ReportedException(new CrashReport("Mod " + modId + " is not registering any packets with its channel handlers.", new Throwable()));
        }

        packetCodec = new AbstractPacketCodec();
        channels = NetworkRegistry.INSTANCE.newChannel(modId, packetCodec);

        ArrayList<Class<? extends AbstractPacket>> list = new ArrayList<Class<? extends AbstractPacket>>();
        for(Class<? extends AbstractPacket> packetType : packetTypes)
        {
            if(!list.contains(packetType))
            {
                list.add(packetType);
            }
            else
            {
                iChunUtil.LOGGER.warn("Channel " + modId + " is reregistering packet types!");
            }
        }

        for(int i = 0; i < list.size(); i++)
        {
            registerPacket(modId, i, list.get(i));
        }
    }

    public void registerPacket(String modId, int i, Class<? extends AbstractPacket> packetClass)
    {
        packetCodec.addDiscriminator(i, packetClass);
        try
        {
            Side side = packetClass.newInstance().receivingSide();
            if(side == null)
            {
                FMLEmbeddedChannel channelServer = channels.get(Side.SERVER);
                String typeServer = channelServer.findChannelHandlerNameForType(AbstractPacketCodec.class);
                PacketHandlerWrapper handlerServer = new PacketHandlerWrapper(Side.SERVER, packetClass);
                channelServer.pipeline().addAfter(typeServer, generateName(channelServer.pipeline(), handlerServer), handlerServer);

                FMLEmbeddedChannel channelClient = channels.get(Side.CLIENT);
                String typeClient = channelClient.findChannelHandlerNameForType(AbstractPacketCodec.class);
                PacketHandlerWrapper handlerClient = new PacketHandlerWrapper(Side.CLIENT, packetClass);
                channelClient.pipeline().addAfter(typeClient, generateName(channelClient.pipeline(), handlerClient), handlerClient);
            }
            else
            {
                FMLEmbeddedChannel channel = channels.get(side);
                String type = channel.findChannelHandlerNameForType(AbstractPacketCodec.class);
                PacketHandlerWrapper handler = new PacketHandlerWrapper(side, packetClass);
                channel.pipeline().addAfter(type, generateName(channel.pipeline(), handler), handler);
            }
        }
        catch (Exception e)
        {
            iChunUtil.LOGGER.warn("Could not create packet for class " + packetClass.getName() + " for channel " + modId + ". Is there a default constructor in that class?" );
            throw Throwables.propagate(e);
        }
    }

    /**
     * Construct a minecraft packet from the supplied message. Can be used where minecraft packets are required, such as
     * {@link TileEntity#getDescriptionPacket}.
     *
     * @param message The message to translate into packet form
     * @return A minecraft {@link Packet} suitable for use in minecraft APIs
     */
    public Packet<?> getPacketFrom(AbstractPacket message)
    {
        return channels.get(Side.SERVER).generatePacketFrom(message);
    }

    public void sendToAllExcept(AbstractPacket packet, EntityPlayer player)
    {
        for(int i = 0; i < FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerList().size(); i++)
        {
            EntityPlayerMP player1 = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerList().get(i);
            if(player.getName().equalsIgnoreCase(player1.getName()))
            {
                continue;
            }
            sendTo(packet, player1);
        }
    }

    public void sendToAll(AbstractPacket message)
    {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
        channels.get(Side.SERVER).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendTo(AbstractPacket message, EntityPlayer player)
    {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        channels.get(Side.SERVER).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendToAllAround(AbstractPacket message, TargetPoint point)
    {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
        channels.get(Side.SERVER).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendToDimension(AbstractPacket message, int dimensionId)
    {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimensionId);
        channels.get(Side.SERVER).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendToServer(AbstractPacket message)
    {
        channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        channels.get(Side.CLIENT).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }
}