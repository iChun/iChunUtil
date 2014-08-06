package ichun.common.core.packet;

import cpw.mods.fml.relauncher.Side;
import ichun.common.core.network.AbstractPacket;
import ichun.common.core.network.PacketHandler;
import ichun.common.iChunUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

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

        PacketHandler.sendToAll(iChunUtil.channels, new PacketPatrons());
    }
}
