package us.ichun.mods.ichunutil.common.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;

import java.util.Collections;
import java.util.Set;

public class FakeNetHandlerPlayServer extends NetHandlerPlayServer
{
    public FakeNetHandlerPlayServer(MinecraftServer server, NetworkManager networkManagerIn, EntityPlayerMP playerIn)
    {
        super(server, networkManagerIn, playerIn);
    }

    @Override
    public void update()
    {
    }

    @Override
    public void kickPlayerFromServer(String reason)
    {
    }

    @Override
    public void processInput(C0CPacketInput packetIn)
    {
    }

    @Override
    public void processPlayer(C03PacketPlayer packetIn)
    {
    }

    @Override
    public void setPlayerLocation(double x, double y, double z, float yaw, float pitch)
    {
        this.setPlayerLocation(x, y, z, yaw, pitch, Collections.emptySet());
    }

    @Override
    public void setPlayerLocation(double x, double y, double z, float yaw, float pitch, Set relativeSet)
    {
        this.playerEntity.setPositionAndRotation(x, y, z, yaw, pitch);
    }

    @Override
    public void processPlayerDigging(C07PacketPlayerDigging packetIn)
    {
    }

    @Override
    public void processPlayerBlockPlacement(C08PacketPlayerBlockPlacement packetIn)
    {
    }

    @Override
    public void handleSpectate(C18PacketSpectate packetIn)
    {
    }

    @Override
    public void handleResourcePackStatus(C19PacketResourcePackStatus packetIn) {}

    @Override
    public void onDisconnect(IChatComponent reason)
    {
    }

    @Override
    public void sendPacket(final Packet packetIn)
    {
    }

    @Override
    public void processHeldItemChange(C09PacketHeldItemChange packetIn)
    {
    }

    @Override
    public void processChatMessage(C01PacketChatMessage packetIn)
    {
    }

    @Override
    public void handleAnimation(C0APacketAnimation packetIn)
    {
    }

    @Override
    public void processEntityAction(C0BPacketEntityAction packetIn)
    {
    }

    @Override
    public void processUseEntity(C02PacketUseEntity packetIn)
    {
    }

    @Override
    public void processClientStatus(C16PacketClientStatus packetIn)
    {
    }

    @Override
    public void processCloseWindow(C0DPacketCloseWindow packetIn)
    {
    }

    @Override
    public void processClickWindow(C0EPacketClickWindow packetIn)
    {
    }

    @Override
    public void processEnchantItem(C11PacketEnchantItem packetIn)
    {
    }

    @Override
    public void processCreativeInventoryAction(C10PacketCreativeInventoryAction packetIn)
    {
    }

    @Override
    public void processConfirmTransaction(C0FPacketConfirmTransaction packetIn)
    {
    }

    @Override
    public void processUpdateSign(C12PacketUpdateSign packetIn)
    {
    }

    @Override
    public void processKeepAlive(C00PacketKeepAlive packetIn)
    {
    }

    @Override
    public void processPlayerAbilities(C13PacketPlayerAbilities packetIn)
    {
    }

    @Override
    public void processTabComplete(C14PacketTabComplete packetIn)
    {
    }

    @Override
    public void processClientSettings(C15PacketClientSettings packetIn)
    {
    }

    @Override
    public void processVanilla250Packet(C17PacketCustomPayload packetIn)
    {
    }
}
