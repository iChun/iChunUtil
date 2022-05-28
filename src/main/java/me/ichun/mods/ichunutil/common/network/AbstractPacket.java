package me.ichun.mods.ichunutil.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public abstract class AbstractPacket //Yes I know we could be an interface.
{
    public abstract void writeTo(FriendlyByteBuf buf);
    public abstract void readFrom(FriendlyByteBuf buf);
    public abstract Optional<Runnable> process(Player player); //done on networking thread. BlockableEventLoop is usually the client or server object. Player is the Player object who sent the packet on the server and the Minecraft Player on the client.
}
