package us.ichun.mods.ichunutil.common.core.packet.mod;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import us.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import us.ichun.mods.ichunutil.common.core.patron.PatronInfo;
import us.ichun.mods.ichunutil.common.iChunUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class PacketPatrons extends AbstractPacket
{
    public PacketPatrons(){}

    @Override
    public void writeTo(ByteBuf buffer, Side side)
    {
        for(PatronInfo info : iChunUtil.patronList)
        {
            ByteBufUtils.writeUTF8String(buffer, info.id);
            buffer.writeInt(info.type);
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
            int type = buffer.readInt();
            iChunUtil.proxy.trailTicker.patronList.add((new PatronInfo(s)).setType(type));
            s = ByteBufUtils.readUTF8String(buffer);
        }
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
    }
}
