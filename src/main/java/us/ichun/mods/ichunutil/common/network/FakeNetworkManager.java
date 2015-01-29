package us.ichun.mods.ichunutil.common.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.*;
import net.minecraft.util.IChatComponent;

import javax.crypto.SecretKey;

public class FakeNetworkManager extends NetworkManager
{
    public FakeNetworkManager(EnumPacketDirection packetDirection)
    {
        super(packetDirection);
    }

    @Override
    public void channelActive(ChannelHandlerContext p_channelActive_1_) throws Exception
    {
    }

    @Override
    public void setConnectionState(EnumConnectionState newState)
    {
    }

    @Override
    public void channelInactive(ChannelHandlerContext p_channelInactive_1_)
    {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext p_exceptionCaught_1_, Throwable p_exceptionCaught_2_)
    {
    }

    @Override
    public void channelRead0(ChannelHandlerContext p_channelRead0_1_, Packet p_channelRead0_2_)
    {
    }

    @Override
    public void setNetHandler(INetHandler handler)
    {
    }

    @Override
    public void sendPacket(Packet packetIn)
    {
    }

    @Override
    public void sendPacket(Packet packetIn, GenericFutureListener listener, GenericFutureListener ... listeners)
    {
    }

    @Override
    public void processReceivedPackets()
    {
    }

    @Override
    public void closeChannel(IChatComponent message)
    {
    }

    @Override
    public boolean isLocalChannel()
    {
        return true;
    }

    @Override
    public void enableEncryption(SecretKey key)
    {
    }

    @Override
    public boolean isChannelOpen()
    {
        return false;
    }

    @Override
    public boolean hasNoChannel()
    {
        return true;
    }

    @Override
    public IChatComponent getExitMessage()
    {
        return null;
    }

    @Override
    public void disableAutoRead()
    {
    }

    @Override
    public void setCompressionTreshold(int treshold)
    {
    }

    @Override
    public void checkDisconnected()
    {
    }

    @Override
    public void channelRead0(ChannelHandlerContext p_channelRead0_1_, Object p_channelRead0_2_)
    {
    }
}
