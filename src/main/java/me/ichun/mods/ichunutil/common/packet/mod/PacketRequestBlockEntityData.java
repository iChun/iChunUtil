package me.ichun.mods.ichunutil.common.packet.mod;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import me.ichun.mods.ichunutil.common.entity.EntityBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;

public class PacketRequestBlockEntityData extends AbstractPacket
{
    public int id;

    public PacketRequestBlockEntityData(){}

    public PacketRequestBlockEntityData(EntityBlock block)
    {
        id = block.getEntityId();
    }

    @Override
    public void writeTo(ByteBuf buffer)
    {
        buffer.writeInt(id);
    }

    @Override
    public void readFrom(ByteBuf buffer)
    {
        id = buffer.readInt();
    }

    @Override
    public AbstractPacket execute(Side side, EntityPlayer player)
    {
        Entity ent = player.worldObj.getEntityByID(id);
        AbstractPacket packet = null;
        if(ent instanceof EntityBlock)
        {
            NBTTagCompound tag = new NBTTagCompound();
            ent.writeToNBT(tag);
            packet = new PacketBlockEntityData(id, tag);
        }
        return packet;
    }

    @Override
    public Side receivingSide()
    {
        return Side.SERVER;
    }
}
