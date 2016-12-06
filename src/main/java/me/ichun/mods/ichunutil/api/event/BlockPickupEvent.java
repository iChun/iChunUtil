package me.ichun.mods.ichunutil.api.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

import java.util.Collection;

/**
 * This event is fired when a {@link me.ichun.mods.ichunutil.common.entity.EntityBlock} is created, converting blocks into an entity form.<br>
 * This event can be cancelled to prevent EntityBlock formation. If your block is in the way, cancel the event or remove it from the list.
 */
@Cancelable
public class BlockPickupEvent extends LivingEvent
{
    private final ItemStack itemStack;
    private final Collection<BlockPos> poses;

    public BlockPickupEvent(EntityLivingBase entity, ItemStack itemStack, Collection<BlockPos> poses)
    {
        super(entity);
        this.poses = poses;
        this.itemStack = itemStack;
    }

    public World getWorld()
    {
        return getEntity().worldObj;
    }

    /**
     * Item that entity currently holds
     */
    public ItemStack getItemStack()
    {
        return itemStack;
    }

    /**
     * The list of BlockPos that will be merged into the one EntityBlock. This list can be modified.
     */
    public Collection<BlockPos> getBlocks()
    {
        return poses;
    }
}
