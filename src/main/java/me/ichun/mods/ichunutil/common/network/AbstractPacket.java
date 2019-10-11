package me.ichun.mods.ichunutil.common.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public abstract class AbstractPacket //Yes I know we could be an interface.
{
    public abstract void writeTo(PacketBuffer buf);
    public abstract void readFrom(PacketBuffer buf);
    public abstract void process(NetworkEvent.Context context); //done on networking thread
}
