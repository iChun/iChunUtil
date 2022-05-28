package me.ichun.mods.ichunutil.loader.fabric.config;

import me.ichun.mods.ichunutil.common.network.AbstractPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class PacketConfig extends AbstractPacket
{
    public String fileName;
    public String tomlString;

    public PacketConfig(){}

    public PacketConfig(String fileName, String tomlString)
    {
        this.fileName = fileName;
        this.tomlString = tomlString;
    }

    @Override
    public void writeTo(FriendlyByteBuf buf)
    {
        buf.writeUtf(this.fileName);
        buf.writeUtf(this.tomlString);
    }

    @Override
    public void readFrom(FriendlyByteBuf buf)
    {
        this.fileName = buf.readUtf();
        this.tomlString = buf.readUtf();
    }

    @Override
    public Optional<Runnable> process(Player player)
    {
        return Optional.of(() -> FabricConfigLoader.receiveServerConfig(this.fileName, this.tomlString));
    }
}
