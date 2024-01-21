package me.ichun.mods.ichunutil.loader;

import me.ichun.mods.ichunutil.loader.fabric.LoaderDelegateFabric;
import org.jetbrains.annotations.NotNull;

// This class is here in case other mods try to access iChunUtil's util stuff before iChunUtil initialises.
public class LoaderHandler
{
    @NotNull private static final Env ENV; //Not sure if defaulting to false is a good idea
    @NotNull private static final LoaderDelegate DELEGATE;

    /*
     * We check if Fabric's loader is available in the classloader
     *
     * "Static block is also known as static initialization block or static initializer block in Java.
     *
     * The static block gets executed only once by JVM when the class is loaded into the memory by Java ClassLoader."
     *
     * Yes, I wanted to look that up to make sure.
     */
    static
    {
        Env thisEnv;
        LoaderDelegate loaderDelegate = null;
        try
        {
            Class.forName("net.fabricmc.loader.impl.FabricLoaderImpl");
            thisEnv = Env.FABRIC;
            loaderDelegate = new LoaderDelegateFabric();
        }
        catch(ClassNotFoundException e)
        {
            thisEnv = Env.FORGE;
            //TODO Forge delegate
        }
        ENV = thisEnv;
        DELEGATE = loaderDelegate;
    }

    public static boolean isFabricEnv()
    {
        return ENV == Env.FABRIC;
    }

    public static Env getEnv()
    {
        return ENV;
    }

    @NotNull
    public static LoaderDelegate d()
    {
        return DELEGATE;
    }

    private LoaderHandler(){}

    public enum Env
    {
        ALL,
        FORGE,
        FABRIC
    }
}
