package me.ichun.mods.ichunutil.common.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;

import java.util.EnumMap;
import java.util.HashMap;

/**
 * Generic data fragment class. Used by child mods to send files larger than 32kb to the server or vice versa.
 * Handling to be done by the mod class.
 */
public abstract class PacketDataFragment extends AbstractPacket
{
    public String fileName;
    public short packetTotal;
    public short packetNumber;
    public byte[] data;

    public PacketDataFragment() {}

    public PacketDataFragment(String fileName, int packetTotal, int packetNumber, byte[] data)
    {
        this.fileName = fileName;
        this.packetTotal = (short)packetTotal;
        this.packetNumber = (short)packetNumber;
        this.data = data;
    }

    @Override
    public void writeTo(PacketBuffer buffer)
    {
        buffer.writeString(fileName);
        buffer.writeShort(packetTotal);
        buffer.writeShort(packetNumber);
        buffer.writeInt(data.length);
        buffer.writeBytes(data);
    }

    @Override
    public void readFrom(PacketBuffer buffer)
    {
        fileName = buffer.readString(32767);
        packetTotal = buffer.readShort();
        packetNumber = buffer.readShort();

        data = new byte[buffer.readInt()];

        buffer.readBytes(data);
    }

    public byte[] process(LogicalSide side) //returns null if not complete, data if complete.
    {
        HashMap<String, byte[][]> sidedFiles = SIDED_PARTIAL_DATA.computeIfAbsent(side, v -> new HashMap<>());
        byte[][] packets = sidedFiles.computeIfAbsent(fileName, v -> new byte[packetTotal][]);
        packets[packetNumber] = data;

        boolean complete = true;
        for(byte[] b : packets)
        {
            if(b == null || b.length == 0)
            {
                complete = false;
                break;
            }
        }
        if(complete)
        {
            int size = 0;
            for(int i = 0; i < packets.length; i++)
            {
                size += packets[i].length;
            }
            byte[] fileData = new byte[size];

            int index = 0;
            for(int i = 0; i < packets.length; i++)
            {
                System.arraycopy(packets[i], 0, fileData, index, packets[i].length);
                index += packets[i].length;
            }

            sidedFiles.remove(fileName);

            return fileData;
        }
        return null;
    }

    public static final EnumMap<LogicalSide, HashMap<String, byte[][]>> SIDED_PARTIAL_DATA = new EnumMap<>(LogicalSide.class);
}
