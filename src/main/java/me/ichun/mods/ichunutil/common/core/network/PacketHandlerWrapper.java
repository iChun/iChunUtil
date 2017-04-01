package me.ichun.mods.ichunutil.common.core.network;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

public class PacketHandlerWrapper<REQ extends AbstractPacket> extends SimpleChannelInboundHandler<REQ>
{
    private final Side side;

    public PacketHandlerWrapper(Side side, Class<REQ> requestType)
    {
        super(requestType);
        this.side = side;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final REQ msg) throws Exception
    {
        EntityPlayer player;
        if(side.isServer())
        {
            INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
            player = ((NetHandlerPlayServer)netHandler).playerEntity;
        }
        else
        {
            player = this.getClientPlayer();
        }

        if(!msg.requiresMainThread())
        {
            executeMessage(msg, player, side, ctx);
        }
        else
        {
            IThreadListener thread = FMLCommonHandler.instance().getWorldThread(ctx.channel().attr(NetworkRegistry.NET_HANDLER).get());
            if(thread.isCallingFromMinecraftThread())
            {
                executeMessage(msg, player, side, ctx);
            }
            else
            {
                thread.addScheduledTask(() -> executeMessage(msg, player, side, ctx));
            }
        }
    }

    public void executeMessage(AbstractPacket msg, EntityPlayer player, Side side, ChannelHandlerContext ctx)
    {
        AbstractPacket result = msg.execute(side, player);
        if(result != null)//TODO remove in 1.11
        {
            if(side.isServer()) //server recieved the packet, send reply to client.
            {
                ctx.channel().attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
                ctx.channel().attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
                ctx.writeAndFlush(result).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            }
            else
            {
                ctx.channel().attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
                ctx.writeAndFlush(result).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public EntityPlayer getClientPlayer()
    {
        return Minecraft.getMinecraft().thePlayer;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        FMLLog.log(Level.ERROR, cause, "iChunUtil Packet exception");
        super.exceptionCaught(ctx, cause);
    }
}