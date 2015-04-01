package us.ichun.mods.ichunutil.common.module.worldportals;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
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

        if(getWorld().isRemote)
        {
            identifyBorders();
        }
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

    public AxisAlignedBB getWorldPortalBoundingBox()
    {
        WorldPortalInfo portalInfo = getPortalInfo();
        BlockPos blockPos = getPos();
        double x1 = blockPos.getX() + 0.5D;
        double x2 = blockPos.getX() + 0.5D;

        double y1 = blockPos.getY() + 0.5D;
        double y2 = blockPos.getY() + 0.5D;

        double z1 = blockPos.getZ() + 0.5D;
        double z2 = blockPos.getZ() + 0.5D;

        if(portalInfo.face == 0)
        {
            y1 += portalInfo.offsetHeight + 0.5D;
            y2 += portalInfo.offsetHeight + 0.5D + portalInfo.height;

            if(portalInfo.orientation == 0)
            {
                x1 += portalInfo.width;
                x2 -= portalInfo.width;

                z1 -= portalInfo.offsetDepth;
                z2 -= portalInfo.offsetDepth;
            }
            else if(portalInfo.orientation == 2)
            {
                x1 += portalInfo.width;
                x2 -= portalInfo.width;

                z1 += portalInfo.offsetDepth;
                z2 += portalInfo.offsetDepth;
            }
            else if(portalInfo.orientation == 1)
            {
                x1 -= portalInfo.offsetDepth;
                x2 -= portalInfo.offsetDepth;

                z1 -= portalInfo.width;
                z2 += portalInfo.width;
            }
            else if(portalInfo.orientation == 3)
            {
                x1 += portalInfo.offsetDepth;
                x2 += portalInfo.offsetDepth;

                z1 -= portalInfo.width;
                z2 += portalInfo.width;
            }
        }

        return AxisAlignedBB.fromBounds(x1, y1, z1, x2, y2, z2);
    }

    @SideOnly(Side.CLIENT)
    public void identifyBorders()
    {
        if(Minecraft.getMinecraft().gameSettings.showDebugInfo)
        {
            AxisAlignedBB aabb = getWorldPortalBoundingBox();
            EnumParticleTypes type = EnumParticleTypes.TOWN_AURA;
            getWorld().spawnParticle(type, aabb.minX, aabb.minY, aabb.minZ, 0, 0, 0);
            getWorld().spawnParticle(type, aabb.minX, aabb.maxY, aabb.minZ, 0, 0, 0);
            getWorld().spawnParticle(type, aabb.minX, aabb.minY, aabb.maxZ, 0, 0, 0);
            getWorld().spawnParticle(type, aabb.minX, aabb.maxY, aabb.maxZ, 0, 0, 0);
            getWorld().spawnParticle(type, aabb.maxX, aabb.minY, aabb.minZ, 0, 0, 0);
            getWorld().spawnParticle(type, aabb.maxX, aabb.maxY, aabb.minZ, 0, 0, 0);
            getWorld().spawnParticle(type, aabb.maxX, aabb.minY, aabb.maxZ, 0, 0, 0);
            getWorld().spawnParticle(type, aabb.maxX, aabb.maxY, aabb.maxZ, 0, 0, 0);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        if(!canRenderEntitySeePortal())
        {
            AxisAlignedBB.fromBounds(0D, -500D, 0D, 0D, -500D, 0D);
        }
        return getWorldPortalBoundingBox();
    }

    @SideOnly(Side.CLIENT)
    public boolean canRenderEntitySeePortal()
    {
        Minecraft mc = Minecraft.getMinecraft();

        Entity ent = mc.getRenderViewEntity();

        float tpDist = mc.gameSettings.thirdPersonView == 0 ? 0F : mc.entityRenderer.thirdPersonDistance;

        WorldPortalInfo portalInfo = getPortalInfo();
        AxisAlignedBB borders = getWorldPortalBoundingBox();

        if(portalInfo.face == 0)
        {
            boolean posFail = portalInfo.orientation == 0 && ent.posZ < borders.minZ - tpDist || portalInfo.orientation == 2 && ent.posZ > borders.minZ + tpDist || portalInfo.orientation == 1 && ent.posX < borders.minX - tpDist || portalInfo.orientation == 3 && ent.posX > borders.minX + tpDist;
            if(posFail)
            {
                return false;
            }
        }

        return true;
    }
}
