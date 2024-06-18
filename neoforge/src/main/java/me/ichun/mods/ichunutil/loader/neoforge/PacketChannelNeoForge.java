package me.ichun.mods.ichunutil.loader.neoforge;

import me.ichun.mods.ichunutil.common.network.AbstractPacket;
import me.ichun.mods.ichunutil.common.network.PacketChannel;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketChannelNeoForge extends PacketChannel
{
    public PacketChannelNeoForge(RegisterPayloadHandlersEvent event, ResourceLocation name, int protocolVersion, Class<? extends AbstractPacket>... packetTypes)
    {
        this(event, name, protocolVersion, false, packetTypes);
    }

    public PacketChannelNeoForge(RegisterPayloadHandlersEvent event, ResourceLocation name, int protocolVersion, boolean isOptional, Class<? extends AbstractPacket>... packetTypes)
    {
        super(name, packetTypes);

        PayloadRegistrar registrar = event.registrar(channelId.toString()) // version number
            .executesOn(HandlerThread.NETWORK)
            .versioned(Integer.toString(protocolVersion));

        if(isOptional)
        {
            registrar = registrar.optional();
        }

        registrar.playBidirectional(new CustomPacketPayload.Type<>(channelId), // payload type - modid
                createCodec(),
                this::handle
            );
    }

    protected void handle(PacketPayload payload, IPayloadContext context)
    {
        Player player = context.player();
        payload.process(player).ifPresent(context::enqueueWork);
    }

    @Override
    public void sendToServer(AbstractPacket packet)
    {
        PacketDistributor.sendToServer(payload(packet));
    }

    @Override
    public void sendTo(AbstractPacket packet, ServerPlayer player)
    {
        PacketDistributor.sendToPlayer(player, payload(packet));
    }

    @Override
    public void sendToAll(AbstractPacket packet)
    {
        PacketDistributor.sendToAllPlayers(payload(packet));
    }

    @Override
    public void sendToTracking(AbstractPacket packet, Entity entity)
    {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, payload(packet));
    }

    @Override
    public void sendToAround(AbstractPacket packet, ServerLevel world, double x, double y, double z, double radius)
    {
        PacketDistributor.sendToPlayersNear(world, null, x, y, z, radius, payload(packet));
    }
}
