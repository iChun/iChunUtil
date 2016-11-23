package me.ichun.mods.ichunutil.api.event;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

import java.util.List;

/**
 * This event is fired when a {@link me.ichun.mods.ichunutil.common.entity.EntityBlock} is created, converting blocks into an entity form.<br>
 * <br>
 * {@link #world} is the world for the entity.
 * {@link #poses} is the list of BlockPos that will be merged into the one EntityBlock. This list can be modified.
 * <br>
 * This event can be cancelled to prevent EntityBlock formation. If your block is in the way, cancel the event or remove it from the list.
 */
@Cancelable
public class BlockPickupEvent extends WorldEvent
{
    public final List<BlockPos> poses;

    public BlockPickupEvent(World world, List<BlockPos> poses)
    {
        super(world);
        this.poses = poses;
    }
}
