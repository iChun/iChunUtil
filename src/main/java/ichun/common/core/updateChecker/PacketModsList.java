package ichun.common.core.updateChecker;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import ichun.common.core.network.AbstractPacket;
import ichun.common.iChunUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.Map;

public class PacketModsList extends AbstractPacket
{
    public int id;
    public boolean isOp;

    public PacketModsList(){}

    public PacketModsList(int i, boolean op)
    {
        id = i;
        isOp = op;
    }

    @Override
    public void writeTo(ByteBuf buffer, Side side)
    {
        buffer.writeInt(id);
        buffer.writeBoolean(isOp);
        ModVersionChecker.writeToBuffer(buffer);
    }

    @Override
    public void readFrom(ByteBuf buffer, Side side)
    {
        id = buffer.readInt();
        isOp = buffer.readBoolean();
        HashMap<String, String> versions = new HashMap<String, String>();
        String name = ByteBufUtils.readUTF8String(buffer);
        while(!name.equalsIgnoreCase("##endPacket"))
        {
            versions.put(name, ByteBufUtils.readUTF8String(buffer));
            name = ByteBufUtils.readUTF8String(buffer);
        }

        if(iChunUtil.config.getInt("versionNotificationTypes") == 1 && !isOp && iChunUtil.proxy.tickHandlerClient.modUpdateNotification != null)
        {
            iChunUtil.proxy.tickHandlerClient.modUpdateNotification.clearModUpdates();
        }

        if(iChunUtil.config.getInt("versionNotificationTypes") == 0 || iChunUtil.config.getInt("versionNotificationTypes") == 1 && isOp)
        {
            ModVersionChecker.compareServerVersions(versions);
        }
    }

    @Override
    public void execute(Side side, EntityPlayer player){}
}
