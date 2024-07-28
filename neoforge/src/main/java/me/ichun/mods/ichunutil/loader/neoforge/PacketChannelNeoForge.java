package me.ichun.mods.ichunutil.loader.neoforge;

import me.ichun.mods.ichunutil.common.network.AbstractPacket;
import me.ichun.mods.ichunutil.common.network.PacketChannel;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class PacketChannelNeoForge extends PacketChannel
{
    public PacketChannelNeoForge(RegisterPayloadHandlerEvent event, ResourceLocation name, int protocolVersion, Class<? extends AbstractPacket>... packetTypes)
    {
        this(event, name, protocolVersion, false, packetTypes);
    }

    public PacketChannelNeoForge(RegisterPayloadHandlerEvent event, ResourceLocation name, int protocolVersion, boolean isOptional, Class<? extends AbstractPacket>... packetTypes)
    {
        super(name, packetTypes);

        IPayloadRegistrar registrar = event.registrar(channelId.getNamespace());
        registrar.versioned(Integer.toString(protocolVersion));

        if(isOptional)
        {
            registrar = registrar.optional();
        }

        registrar.play(channelId, // payload type - modid
            this::readPacket,
            this::handle
        );
    }

    protected void handle(PacketPayload payload, IPayloadContext context)
    {
        Player player = null;
        if(context.flow() == PacketFlow.CLIENTBOUND)
        {
            player = getPlayer();
        }
        else if(context.player().isPresent())
        {
            player = context.player().get();
        }
        payload.process(player).ifPresent(r -> context.workHandler().submitAsync(r));
    }

    @Override
    public void sendToServer(AbstractPacket packet)
    {
        PacketDistributor.SERVER.noArg().send(payload(packet));
    }

    @Override
    public void sendTo(AbstractPacket packet, ServerPlayer player)
    {
        PacketDistributor.PLAYER.with(player).send(payload(packet));
    }

    @Override
    public void sendToAll(AbstractPacket packet)
    {
        PacketDistributor.ALL.noArg().send(payload(packet));
    }

    @Override
    public void sendToTracking(AbstractPacket packet, Entity entity)
    {
        PacketDistributor.TRACKING_ENTITY_AND_SELF.with(entity).send(payload(packet));
    }

    @Override
    public void sendToAround(AbstractPacket packet, ServerLevel world, double x, double y, double z, double radius)
    {
        PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(x, y, z, radius, world.dimension())).send(payload(packet));
    }
}
