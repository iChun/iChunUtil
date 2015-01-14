package us.ichun.mods.ichunutil.common.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public abstract class AbstractPacket
{
    protected EntityPlayer player;

    /**
     * Write packet info in this function
     */
    public abstract void writeTo(ByteBuf buffer, Side side);

    /**
     * Read packet info in this function. Execution is done elsewhere.
     */
    public abstract void readFrom(ByteBuf buffer, Side side);

    /**
     * Execute your packet here.
     */
    public abstract void execute(Side side, EntityPlayer player);

    /**
     * By default all packets are only executed on the next main server/client render tick to prevent CME issues.
     * If your packet is thread-safe you can return false for this.
     * @return is packet executed on the main ticking thread.
     */
    public boolean requiresMainThread(){ return true; };

    protected void setFields(EntityPlayer player)
    {
        this.player = player;
    }
}
