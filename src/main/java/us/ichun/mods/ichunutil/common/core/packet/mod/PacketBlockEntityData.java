package us.ichun.mods.ichunutil.common.core.packet.mod;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import us.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import us.ichun.mods.ichunutil.common.entity.EntityBlock;

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
    public void writeTo(ByteBuf buffer, Side side)
    {
        buffer.writeInt(id);
        ByteBufUtils.writeTag(buffer, tag);
    }

    @Override
    public void readFrom(ByteBuf buffer, Side side)
    {
        id = buffer.readInt();
        tag = ByteBufUtils.readTag(buffer);
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        Entity ent = player.worldObj.getEntityByID(id);
        if(ent instanceof EntityBlock)
        {
            EntityBlock block = (EntityBlock)ent;
            block.readFromNBT(tag);
            block.setup = true;
        }
    }
}
