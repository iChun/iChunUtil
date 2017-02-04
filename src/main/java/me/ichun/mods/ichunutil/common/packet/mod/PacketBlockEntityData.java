package me.ichun.mods.ichunutil.common.packet.mod;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import me.ichun.mods.ichunutil.common.entity.EntityBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

public class PacketBlockEntityData extends AbstractPacket
{
    public int id;
    public NBTTagCompound tag;

    public PacketBlockEntityData() {}

    public PacketBlockEntityData(int id, NBTTagCompound tag)
    {
        this.id = id;
        this.tag = tag;
    }

    @Override
    public void writeTo(ByteBuf buffer)
    {
        buffer.writeInt(id);
        ByteBufUtils.writeTag(buffer, tag);
    }

    @Override
    public void readFrom(ByteBuf buffer)
    {
        id = buffer.readInt();
        tag = ByteBufUtils.readTag(buffer);
    }

    @Override
    public AbstractPacket execute(Side side, EntityPlayer player)
    {
        Entity ent = player.world.getEntityByID(id);
        if(ent instanceof EntityBlock)
        {
            EntityBlock block = (EntityBlock)ent;
            block.readFromNBT(tag);
            block.setup = true;
        }
        return null;
    }

    @Override
    public Side receivingSide()
    {
        return Side.CLIENT;
    }
}
