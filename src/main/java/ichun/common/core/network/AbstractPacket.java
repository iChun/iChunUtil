package ichun.common.core.network;

import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public abstract class AbstractPacket
{
    public abstract void writeTo(ByteBuf buffer, Side side);
    public abstract void readFrom(ByteBuf buffer, Side side, EntityPlayer player);
}
