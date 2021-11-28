package me.ichun.mods.ichunutil.common.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;

import javax.annotation.Nonnull;

public interface DualHandedItem
{
    default boolean isDualHanded(@Nonnull ItemStack is) { return true; }
    default boolean canBeUsed(@Nonnull ItemStack is, @Nonnull LivingEntity living) { return true; }
    default boolean isHeldLikeBow(@Nonnull ItemStack is, @Nonnull LivingEntity living) { return true; }

    static boolean isItemDualHanded(@Nonnull ItemStack is)
    {
        return is.getItem() instanceof DualHandedItem && ((DualHandedItem)is.getItem()).isDualHanded(is);
    }

    static boolean canItemBeUsed(@Nonnull LivingEntity living, @Nonnull ItemStack is)
    {
        return !isItemDualHanded(is) || ((DualHandedItem)is.getItem()).canBeUsed(is, living) && (living.getHeldItem(Hand.MAIN_HAND) == is && living.getHeldItem(Hand.OFF_HAND).isEmpty() || living.getHeldItem(Hand.OFF_HAND) == is && living.getHeldItem(Hand.MAIN_HAND).isEmpty());
    }

    @Nonnull
    static ItemStack getUsableDualHandedItem(@Nonnull LivingEntity living) //returns EMPTY if item cannot be used.
    {
        ItemStack is = living.getHeldItem(Hand.MAIN_HAND);
        if(isItemDualHanded(is) && canItemBeUsed(living, is))
        {
            return is;
        }
        is = living.getHeldItem(Hand.OFF_HAND);
        if(isItemDualHanded(is) && canItemBeUsed(living, is))
        {
            return is;
        }
        return ItemStack.EMPTY;
    }

    static HandSide getHandSide(LivingEntity living, ItemStack is)
    {
        //No HandSide.opposite() on servers.
        return living.getHeldItem(Hand.OFF_HAND) == is ? (living.getPrimaryHand() == HandSide.RIGHT ? HandSide.LEFT : HandSide.RIGHT) : living.getPrimaryHand();
    }

}
