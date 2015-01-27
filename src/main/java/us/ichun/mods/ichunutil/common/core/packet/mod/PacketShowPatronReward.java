package us.ichun.mods.ichunutil.common.core.packet.mod;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import us.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import us.ichun.mods.ichunutil.common.iChunUtil;

public class PacketShowPatronReward extends AbstractPacket
{
    public boolean show;

    public PacketShowPatronReward(){}

    public PacketShowPatronReward(boolean show)
    {
        this.show = show;
    }

    @Override
    public void writeTo(ByteBuf buffer, Side side)
    {
        buffer.writeBoolean(show);
    }

    @Override
    public void readFrom(ByteBuf buffer, Side side)
    {
        show = buffer.readBoolean();
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        if(show)
        {
            iChunUtil.patronList.add(player.getGameProfile().getId().toString());
        }
        else
        {
            iChunUtil.patronList.remove(player.getGameProfile().getId().toString());
        }

        iChunUtil.channel.sendToAll(new PacketPatrons());
    }
}
