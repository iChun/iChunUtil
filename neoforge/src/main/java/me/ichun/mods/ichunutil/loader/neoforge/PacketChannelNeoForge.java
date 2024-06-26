package me.ichun.mods.ichunutil.loader.neoforge;

import me.ichun.mods.ichunutil.common.network.AbstractPacket;
import me.ichun.mods.ichunutil.common.network.PacketChannel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.PlayNetworkDirection;
import net.neoforged.neoforge.network.simple.SimpleChannel;

public class PacketChannelNeoForge extends PacketChannel
{
    private final SimpleChannel channel;

    public PacketChannelNeoForge(ResourceLocation name, int protocolVersion, Class<? extends AbstractPacket>... packetTypes)
    {
        this(name, protocolVersion, true, true, packetTypes);
    }

    public PacketChannelNeoForge(ResourceLocation name, int protocolVersion, boolean clientRequired, boolean serverRequired, Class<? extends AbstractPacket>... packetTypes)
    {
        super(name, packetTypes);

        final String protVersion = Integer.toString(protocolVersion);
        channel = NetworkRegistry.ChannelBuilder.named(name).networkProtocolVersion(() -> protVersion)
            .clientAcceptedVersions(version -> protVersion.equals(version) || !clientRequired)
            .serverAcceptedVersions(version -> protVersion.equals(version) || !serverRequired)
            .simpleChannel();
        channel.messageBuilder(PacketPayload.class, 0)
            .encoder(PacketPayload::write)
            .decoder(this::readPacket)
            .consumerNetworkThread((payload, context) -> {
                Player player = context.getDirection() == PlayNetworkDirection.PLAY_TO_SERVER ? context.getSender() : getPlayer();
                payload.process(player).ifPresent(context::enqueueWork);
                context.setPacketHandled(true);
            })
            .add();
    }

    @Override
    public void sendToServer(AbstractPacket packet)
    {
        channel.sendToServer(payload(packet));
    }

    @Override
    public void sendTo(AbstractPacket packet, ServerPlayer player)
    {
        channel.send(PacketDistributor.PLAYER.with(() -> player), payload(packet));
    }

    @Override
    public void sendToAll(AbstractPacket packet)
    {
        channel.send(PacketDistributor.ALL.noArg(), payload(packet));
    }

    @Override
    public void sendToTracking(AbstractPacket packet, Entity entity)
    {
        channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), payload(packet));
    }

    @Override
    public void sendToAround(AbstractPacket packet, ServerLevel world, double x, double y, double z, double radius)
    {
        channel.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(x, y, z, radius, world.dimension())), payload(packet));
    }
}
