package me.ichun.mods.ichunutil.common.util;

import com.google.gson.Gson;
import me.ichun.mods.ichunutil.client.gui.bns.Theme;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

public final class StringUtil
{
    public static final Gson GSON = new Gson();

    public static final Random RAND = new Random(); // "true" random
    public static final Random SEEDED_RAND = new Random(); // this random has its seed set before being used

    public static final int IDENTIFIER_LENGTH = 20; // My typical string length for an identifier.

    private static final HashMap<Class<?>, Function<Object, List<String>>> OBJECT_INTERPRETER = Util.make(new HashMap<>(), m -> {
        m.put(File.class, (o) -> {
            File file = (File)o;
            List<String> info = new ArrayList<>();
            info.add(file.getName());
            info.add((new SimpleDateFormat()).format(new Date(file.lastModified())));
            info.add(readableFileSize(file.length()));
            return info;
        });
        m.put(Path.class, (o) -> {
            Path path = (Path)o;
            if(Files.isDirectory(path))
            {
                List<String> info = new ArrayList<>();
                info.add(path.getFileName().toString());
                info.add("Folder");
                return info;
            }
            else
            {
                return getInterpretedInfo(path.toFile());
            }
        });
        m.put(Theme.class, (o) -> Collections.singletonList(((Theme)o).name + " - " + ((Theme)o).author));
        m.put(Entity.class, (o) -> Collections.singletonList(((Entity)o).getDisplayName().getString()));
        m.put(Class.class, (o) -> Collections.singletonList(((Class)o).getSimpleName()));
    });

    @NotNull
    public static List<String> getInterpretedInfo(Object o)
    {
        Map.Entry<Class<?>, Function<Object, List<String>>> lastEntryUsed = null;
        List<String> infos = null;
        for(Map.Entry<Class<?>, Function<Object, List<String>>> e : OBJECT_INTERPRETER.entrySet())
        {
            if(e.getKey().isInstance(o))
            {
                if(!(lastEntryUsed != null && e.getKey().isAssignableFrom(lastEntryUsed.getKey()))) // !(the last entry extends our current class)
                {
                    lastEntryUsed = e;
                    infos = e.getValue().apply(o);
                }
            }
        }
        if(infos == null)
        {
            infos = new ArrayList<>();
            infos.add(o.toString());
        }
        return infos;
    }

    public static void registerObjectInterpreter(Class<?> clz, Function<Object, List<String>> function)
    {
        OBJECT_INTERPRETER.put(clz, function);
    }

    public static String readableFileSize(long size)
    {
        if(size <= 0)
        {
            return "0";
        }
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB", "PB" };
        int digitGroups = (int)(Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static int getRandomColourForName(String s)
    {
        if(s.equalsIgnoreCase("System"))
        {
            return 0xffcc00;
        }
        else
        {
            return Math.abs(s.hashCode()) & 0xffffff;
        }
    }

    public static ChatFormatting getRandomTextFormattingColorForName(String s) //I know this can be cached but meh
    {
        if(s.equalsIgnoreCase("System"))
        {
            return ChatFormatting.RED;
        }

        ArrayList<ChatFormatting> formats = new ArrayList<>();
        for(int i = 1; i < 15; i++) // no black no red no white
        {
            if(i != 12) //no red
            {
                formats.add(ChatFormatting.values()[i]);
            }
        }
        SEEDED_RAND.setSeed(Math.abs(s.hashCode()));
        return formats.get(SEEDED_RAND.nextInt(formats.size()));
    }
}
