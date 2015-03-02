package us.ichun.mods.ichunutil.common.module.worldportals;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class WorldPortalCarrier extends TileEntity
        implements IUpdatePlayerListBox
{
    public abstract WorldPortalInfo getPortalInfo();

    @Override
    public void update()
    {
        getPortalInfo().setParent(this);
        getPortalInfo().update();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.getNbtCompound());

        worldObj.markBlockForUpdate(getPos());
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(pos, 0, tag);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        NBTTagCompound tag1 = new NBTTagCompound();
        getPortalInfo().write(tag1);
        tag.setTag("portalInfo", tag1);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);

        getPortalInfo().setParent(this);
        getPortalInfo().read(tag.getCompoundTag("portalInfo"));
    }
}
