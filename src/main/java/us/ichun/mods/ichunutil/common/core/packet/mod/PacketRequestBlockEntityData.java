package us.ichun.mods.ichunutil.common.core.packet.mod;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import us.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import us.ichun.mods.ichunutil.common.entity.EntityBlock;
import us.ichun.mods.ichunutil.common.iChunUtil;

public class PacketRequestBlockEntityData extends AbstractPacket
{
    public int id;

    public PacketRequestBlockEntityData(){}

    public PacketRequestBlockEntityData(EntityBlock block)
    {
        id = block.getEntityId();
    }

    @Override
    public void writeTo(ByteBuf buffer, Side side)
    {
        buffer.writeInt(id);
    }

    @Override
    public void readFrom(ByteBuf buffer, Side side)
    {
        id = buffer.readInt();
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        Entity ent = player.worldObj.getEntityByID(id);
        if(ent instanceof EntityBlock)
        {
            NBTTagCompound tag = new NBTTagCompound();
            ((EntityBlock)ent).writeToNBT(tag);

            iChunUtil.channel.sendToPlayer(new PacketBlockEntityData(id, tag), player);
        }
    }
}
