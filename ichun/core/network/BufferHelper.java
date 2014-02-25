package ichun.core.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class BufferHelper
{
    public static void writeInt(ByteBuf buf, int i)
    {
        buf.writeInt(i);
    }

    public static int readInt(ByteBuf buf)
    {
        return buf.readInt();
    }

    public static void writeBoolean(ByteBuf buf, boolean flag)
    {
        buf.writeBoolean(flag);
    }

    public static boolean readBoolean(ByteBuf buf)
    {
        return buf.readBoolean();
    }

    public static void writeShort(ByteBuf buf, int i)
    {
        buf.writeShort(i);
    }

    public static short readShort(ByteBuf buf)
    {
        return buf.readShort();
    }

    public static void writeFloat(ByteBuf buf, float f)
    {
        buf.writeFloat(f);
    }

    public static float readFloat(ByteBuf buf)
    {
        return buf.readFloat();
    }

    public static void writeDouble(ByteBuf buf, double d)
    {
        buf.writeDouble(d);
    }

    public static double readDouble(ByteBuf buf)
    {
        return buf.readDouble();
    }

    public static void writeString(ByteBuf buf, String s)
    {
        ByteBufUtils.writeUTF8String(buf, s);
    }

    public static String readString(ByteBuf buf)
    {
        return ByteBufUtils.readUTF8String(buf);
    }

    public static void writeItemStack(ByteBuf buf, ItemStack is)
    {
        ByteBufUtils.writeItemStack(buf, is);
    }

    public static ItemStack readItemStack(ByteBuf buf)
    {
        return ByteBufUtils.readItemStack(buf);
    }

    public static void writeNBT(ByteBuf buf, NBTTagCompound tag)
    {
        ByteBufUtils.writeTag(buf, tag);
    }

    public static NBTTagCompound readNBT(ByteBuf buf)
    {
        return ByteBufUtils.readTag(buf);
    }
}
