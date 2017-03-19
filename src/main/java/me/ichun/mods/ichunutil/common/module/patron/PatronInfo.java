package me.ichun.mods.ichunutil.common.module.patron;

public class PatronInfo
{
    public final String id;
    public int effectType;
    public boolean showEffect;

    public PatronInfo(String id, int effectType, boolean showEffect)
    {
        this.id = id;
        this.effectType = effectType;
        this.showEffect = showEffect;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof PatronInfo && ((PatronInfo)obj).id.equals(id); //Check for just the ID. This is only used for ArrayList management.
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }
}
