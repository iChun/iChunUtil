package us.ichun.mods.ichunutil.common.core.packet.mod;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import us.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import us.ichun.mods.ichunutil.common.core.patron.PatronInfo;
import us.ichun.mods.ichunutil.common.iChunUtil;

public class PacketShowPatronReward extends AbstractPacket
{
    public boolean show;
    public int type;

    public PacketShowPatronReward(){}

    public PacketShowPatronReward(boolean show, int id)
    {
        this.show = show;
        this.type = id;
    }

    @Override
    public void writeTo(ByteBuf buffer, Side side)
    {
        buffer.writeBoolean(show);
        buffer.writeInt(type);
    }

    @Override
    public void readFrom(ByteBuf buffer, Side side)
    {
        show = buffer.readBoolean();
        type = buffer.readInt();
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        PatronInfo info = null;
        for(PatronInfo inf : iChunUtil.patronList)
        {
            if(inf.id.equals(player.getGameProfile().getId().toString()))
            {
                info = inf;
                break;
            }
        }
        if(info == null)
        {
            info = new PatronInfo(player.getGameProfile().getId().toString());
            iChunUtil.patronList.add(info);
        }

        if(show)
        {
            info.setType(type);
        }
        else
        {
            iChunUtil.patronList.remove(info);
        }

        iChunUtil.channel.sendToAll(new PacketPatrons());
    }
}
