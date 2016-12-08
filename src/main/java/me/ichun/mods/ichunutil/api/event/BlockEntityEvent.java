package me.ichun.mods.ichunutil.api.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

import java.util.Collection;

/**
 * BlockEntityEvent is fired when something significant relating to {@link me.ichun.mods.ichunutil.common.entity.EntityBlock} is done.<br>
 * <br>
 * {@link #world} is the world for the entity.<br>
 * <br>
 * All children of this event are fired on the {@link net.minecraftforge.common.MinecraftForge#EVENT_BUS}.<br>
 *
 */
public class BlockEntityEvent extends WorldEvent
{
    public BlockEntityEvent(World world)
    {
        super(world);
    }

    /**
     * BlockEntityEvent.Pickup is fired when an entity picks up blocks, creating a {@link me.ichun.mods.ichunutil.common.entity.EntityBlock}.<br>
     * <br>
     * {@link #living} is the EntityLivingBase that's picking up the blocks.<br>
     * {@link #poses} is the list of BlockPos that will be merged into the one EntityBlock. This list can be modified.<br>
     * <br>
     * This event can be cancelled to prevent EntityBlock formation. If your block is in the way, cancel the event or remove it from the list.<br>
     */
    @Cancelable
    public static class Pickup extends BlockEntityEvent
    {
        private final EntityLivingBase living;
        private final Collection<BlockPos> poses;

        public Pickup(World world, EntityLivingBase living, Collection<BlockPos> poses)
        {
            super(world);
            this.living = living;
            this.poses = poses;
        }

        public EntityLivingBase getEntityLiving()
        {
            return living;
        }

        public Collection<BlockPos> getPoses()
        {
            return poses;
        }
    }

    /**
     * BlockEntityEvent.Place is fired when {@link me.ichun.mods.ichunutil.common.entity.EntityBlock} tries to place its blocks in the world.<br>
     * <br>
     * {@link #blockEntity} is the EntityBlock reference. Just there if you want to reference it.<br>
     * {@link #living} is the EntityLivingBase that's initially picked up the block. This can be null. This is also not saved, and will be null after a World load.<br>
     * {@link #blockState} is the IBlockState that will be placed.<br>
     * {@link #pos} is the BlockPos where the block is being placed.<br>
     * <br>
     * This event can be cancelled to prevent Block placement, and will go to EntityBlock's fallback of dropping the block as an item instead, and its contents if the block is an IInventory or IItemHandler<br>
     */
    @Cancelable
    public static class Place extends BlockEntityEvent
    {
        private final Entity blockEntity;
        private final EntityLivingBase living;
        private final IBlockState blockState;
        private final BlockPos pos;

        public Place(World world, Entity blockEntity, EntityLivingBase living, IBlockState blockState, BlockPos blockPos)
        {
            super(world);
            this.blockEntity = blockEntity;
            this.living = living;
            this.blockState = blockState;
            this.pos = blockPos;
        }

        public Entity getBlockEntity()
        {
            return blockEntity;
        }

        public EntityLivingBase getEntityLiving()
        {
            return living;
        }

        public IBlockState getBlockState()
        {
            return blockState;
        }

        public BlockPos getPos()
        {
            return pos;
        }
    }
}
