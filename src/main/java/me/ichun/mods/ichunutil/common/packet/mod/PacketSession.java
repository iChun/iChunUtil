package me.ichun.mods.ichunutil.common.packet.mod;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.core.config.types.Colour;
import me.ichun.mods.ichunutil.common.core.config.types.NestedIntArray;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class PacketSession extends AbstractPacket
{
    public ConfigBase config;
    public String modId;
    public HashMap<String, Object> vars;

    public PacketSession() {}

    public PacketSession(ConfigBase config)
    {
        this.config = config;
    }

    @Override
    public void writeTo(ByteBuf buffer)
    {
        ByteBufUtils.writeUTF8String(buffer, config.getModId());

        for(Map.Entry<Field, Object> e : config.session.entrySet())
        {
            ByteBufUtils.writeUTF8String(buffer, e.getKey().getName());

            Object obj = e.getValue();
            Class clz = obj.getClass();
            if(clz.equals(int.class) || clz.equals(Integer.class))
            {
                buffer.writeInt(0);// TYPE

                buffer.writeInt((Integer)obj);
            }
            else if(clz.equals(int[].class))
            {
                buffer.writeInt(1);// TYPE

                int[] ints = (int[])obj;
                buffer.writeInt(ints.length);
                for(int i = 0; i < ints.length; i++)
                {
                    buffer.writeInt(ints[i]);
                }
            }
            else if(clz.equals(NestedIntArray.class))
            {
                buffer.writeInt(2);// TYPE

                ByteBufUtils.writeUTF8String(buffer, ((NestedIntArray)obj).serialize());

            }
            else if(clz.equals(Colour.class))
            {
                buffer.writeInt(3);// TYPE

                ByteBufUtils.writeUTF8String(buffer, ((Colour)obj).serialize());
            }
            else if(clz.equals(String.class))
            {
                buffer.writeInt(4);// TYPE

                ByteBufUtils.writeUTF8String(buffer, ((String)obj));
            }
            else if(clz.equals(String[].class))
            {
                buffer.writeInt(5);// TYPE

                String[] strings = (String[])obj;
                buffer.writeInt(strings.length);
                for(int i = 0; i < strings.length; i++)
                {
                    ByteBufUtils.writeUTF8String(buffer, strings[i]);
                }
            }
            else
            {
                throw new RuntimeException("Trying to send an unsupported field for a session: " + clz.getName());
            }
        }
        ByteBufUtils.writeUTF8String(buffer, "##endPacket");
    }

    @Override
    public void readFrom(ByteBuf buffer)
    {
        modId = ByteBufUtils.readUTF8String(buffer);
        vars = new HashMap<>();

        String var = ByteBufUtils.readUTF8String(buffer);
        while(!var.equals("##endPacket"))
        {
            int type = buffer.readInt();

            if(type == 0)
            {
                vars.put(var, buffer.readInt());
            }
            else if(type == 1)
            {
                int[] ints = new int[buffer.readInt()];
                for(int i = 0; i < ints.length; i++)
                {
                    ints[i] = buffer.readInt();
                }

                vars.put(var, ints);
            }
            else if(type == 2)
            {
                NestedIntArray ints = new NestedIntArray(new TreeMap<Integer, ArrayList<Integer>>());
                ints.deserialize(ByteBufUtils.readUTF8String(buffer), Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);

                vars.put(var, ints);
            }
            else if(type == 3)
            {
                Colour clr = new Colour(0);
                clr.deserialize(ByteBufUtils.readUTF8String(buffer));

                vars.put(var, clr);
            }
            else if(type == 4)
            {
                vars.put(var, ByteBufUtils.readUTF8String(buffer));
            }
            else if(type == 5)
            {
                String[] ints = new String[buffer.readInt()];
                for(int i = 0; i < ints.length; i++)
                {
                    ints[i] = ByteBufUtils.readUTF8String(buffer);
                }

                vars.put(var, ints);
            }

            var = ByteBufUtils.readUTF8String(buffer);
        }
    }

    @Override
    public AbstractPacket execute(Side side, EntityPlayer player)
    {
        for(ConfigBase conf : ConfigHandler.configs)
        {
            if(conf.getModId().equals(modId))
            {
                for(Field field : conf.sessionProp)
                {
                    try
                    {
                        field.setAccessible(true);
                        if(vars.containsKey(field.getName()))
                        {
                            Object ori = field.get(conf);
                            field.set(conf, vars.get(field.getName()));
                            conf.onSessionChange(field, ori);
                        }
                    }
                    catch(Exception ignored)
                    {
                    }
                }
                conf.onReceiveSession();
                break;
            }
        }
        return null;
    }

    @Override
    public Side receivingSide() { return Side.CLIENT; }

    @Override
    public boolean requiresMainThread() { return false; }

    ;
}
