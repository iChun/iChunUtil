package me.ichun.mods.ichunutil.loader;

public enum Side
{
    CLIENT,
    SERVER;

    public boolean isClient()
    {
        return !this.isServer();
    }

    public boolean isServer()
    {
        return this == SERVER;
    }

}
