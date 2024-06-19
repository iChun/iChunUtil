package me.ichun.mods.ichunutil.loader.forge;

import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.network.AbstractPacket;
import me.ichun.mods.ichunutil.common.network.PacketChannel;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public class PacketChannelForge extends PacketChannel
{
    private final SimpleChannel channel;

    @SafeVarargs
    public PacketChannelForge(ResourceLocation name, int protocolVersion, Class<? extends AbstractPacket>...packetTypes)
    {
        this(name, protocolVersion, true, true, packetTypes);
    }

    @SafeVarargs
    public PacketChannelForge(ResourceLocation name, int protocolVersion, boolean clientRequired, boolean serverRequired, Class<? extends AbstractPacket>...packetTypes)
    {
        super(name, packetTypes);

        ChannelBuilder channelBuilder = ChannelBuilder.named(name).networkProtocolVersion(protocolVersion);
        if(!clientRequired)
        {
            channelBuilder = channelBuilder.optionalClient();
        }
        if(!serverRequired)
        {
            channelBuilder = channelBuilder.optionalServer();
        }
        channel = channelBuilder.simpleChannel();
        channel.messageBuilder(PacketPayload.class)
            .codec(createCodec())
            .consumerNetworkThread((payload, context) -> {
                Player player = context.isServerSide() ? context.getSender() : iChunUtil.dC().getPlayer();
                payload.process(player).ifPresent(context::enqueueWork);
                context.setPacketHandled(true);
            })
            .add();
    }

    @Override
    public void sendTo(AbstractPacket packet, ServerPlayer player)
    {
        channel.send(payload(packet), PacketDistributor.PLAYER.with(player));
    }

    @Override
    public void sendToAll(AbstractPacket packet)
    {
        channel.send(payload(packet), PacketDistributor.ALL.noArg());
    }

    @Override
    public void sendToTracking(AbstractPacket packet, Entity entity)
    {
        channel.send(payload(packet), PacketDistributor.TRACKING_ENTITY_AND_SELF.with(entity));
    }

    @Override
    public void sendToAround(AbstractPacket packet, ServerLevel world, double x, double y, double z, double radius)
    {
        channel.send(payload(packet), PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(x, y, z, radius, world.dimension())));
    }

    @Override
    public void sendToServer(AbstractPacket packet)
    {
        if(FMLEnvironment.dist.isClient())
        {
            sendToServerImpl(packet);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void sendToServerImpl(AbstractPacket packet)
    {
        channel.send(payload(packet), Minecraft.getInstance().getConnection().getConnection());
    }
}
