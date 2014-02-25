package ichun.core.network;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;

public class ChannelHandler extends FMLIndexedMessageToMessageCodec<AbstractPacket>
{
    public final String channel;

    public ChannelHandler(String s, Class<? extends AbstractPacket>...packetTypes)
    {
        channel = s;
        for(int i = 0; i < packetTypes.length; i++)
        {
            addDiscriminator(i, packetTypes[i]);
        }
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, AbstractPacket msg, ByteBuf target) throws Exception
    {
        msg.writeTo(target, FMLCommonHandler.instance().getEffectiveSide());
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, AbstractPacket msg)
    {
        Side side = FMLCommonHandler.instance().getEffectiveSide();
        EntityPlayer player = null;
        switch(side)
        {
            case CLIENT:
            {
                player = this.getClientPlayer();
                break;
            }
            case SERVER:
            {
                INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
                player = ((NetHandlerPlayServer) netHandler).playerEntity;
                break;
            }
            default:
        }
        msg.readFrom(source, side, player);
    }

    @SideOnly(Side.CLIENT)
    public EntityPlayer getClientPlayer()
    {
        return Minecraft.getMinecraft().thePlayer;
    }

}
