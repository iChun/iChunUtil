package me.ichun.mods.ichunutil.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.HashSet;

public class ItemHandler
{
    public static HashSet<Class<? extends Item>> dualHandedItems = new HashSet<>();

    //Items registered here can never be allowed to use MC's default "use timer". Their use timer (getMaxItemUseDuration) must be set to Integer.MAX_VALUE.
    public static void registerDualHandedItem(@Nonnull Class<? extends Item>clz)
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

    public static boolean canItemBeUsed(EntityPlayer player, ItemStack is)
    {
        return is != null && (!isItemDualHanded(is.getItem()) || player.inventory.mainInventory[player.inventory.currentItem] == is && player.inventory.offHandInventory[0] == null || player.inventory.offHandInventory[0] == is && player.inventory.mainInventory[player.inventory.currentItem] == null);
    }
}
