package us.ichun.mods.ichunutil.common.core.patron;

public class PatronInfo
{
    public final String id;
    public int type;

    public PatronInfo(String id)
    {
        this.id = id;
    }

    public PatronInfo setType(int i)
    {
        type = i;
        return this;
    }
}
