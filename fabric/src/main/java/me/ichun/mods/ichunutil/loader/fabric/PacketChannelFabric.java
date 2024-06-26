package me.ichun.mods.ichunutil.loader.fabric;

import me.ichun.mods.ichunutil.common.network.AbstractPacket;
import me.ichun.mods.ichunutil.common.network.PacketChannel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
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
        ServerPlayNetworking.registerGlobalReceiver(channelId, (server, player, handler, buffer, responseSender) -> {
            readPacket(buffer).process(player).ifPresent(server::execute);
        });
        if(FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT))
        {
            ClientClassloaderHaxor.registerClientReceiver(this, channelId);
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void sendToServer(AbstractPacket packet)
    {
        ClientPlayNetworking.send(channelId, asBuffer(payload(packet)));
    }

    @Override
    public void sendTo(AbstractPacket packet, ServerPlayer player)
    {
        ServerPlayNetworking.send(player, channelId, asBuffer(payload(packet)));
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
            ServerPlayNetworking.send(player, channelId, asBuffer(payload(packet)));
        }
    }

    private FriendlyByteBuf asBuffer(PacketPayload packet)
    {
        FriendlyByteBuf buffer = PacketByteBufs.create();
        packet.write(buffer);
        return buffer;
    }

    @Environment(EnvType.CLIENT)
    public static class ClientClassloaderHaxor
    {
        @Environment(EnvType.CLIENT)
        public static void registerClientReceiver(PacketChannelFabric channel, ResourceLocation channelId)
        {
            ClientPlayNetworking.registerGlobalReceiver(channelId, (client, handler, buffer, responseSender) -> {
                channel.readPacket(buffer).process(client.player).ifPresent(client::execute);
            });
        }
    }
}
