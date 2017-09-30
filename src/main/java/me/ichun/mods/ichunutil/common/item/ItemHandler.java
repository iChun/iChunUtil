package me.ichun.mods.ichunutil.common.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class ItemHandler
{
    public static HashMap<Class<? extends Item>, DualHandedItemCallback> dualHandedItems = new HashMap<>();

    //Items registered here can never be allowed to use MC's default "use timer". Their use timer (getMaxItemUseDuration) must be set to Integer.MAX_VALUE.
    public static void registerDualHandedItem(@Nonnull Class<? extends Item> clz, DualHandedItemCallback itemCallback)
    {
        dualHandedItems.put(clz, itemCallback != null ? itemCallback : DualHandedItemCallback.DEFAULT);
    }

    public static void registerDualHandedItem(@Nonnull Class<? extends Item> clz)
    {
        registerDualHandedItem(clz, null);
    }

    public static DualHandedItemCallback getDualHandedItemCallback(ItemStack is)
    {
        for(Map.Entry<Class<? extends Item>, DualHandedItemCallback> e : dualHandedItems.entrySet())
        {
            if(e.getKey().isInstance(is.getItem()))
            {
                return e.getValue();
            }
        }
        return DualHandedItemCallback.DEFAULT;
    }

    public static boolean isItemDualHanded(ItemStack is)
    {
        if(is.isEmpty())
        {
            return false;
        }
        for(Map.Entry<Class<? extends Item>, DualHandedItemCallback> e : dualHandedItems.entrySet())
        {
            if(e.getKey().isInstance(is.getItem()))
            {
                return e.getValue().isItemDualHanded(is);
            }
        }
        return false;
    }

    public static boolean canItemBeUsed(EntityLivingBase living, ItemStack is)
    {
        return is != null && (!isItemDualHanded(is) || getDualHandedItemCallback(is).canItemBeUsed(is, living) && (living.getHeldItem(EnumHand.MAIN_HAND) == is && living.getHeldItem(EnumHand.OFF_HAND).isEmpty() || living.getHeldItem(EnumHand.OFF_HAND) == is && living.getHeldItem(EnumHand.MAIN_HAND).isEmpty()));
    }

    public static @Nonnull ItemStack getUsableDualHandedItem(EntityLivingBase living) //returns null if item cannot be used.
    {
        ItemStack is = living.getHeldItem(EnumHand.MAIN_HAND);
        if(isItemDualHanded(is) && canItemBeUsed(living, is))
        {
            return is;
        }
        is = living.getHeldItem(EnumHand.OFF_HAND);
        if(isItemDualHanded(is) && canItemBeUsed(living, is))
        {
            return is;
        }
        return ItemStack.EMPTY;
    }

    public static EnumHandSide getHandSide(EntityLivingBase living, ItemStack is)
    {
        return living.getHeldItem(EnumHand.OFF_HAND) == is ? living.getPrimaryHand().opposite() : living.getPrimaryHand();
    }
}
