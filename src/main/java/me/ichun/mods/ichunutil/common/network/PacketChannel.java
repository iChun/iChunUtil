package me.ichun.mods.ichunutil.common.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.Logger;

public class PacketChannel
{
    private final SimpleChannel channel;
    private final BiMap<Class<? extends AbstractPacket>, Byte> clzToId;
    private final BiMap<Byte, Class<? extends AbstractPacket>> idToClz;

    public PacketChannel(ResourceLocation name, String protocolVersion, Class<? extends AbstractPacket>...packetTypes)
    {
        clzToId = HashBiMap.create(packetTypes.length);
        for(int i = 0; i < packetTypes.length; i++)
        {
            clzToId.put(packetTypes[i], (byte)i);
        }
        idToClz = clzToId.inverse();

        channel = NetworkRegistry.newSimpleChannel(name, () -> protocolVersion, protocolVersion::equals, protocolVersion::equals);
        channel.registerMessage(0, PacketHolder.class,
        (packet, buffer) -> {
            buffer.writeByte(clzToId.get(packet.packet.getClass()));
            packet.packet.writeTo(buffer);
        },
        (buffer) -> {
            Class<? extends AbstractPacket> clz = idToClz.get(buffer.readByte());
            AbstractPacket packet = null;
            try
            {
                packet = clz.newInstance();
                packet.readFrom(buffer);
            }
            catch(InstantiationException | IllegalAccessException ignored){}
            return new PacketHolder(packet);
        },
        (packet, contextSupplier) -> {
            NetworkEvent.Context context = contextSupplier.get();
            packet.packet.process(context);
            context.setPacketHandled(true);
        });
    }

    public void sendToServer(AbstractPacket packet)
    {
        channel.sendToServer(new PacketHolder(packet));
    }

    public void sendTo(AbstractPacket packet, ServerPlayerEntity player)
    {
        channel.sendTo(new PacketHolder(packet), player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
    }

    public void sendTo(AbstractPacket packet, PacketDistributor.PacketTarget packetTarget)
    {
        channel.send(packetTarget, new PacketHolder(packet));
    }

    public static class PacketHolder
    {
        private AbstractPacket packet;
        private PacketHolder(AbstractPacket packet)
        {
            this.packet = packet;
        }
    }
}
