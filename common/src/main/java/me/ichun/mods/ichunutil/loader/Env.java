package me.ichun.mods.ichunutil.loader;

public enum Env
{
    FORGE,
    FABRIC,
    NEOFORGE,
    ALL;

    public boolean isFabric()
    {
        return this == FABRIC;
    }

    public boolean isForge()
    {
        return this == FORGE;
    }

    public boolean isNeoForge()
    {
        return this == NEOFORGE;
    }
}
