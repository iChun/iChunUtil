package me.ichun.mods.ichunutil.common.network;

import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PacketChannel
{
    public static final PacketDistributor<ServerPlayerEntity> ALL_EXCEPT = new PacketDistributor<>(PacketChannel::allExcept, NetworkDirection.PLAY_TO_CLIENT);

    private final SimpleChannel channel;
    private final Object2ByteOpenHashMap<Class<? extends AbstractPacket>> clzToId;
    private final Class<? extends AbstractPacket>[] idToClz;

    @SafeVarargs
    public PacketChannel(ResourceLocation name, String protocolVersion, Class<? extends AbstractPacket>...packetTypes)
    {
        this(name, protocolVersion, true, true, packetTypes);
    }

    @SafeVarargs
    public PacketChannel(ResourceLocation name, String protocolVersion, boolean clientRequired, boolean serverRequired, Class<? extends AbstractPacket>...packetTypes)
    {
        this(name, protocolVersion, o -> protocolVersion.equals(o) || !serverRequired, o -> protocolVersion.equals(o) || !clientRequired, packetTypes);
    }

    @SafeVarargs
    public PacketChannel(ResourceLocation name, String protocolVersion, Predicate<String> clientPredicate, Predicate<String> serverPredicate, Class<? extends AbstractPacket>...packetTypes)
    {
        clzToId = new Object2ByteOpenHashMap<>(packetTypes.length);
        for(int i = 0; i < packetTypes.length; i++)
        {
            clzToId.put(packetTypes[i], (byte)i);
        }
        idToClz = packetTypes;

        channel = NetworkRegistry.newSimpleChannel(name, () -> protocolVersion, clientPredicate, serverPredicate);
        channel.registerMessage(0, PacketHolder.class,
                (packet, buffer) -> {
                    buffer.writeByte(clzToId.getByte(packet.packet.getClass()));
                    packet.packet.writeTo(buffer);
                },
                (buffer) -> {
                    Class<? extends AbstractPacket> clz = idToClz[buffer.readByte()];
                    AbstractPacket packet = null;
                    try
                    {
                        packet = clz.newInstance();
                        packet.readFrom(buffer);
                    }
                    catch(InstantiationException | IllegalAccessException e)
                    {
                        iChunUtil.LOGGER.error("Error initialising packet.");
                        e.printStackTrace();
                    }
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

    public void reply(AbstractPacket packet, NetworkEvent.Context context)
    {
        channel.reply(new PacketHolder(packet), context);
    }

    private static class PacketHolder
    {
        private final AbstractPacket packet;
        private PacketHolder(AbstractPacket packet)
        {
            this.packet = packet;
        }
    }

    private static Consumer<IPacket<?>> allExcept(PacketDistributor<ServerPlayerEntity> packetDistributor, Supplier<ServerPlayerEntity> entityPlayerMPSupplier)
    {
        return p -> {
            ServerPlayerEntity player = entityPlayerMPSupplier.get();
            for(ServerPlayerEntity serverPlayerEntity : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers())
            {
                if(serverPlayerEntity == player)
                {
                    continue;
                }
                serverPlayerEntity.connection.sendPacket(p);
            }
        };
    }
}
