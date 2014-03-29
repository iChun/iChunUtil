package ichun.core.network;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ichun.core.iChunUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;

import java.util.ArrayList;
import java.util.Collections;

public class ChannelHandler extends FMLIndexedMessageToMessageCodec<AbstractPacket>
{
    public final String channel;

    public ChannelHandler(String s, Class<? extends AbstractPacket>...packetTypes)
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

    @Override
    public void encodeInto(ChannelHandlerContext ctx, AbstractPacket msg, ByteBuf target) throws Exception
    {
        try
        {
            msg.writeTo(target, FMLCommonHandler.instance().getEffectiveSide());
        }
        catch(Exception e)
        {
            iChunUtil.console("Error writing to packet for channel: " + channel);
            e.printStackTrace();
        }
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, AbstractPacket msg)
    {
        Side side = FMLCommonHandler.instance().getEffectiveSide();
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
        try
        {
            msg.readFrom(source, side, player);
        }
        catch(Exception e)
        {
            iChunUtil.console("Error reading to packet for channel: " + channel);
            e.printStackTrace();
        }
    }

    @SideOnly(Side.CLIENT)
    public EntityPlayer getClientPlayer()
    {
        return Minecraft.getMinecraft().thePlayer;
    }

}
