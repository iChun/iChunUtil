package me.ichun.mods.ichunutil.common.util;

import me.ichun.mods.ichunutil.loader.LoaderHandler;

public class ObfHelper
{
    private static final String OBF_VERSION = "1.18.2";
    private static boolean devEnvironment;

    public static void detectDevEnvironment()
    {
        devEnvironment = LoaderHandler.d().isDevEnvironment();
    }
    public static boolean isDevEnvironment()
    {
        return devEnvironment;
    }
}
