package me.ichun.mods.ichunutil.common.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

public abstract class AbstractPacket
{
    /**
     * Write packet info in this function
     */
    public abstract void writeTo(ByteBuf buf);

    /**
     * Read packet info in this function. Execution is done elsewhere.
     */
    public abstract void readFrom(ByteBuf buf);

    /**
     * Execute your packet here. Return a packet to reply with if you'd like.
     */
    public abstract AbstractPacket execute(Side side, EntityPlayer player);

    /**
     * @return The side that will be receiving the packet, null for both sides.
     */
    public abstract Side receivingSide();

    /**
     * By default all packets are only executed on the next main server/client render tick to prevent CME issues.
     * If your packet is thread-safe you can return false for this.
     * @return is packet executed on the main ticking thread.
     */
    public boolean requiresMainThread(){ return true; };
}
