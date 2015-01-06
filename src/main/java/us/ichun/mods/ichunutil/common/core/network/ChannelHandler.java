package us.ichun.mods.ichunutil.common.core.network;

import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLIndexedMessageToMessageCodec;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import us.ichun.mods.ichunutil.common.iChunUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.ReportedException;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

public class ChannelHandler extends FMLIndexedMessageToMessageCodec<AbstractPacket>
{
    public final String channel;

    private ChannelHandler(String s, Class<? extends AbstractPacket>...packetTypes)
    {
        channel = s;
        ArrayList<Class<? extends AbstractPacket>> list = new ArrayList<Class<? extends AbstractPacket>>();
        for(int i = 0; i < packetTypes.length; i++)
        {
            if(!list.contains(packetTypes[i]))
            {
                list.add(packetTypes[i]);
            }
            else
            {
                iChunUtil.console("Channel " + channel + " is reregistering packet types!", true);
            }
            addDiscriminator(i, packetTypes[i]);
        }
    }

    public static EnumMap<Side, FMLEmbeddedChannel> getChannelHandlers(String modId, Class<? extends AbstractPacket>...packetTypes)
    {
        if(packetTypes.length == 0)
        {
            throw new ReportedException(new CrashReport("Mod " + modId + " is not registering any packets with its channel handlers.", new Throwable()));
        }
        EnumMap<Side, FMLEmbeddedChannel> handlers = NetworkRegistry.INSTANCE.newChannel(modId, new ChannelHandler(modId, packetTypes));

        PacketExecuter executer = new PacketExecuter();

        for(Map.Entry<Side, FMLEmbeddedChannel> e : handlers.entrySet())
        {
            FMLEmbeddedChannel channel = e.getValue();
            String codec = channel.findChannelHandlerNameForType(ChannelHandler.class);
            channel.pipeline().addAfter(codec, "PacketExecuter", executer);
        }

        return handlers;
    }

    @Sharable
    private static class PacketExecuter extends SimpleChannelInboundHandler<AbstractPacket>
    {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, AbstractPacket msg) throws Exception
        {
            Side side = ctx.channel().attr(NetworkRegistry.CHANNEL_SOURCE).get();
            EntityPlayer player = null;
            if(side.isServer())
            {
                INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
                player = ((NetHandlerPlayServer) netHandler).playerEntity;
            }
            else
            {
                player = this.getClientPlayer();
            }

            msg.execute(side, player);
        }

        @SideOnly(Side.CLIENT)
        public EntityPlayer getClientPlayer()
        {
            return Minecraft.getMinecraft().thePlayer;
        }
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, AbstractPacket msg, ByteBuf target) throws Exception
    {
        try
        {
            msg.writeTo(target, ctx.channel().attr(NetworkRegistry.CHANNEL_SOURCE).get());
        }
        catch(Exception e)
        {
            iChunUtil.console("Error writing to packet for channel: " + channel, true);
            e.printStackTrace();
        }
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, AbstractPacket msg)
    {
        try
        {
            msg.readFrom(source, ctx.channel().attr(NetworkRegistry.CHANNEL_SOURCE).get());
        }
        catch(Exception e)
        {
            iChunUtil.console("Error reading from packet for channel: " + channel, true);
            e.printStackTrace();
        }
    }

}
