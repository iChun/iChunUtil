package me.ichun.mods.ichunutil.common.packet.mod;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.patron.PatronInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

public class PacketPatronInfo extends AbstractPacket
{
    public String playerId;
    public int patronRewardType;
    public boolean showPatronReward;

    public PacketPatronInfo(){}

    public PacketPatronInfo(String playerId, int patronRewardType, boolean showPatronReward)
    {
        this.playerId = playerId;
        this.patronRewardType = patronRewardType;
        this.showPatronReward = showPatronReward;
    }

    @Override
    public void writeTo(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, playerId);
        buf.writeInt(patronRewardType);
        buf.writeBoolean(showPatronReward);
    }

    @Override
    public void readFrom(ByteBuf buf)
    {
        playerId = ByteBufUtils.readUTF8String(buf);
        patronRewardType = buf.readInt();
        showPatronReward = buf.readBoolean();
    }

    @Override
    public AbstractPacket execute(Side side, EntityPlayer player)
    {
        PatronInfo info = new PatronInfo(playerId, patronRewardType, showPatronReward);
        if(iChunUtil.eventHandlerServer.patrons.contains(info)) //This is fine because for the equal check to pass, all that is checked is the ID.
        {
            iChunUtil.eventHandlerServer.patrons.remove(info);
        }
        iChunUtil.eventHandlerServer.patrons.add(info);
        iChunUtil.channel.sendToAll(new PacketPatrons(info));
        return null;
    }

    @Override
    public Side receivingSide() { return Side.SERVER; }
}
