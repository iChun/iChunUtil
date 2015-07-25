package us.ichun.mods.ichunutil.common.core.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderAtPlayerEvent extends Event
{
    public final double x;
    public final double y;
    public final double z;
    public final float renderTick;

    public RenderAtPlayerEvent(double x, double y, double z, float renderTick)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.renderTick = renderTick;
    }
}
