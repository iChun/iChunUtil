package us.ichun.mods.ichunutil.common.core.packet.mod;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import us.ichun.mods.ichunutil.client.thread.ThreadStatistics;
import us.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import us.ichun.mods.ichunutil.common.iChunUtil;

public class PacketPatientData extends AbstractPacket
{
    public int level;
    public boolean mutate;
    public String infector;

    public PacketPatientData(){}

    public PacketPatientData(int i, boolean isMutation, String inf)
    {
        level = i;
        mutate = isMutation;
        infector = inf;
    }

    @Override
    public void writeTo(ByteBuf buffer, Side side)
    {
        buffer.writeInt(level);
        buffer.writeBoolean(mutate);
        ByteBufUtils.writeUTF8String(buffer, infector);
    }

    @Override
    public void readFrom(ByteBuf buffer, Side side)
    {
        level = buffer.readInt();
        mutate = buffer.readBoolean();
        infector = ByteBufUtils.readUTF8String(buffer);
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        if(side.isServer())
        {
            iChunUtil.proxy.tickHandlerServer.infectionMap.put(player.getGameProfile().getId().toString(), level);
        }
        else
        {
            handleClient();
        }
    }

    @SideOnly(Side.CLIENT)
    public void handleClient()
    {
        if(ThreadStatistics.stats.statsOptOut != 1)
        {
            iChunUtil.proxy.tickHandlerClient.infectionTimeout = level == 0 ? 100 : 60;
            iChunUtil.proxy.tickHandlerClient.isFirstInfection = level == 0;
            ThreadStatistics.stats.reveal("statsData");
            ThreadStatistics.stats.statsData = ThreadStatistics.getInfectionHash(level);
            ThreadStatistics.stats.save();

            if(!mutate) //Infect Event
            {
                (new ThreadStatistics(2, level, infector)).start();
            }
            else //Mutate Event
            {
                (new ThreadStatistics(3, level)).start();
            }
        }
    }
}
