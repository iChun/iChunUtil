package ichun.common.core.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import ichun.common.core.network.AbstractPacket;
import ichun.common.iChunUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class PacketPatrons extends AbstractPacket
{
    public PacketPatrons(){}

    @Override
    public void writeTo(ByteBuf buffer, Side side)
    {
        for(String s : iChunUtil.patronList)
        {
            ByteBufUtils.writeUTF8String(buffer, s);
        }
        ByteBufUtils.writeUTF8String(buffer, "##endPacket");
    }

    @Override
    public void readFrom(ByteBuf buffer, Side side)
    {
        iChunUtil.proxy.trailTicker.patronList.clear();
        String s = ByteBufUtils.readUTF8String(buffer);
        while(!s.equals("##endPacket"))
        {
            iChunUtil.proxy.trailTicker.patronList.add(s);
            s = ByteBufUtils.readUTF8String(buffer);
        }
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
    }
}
