package me.ichun.mods.ichunutil.loader.neoforge;

import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class is just to trigger errors in case NeoForge changes any packages/classes/methods/fields
 */
@SuppressWarnings("all")
public final class ReflectionReferenceNeoForge
{
    private static final String lastUpdated = "1.20.6-20.6.18";

    //methods
    private static final Method setKeyConflictContext;

    static
    {
        try
        {
            setKeyConflictContext = KeyMapping.class.getDeclaredMethod("setKeyConflictContext", IKeyConflictContext.class);
            setKeyConflictContext.setAccessible(true);
        }
        catch(NoSuchMethodException e)
        {
            throw new RuntimeException("iChun forgot to update the reflection references! Go yell at him!", e);
        }
    }

    public static void setKeyConflictContext(KeyMapping key, IKeyConflictContext keyConflict)
    {
        invokeMethod(setKeyConflictContext, key, keyConflict);
    }

    private static void invokeMethod(Method m, Object target, Object...params)
    {
        try
        {
            m.invoke(target, params);
        }
        catch(IllegalAccessException | InvocationTargetException e)
        {
            throw new RuntimeException("An error occurred trying to invoke method!", e);
        }
    }
}
