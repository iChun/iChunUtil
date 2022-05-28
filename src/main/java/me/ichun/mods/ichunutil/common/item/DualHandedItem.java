package me.ichun.mods.ichunutil.common.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;

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
        return !isItemDualHanded(is) || ((DualHandedItem)is.getItem()).canBeUsed(is, living) && (living.getItemInHand(InteractionHand.MAIN_HAND) == is && living.getItemInHand(InteractionHand.OFF_HAND).isEmpty() || living.getItemInHand(InteractionHand.OFF_HAND) == is && living.getItemInHand(InteractionHand.MAIN_HAND).isEmpty());
    }

    @Nonnull
    static ItemStack getUsableDualHandedItem(@Nonnull LivingEntity living) //returns EMPTY if item cannot be used.
    {
        ItemStack is = living.getItemInHand(InteractionHand.MAIN_HAND);
        if(isItemDualHanded(is) && canItemBeUsed(living, is))
        {
            return is;
        }
        is = living.getItemInHand(InteractionHand.OFF_HAND);
        if(isItemDualHanded(is) && canItemBeUsed(living, is))
        {
            return is;
        }
        return ItemStack.EMPTY;
    }

    static HumanoidArm getHandSide(LivingEntity living, ItemStack is)
    {
        //No HandSide.opposite() on servers.
        return living.getItemInHand(InteractionHand.OFF_HAND) == is ? (living.getMainArm() == HumanoidArm.RIGHT ? HumanoidArm.LEFT : HumanoidArm.RIGHT) : living.getMainArm();
    }

}
