package me.ichun.mods.ichunutil.common.packet.mod;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.ichunutil.client.module.update.GuiUpdateNotifier;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import me.ichun.mods.ichunutil.common.core.util.EventCalendar;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.update.UpdateChecker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketUserShouldShowUpdates extends AbstractPacket
{
    public boolean notifyUpdate;

    public PacketUserShouldShowUpdates(){}

    public PacketUserShouldShowUpdates(boolean update)
    {
        notifyUpdate = update;
    }

    @Override
    public void writeTo(ByteBuf buf)
    {
        buf.writeBoolean(notifyUpdate);
    }

    @Override
    public void readFrom(ByteBuf buf)
    {
        notifyUpdate = buf.readBoolean();
    }

    @Override
    public AbstractPacket execute(Side side, EntityPlayer player)
    {
        for(UpdateChecker.ModVersionInfo info : UpdateChecker.getModsWithUpdates())
        {
            if(!notifyUpdate && !info.isModClientOnly)
            {
                continue;
            }
            iChunUtil.LOGGER.info("[NEW UPDATE AVAILABLE] " + info.modName + " - " + info.modVersionNew);
        }

        if(iChunUtil.config.versionNotificationFrequency == 1 || iChunUtil.config.versionNotificationFrequency == 2 && iChunUtil.config.versionSave != EventCalendar.day)
        {
            handleClient(notifyUpdate);
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    public void handleClient(boolean notifyAll)
    {
        GuiUpdateNotifier.notifyAll = notifyAll;
        GuiUpdateNotifier.notifyUpdates();

        if(iChunUtil.config.versionNotificationFrequency == 2)
        {
            iChunUtil.config.versionSave = EventCalendar.day;
            iChunUtil.config.save();
        }
    }

    @Override
    public Side receivingSide()
    {
        return Side.CLIENT;
    }
}
