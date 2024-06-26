package me.ichun.mods.ichunutil.common.network;

import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public abstract class PacketChannel
{
    protected final ResourceLocation channelId;
    protected final Object2ByteOpenHashMap<Class<? extends AbstractPacket>> clzToId;
    protected final Class<? extends AbstractPacket>[] idToClz;

    @SafeVarargs
    public PacketChannel(ResourceLocation name, Class<? extends AbstractPacket>...packetTypes)
    {
        channelId = name;

        clzToId = new Object2ByteOpenHashMap<>(packetTypes.length);
        for(int i = 0; i < packetTypes.length; i++)
        {
            clzToId.put(packetTypes[i], (byte)i);
        }
        idToClz = packetTypes;
    }

    public abstract void sendToServer(AbstractPacket packet);

    public abstract void sendTo(AbstractPacket packet, ServerPlayer player);

    public abstract void sendToAll(AbstractPacket packet);

    public abstract void sendToTracking(AbstractPacket packet, Entity entity);

    public abstract void sendToAround(AbstractPacket packet, ServerLevel world, double x, double y, double z, double radius);

    protected PacketPayload payload(AbstractPacket packet)
    {
        //        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer()); // Taken from Fabric's PacketByteBufs.create();
        return new PacketPayload(packet);
    }

    protected PacketPayload readPacket(FriendlyByteBuf buffer)
    {
        byte id = buffer.readByte();
        Class<? extends AbstractPacket> clz = idToClz[id];
        AbstractPacket packet;
        try
        {
            packet = clz.getDeclaredConstructor().newInstance();
            packet.readFrom(buffer);
        }
        catch(NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e)
        {
            throw new RuntimeException("Unable to create packet for " + channelId.toString() + " with id " + id, e);
        }
        return new PacketPayload(packet);
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    protected Player getPlayer()
    {
        return iChunUtil.eC().getPlayer();
    }

    protected class PacketPayload implements CustomPacketPayload
    {
        private final AbstractPacket packet;

        private PacketPayload(AbstractPacket packet)
        {
            this.packet = packet;
        }

        public void write(FriendlyByteBuf buffer)
        {
            buffer.writeByte(clzToId.getByte(packet.getClass()));
            packet.writeTo(buffer);
        }

        @Override
        public ResourceLocation id()
        {
            return channelId;
        }

        public Optional<Runnable> process(@Nullable Player player)
        {
            return packet.process(player);
        }
    }
}
