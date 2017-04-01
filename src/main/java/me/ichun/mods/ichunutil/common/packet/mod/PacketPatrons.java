package me.ichun.mods.ichunutil.common.packet.mod;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.ichunutil.client.module.patron.PatronEffectRenderer;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.patron.PatronInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashSet;

public class PacketPatrons extends AbstractPacket
{
    public PatronInfo info;
    public HashSet<PatronInfo> patrons;

    public PacketPatrons() {}

    public PacketPatrons(PatronInfo info)
    {
        this.info = info;
    }

    @Override
    public void writeTo(ByteBuf buf)
    {
        HashSet<PatronInfo> patrons = info != null ? new HashSet<PatronInfo>()
        {{
            add(info);
        }} : iChunUtil.eventHandlerServer.patrons;
        for(PatronInfo info : patrons)
        {
            ByteBufUtils.writeUTF8String(buf, info.id);
            buf.writeInt(info.effectType);
            buf.writeBoolean(info.showEffect);
        }
        ByteBufUtils.writeUTF8String(buf, "##endPacket");
    }

    @Override
    public void readFrom(ByteBuf buf)
    {
        patrons = new HashSet<>();
        String s = ByteBufUtils.readUTF8String(buf);
        while(!s.equals("##endPacket"))
        {
            int type = buf.readInt();
            boolean show = buf.readBoolean();
            patrons.add(new PatronInfo(s, type, show));
            s = ByteBufUtils.readUTF8String(buf);
        }
    }

    @Override
    public AbstractPacket execute(Side side, EntityPlayer player)
    {
        for(PatronInfo info : patrons)
        {
            PatronEffectRenderer.patrons.remove(info); //purge the old version of info.
            PatronEffectRenderer.patrons.add(info);
        }
        return null;
    }

    @Override
    public Side receivingSide() { return Side.CLIENT; }
}
