package me.ichun.mods.ichunutil.common.core.util;

import me.ichun.mods.ichunutil.common.block.BlockCompactPorkchop;
import me.ichun.mods.ichunutil.common.iChunUtil;

import java.lang.reflect.Field;

public class ObfHelper
{
    private static final String OBF_VERSION = "1.8.9";

    private static boolean isObfuscated;

    public static void obfWarning()
    {
        iChunUtil.LOGGER.warn("Error with some obfuscation!");
    }

    public static void detectObfuscation()
    {
        isObfuscated = true;
        try
        {
            Field[] fields = Class.forName("net.minecraft.item.ItemBlock").getDeclaredFields();
            for(Field f : fields)
            {
                f.setAccessible(true);
                if(f.getName().equalsIgnoreCase("block"))
                {
                    isObfuscated = false;
                    if(!iChunUtil.VERSION_OF_MC.equals(OBF_VERSION))
                    {
                        iChunUtil.LOGGER.warn("ObfHelper strings are not updated!");
                        throw new RuntimeException("Bad iChun! Update obfuscation strings!");
                    }
                    return;
                }
            }

            BlockCompactPorkchop.class.getDeclaredMethod("func_149722_s"); // will only reach here if in dev env, setBlockUnbreakable
        }
        catch(NoSuchMethodException e)
        {
            throw new RuntimeException("You're running the deobf version of iChunUtil in an obfuscated environment! Don't do this!");
        }
        catch (Exception ignored)
        {
        }
    }

    public static boolean obfuscated()
    {
        return isObfuscated;
    }
}
