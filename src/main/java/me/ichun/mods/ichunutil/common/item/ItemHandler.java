package me.ichun.mods.ichunutil.common.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;

import javax.annotation.Nonnull;
import java.util.HashSet;

public class ItemHandler
{
    public static HashSet<Class<? extends Item>> dualHandedItems = new HashSet<>();

    //Items registered here can never be allowed to use MC's default "use timer". Their use timer (getMaxItemUseDuration) must be set to Integer.MAX_VALUE.
    public static void registerDualHandedItem(@Nonnull Class<? extends Item> clz)
    {
        dualHandedItems.add(clz);
    }

    public static boolean isItemDualHanded(Item item)
    {
        for(Class<? extends Item> clz : dualHandedItems)
        {
            if(clz.isInstance(item))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean canItemBeUsed(EntityLivingBase living, @Nonnull ItemStack is)
    {
        return !is.isEmpty() && (!isItemDualHanded(is.getItem()) || living.getHeldItem(EnumHand.MAIN_HAND) == is && living.getHeldItem(EnumHand.OFF_HAND).isEmpty() || living.getHeldItem(EnumHand.OFF_HAND) == is && living.getHeldItem(EnumHand.MAIN_HAND).isEmpty());
    }

    @Nonnull
    public static ItemStack getUsableDualHandedItem(EntityLivingBase living) //returns null if item cannot be used.
    {
        ItemStack is = living.getHeldItem(EnumHand.MAIN_HAND);
        if(!is.isEmpty() && isItemDualHanded(is.getItem()) && canItemBeUsed(living, is))
        {
            return is;
        }
        is = living.getHeldItem(EnumHand.OFF_HAND);
        if(!is.isEmpty() && isItemDualHanded(is.getItem()) && canItemBeUsed(living, is))
        {
            return is;
        }
        return ItemStack.EMPTY;
    }

    public static EnumHandSide getHandSide(EntityLivingBase living, @Nonnull ItemStack is)
    {
        return living.getHeldItem(EnumHand.OFF_HAND) == is ? living.getPrimaryHand().opposite() : living.getPrimaryHand();
    }
}
