package us.ichun.mods.ichunutil.common.module.worldportals;

import net.minecraft.util.BlockPos;

public class WorldPortalLocation
{
    public final WorldPortalInfo info;
    public final BlockPos pos;

    public WorldPortalLocation(WorldPortalInfo info, BlockPos pos)
    {
        this.info = info;
        this.pos = pos;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof WorldPortalLocation)
        {
            WorldPortalLocation loc = (WorldPortalLocation)obj;
            return loc.info == this.info && loc.pos.getX() == this.pos.getX() && loc.pos.getY() == this.pos.getY() && loc.pos.getZ() == this.pos.getZ();
        }
        return false;
    }
}
