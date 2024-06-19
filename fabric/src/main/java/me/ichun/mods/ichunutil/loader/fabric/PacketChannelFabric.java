package me.ichun.mods.ichunutil.loader.fabric;

import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.network.AbstractPacket;
import me.ichun.mods.ichunutil.common.network.PacketChannel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;

public class PacketChannelFabric extends PacketChannel
{
    public MinecraftServer serverInstance;

    @SafeVarargs
    public PacketChannelFabric(ResourceLocation name, Class<? extends AbstractPacket>... packetTypes)
    {
        super(name, packetTypes);

        //Fabric doesn't do any network protocol checks.

        //receiving the packet
        CustomPacketPayload.Type<PacketPayload> type = new CustomPacketPayload.Type<>(channelId);
        StreamCodec<FriendlyByteBuf, PacketPayload> codec = createCodec();
        PayloadTypeRegistry.playS2C().register(type, codec);
        PayloadTypeRegistry.playC2S().register(type, codec);
        ServerPlayNetworking.registerGlobalReceiver(type,
            (payload, context) -> payload.process(context.player()).ifPresent(r -> context.server().execute(r)));
        if(FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT))
        {
            ClientClassloaderHaxor.registerClientReceiver(channelId);
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void sendToServer(AbstractPacket packet)
    {
        ClientPlayNetworking.send(payload(packet));
    }

    @Override
    public void sendTo(AbstractPacket packet, ServerPlayer player)
    {
        ServerPlayNetworking.send(player, payload(packet));
    }

    @Override
    public void sendToAll(AbstractPacket packet)
    {
        sendTo(packet, PlayerLookup.all(ServerListenerFabric.getServerInstance()));
    }

    @Override
    public void sendToTracking(AbstractPacket packet, Entity entity)
    {
        sendTo(packet, PlayerLookup.tracking(entity));
    }

    @Override
    public void sendToAround(AbstractPacket packet, ServerLevel world, double x, double y, double z, double radius)
    {
        sendTo(packet, PlayerLookup.around(world, new Vec3(x, y, z), radius));
    }

    private void sendTo(AbstractPacket packet, Collection<ServerPlayer> players)
    {
        for(ServerPlayer player : players)
        {
            ServerPlayNetworking.send(player, payload(packet));
        }
    }

    @Environment(EnvType.CLIENT)
    public static class ClientClassloaderHaxor
    {
        @Environment(EnvType.CLIENT)
        public static void registerClientReceiver(ResourceLocation channelId)
        {
            ClientPlayNetworking.registerGlobalReceiver(new CustomPacketPayload.Type<PacketPayload>(channelId),
                (payload, context) -> payload.process(iChunUtil.dC().getPlayer()).ifPresent(r -> context.client().execute(r)));
        }
    }
}
