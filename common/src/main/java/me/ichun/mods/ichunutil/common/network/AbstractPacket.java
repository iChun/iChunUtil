package me.ichun.mods.ichunutil.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class AbstractPacket
{
    public abstract void writeTo(FriendlyByteBuf buf);
    public abstract void readFrom(FriendlyByteBuf buf);
    public abstract Optional<Runnable> process(@Nullable Player player); //done on networking thread.
}
