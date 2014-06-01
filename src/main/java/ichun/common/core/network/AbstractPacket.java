package ichun.common.core.network;

import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public abstract class AbstractPacket
{
    /**
     * Write packet info in this function
     */
    public abstract void writeTo(ByteBuf buffer, Side side);

    /**
     * Read packet info in this function. Execution is done elsewhere.
     */
    public abstract void readFrom(ByteBuf buffer, Side side);

    /**
     * Execute your packet here... I think.
     */
    public abstract void execute(Side side, EntityPlayer player);
}
