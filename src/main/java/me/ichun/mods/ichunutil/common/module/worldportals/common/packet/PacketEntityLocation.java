package me.ichun.mods.ichunutil.common.module.worldportals.common.packet;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import me.ichun.mods.ichunutil.common.module.worldportals.common.WorldPortals;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class PacketEntityLocation extends AbstractPacket
{
    public int id;
    public double lX;
    public double lY;
    public double lZ;
    public double x;
    public double y;
    public double z;
    public float prevYaw;
    public float prevPitch;
    public float yaw;
    public float pitch;
    public double mX;
    public double mY;
    public double mZ;

    public PacketEntityLocation() {}

    public PacketEntityLocation(Entity ent)
    {
        id = ent.getEntityId();
        lX = ent.lastTickPosX;
        lY = ent.lastTickPosY;
        lZ = ent.lastTickPosZ;
        x = ent.posX;
        y = ent.posY;
        z = ent.posZ;
        prevYaw = ent.prevRotationYaw;
        prevPitch = ent.prevRotationPitch;
        yaw = ent.rotationYaw;
        pitch = ent.rotationPitch;
        mX = ent.motionX;
        mY = ent.motionY;
        mZ = ent.motionZ;
    }

    public PacketEntityLocation(int id, double lastX, double lastY, double lastZ, double x, double y, double z, float prevYaw, float prevPitch, float yaw, float pitch, double mX, double mY, double mZ)
    {
        this.id = id;
        this.lX = lastX;
        this.lY = lastY;
        this.lZ = lastZ;
        this.x = x;
        this.y = y;
        this.z = z;
        this.prevYaw = prevYaw;
        this.prevPitch = prevPitch;
        this.yaw = yaw;
        this.pitch = pitch;
        this.mX = mX;
        this.mY = mY;
        this.mZ = mZ;
    }

    @Override
    public void writeTo(ByteBuf buf)
    {
        buf.writeInt(id);
        buf.writeDouble(lX);
        buf.writeDouble(lY);
        buf.writeDouble(lZ);
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeFloat(prevYaw);
        buf.writeFloat(prevPitch);
        buf.writeFloat(yaw);
        buf.writeFloat(pitch);
        buf.writeDouble(mX);
        buf.writeDouble(mY);
        buf.writeDouble(mZ);
    }

    @Override
    public void readFrom(ByteBuf buf)
    {
        id = buf.readInt();
        lX = buf.readDouble();
        lY = buf.readDouble();
        lZ = buf.readDouble();
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        prevYaw = buf.readFloat();
        prevPitch = buf.readFloat();
        yaw = buf.readFloat();
        pitch = buf.readFloat();
        mX = buf.readDouble();
        mY = buf.readDouble();
        mZ = buf.readDouble();
    }

    @Override
    public AbstractPacket execute(Side side, EntityPlayer player)
    {
        Entity ent = player.world.getEntityByID(id);
        if(ent != null && !(player.world.isRemote && player == ent))
        {
            ent.setLocationAndAngles(x, y, z, yaw, pitch);
            ent.motionX = mX;
            ent.motionY = mY;
            ent.motionZ = mZ;
            if(!player.world.isRemote)
            {
                if(ent instanceof EntityPlayerMP && ent == player)
                {
                    EntityPlayerMP ply = (EntityPlayerMP)ent;
                    ply.connection.lastPositionUpdate = ply.connection.networkTickCount;
                    ply.setPositionAndRotation(x, y, z, yaw, pitch);
                    ply.motionX = mX;
                    ply.motionY = mY;
                    ply.motionZ = mZ;
                }
                WorldPortals.channel.sendToAllAround(new PacketEntityLocation(ent), new NetworkRegistry.TargetPoint(ent.dimension, ent.posX, ent.posY, ent.posZ, 256D));
            }
            else
            {
                float yawDifference = 0.0F;
                float prevYawDifference = 0.0F;
                EntityLivingBase living = null;
                float riderYaw = 0.0F;
                float prevRiderYaw = 0.0F;

                if(ent instanceof EntityLivingBase)
                {
                    living = (EntityLivingBase)ent;
                    yawDifference = living.renderYawOffset - ent.rotationYaw;
                    prevYawDifference = living.prevRenderYawOffset - ent.rotationYaw;
                }

                for(Entity passenger : ent.getPassengers())
                {
                    riderYaw = passenger.rotationYaw - ent.rotationYaw;
                    prevRiderYaw = passenger.prevRotationYaw - ent.rotationYaw;
                }

                ent.lastTickPosX = ent.prevPosX = lX;
                ent.lastTickPosY = ent.prevPosY = lY;
                ent.lastTickPosZ = ent.prevPosZ = lZ;
                ent.posX = x;
                ent.posY = y;
                ent.posZ = z;
                ent.prevRotationYaw = prevYaw;
                ent.prevRotationPitch = prevPitch;
                ent.rotationYaw = yaw;
                ent.rotationPitch = pitch;
                ent.setPosition(ent.posX, ent.posY, ent.posZ);

                for(Entity passenger : ent.getPassengers())
                {
                    ent.updatePassenger(passenger);
                    passenger.rotationYaw = passenger.prevRotationYaw = ent.rotationYaw;
                    passenger.rotationYaw += riderYaw;
                    passenger.prevRotationYaw += prevRiderYaw;
                }

                if(ent instanceof EntityLivingBase)
                {
                    living.renderYawOffset = living.prevRenderYawOffset = ent.rotationYaw;
                    living.renderYawOffset += yawDifference;
                    living.prevRenderYawOffset += prevYawDifference;
                }
                else if(ent instanceof EntityArrow)
                {
                    ((EntityArrow)ent).inGround = false;
                }
            }
        }
        return null;
    }

    @Override
    public Side receivingSide()
    {
        return null;
    }
}
