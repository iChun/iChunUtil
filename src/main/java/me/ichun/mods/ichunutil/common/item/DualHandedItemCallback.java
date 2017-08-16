package me.ichun.mods.ichunutil.common.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class DualHandedItemCallback
{
    public static final DualHandedItemCallback DEFAULT = new DualHandedItemCallback();

    public boolean isItemDualHanded(ItemStack is) { return true; }
    public boolean canItemBeUsed(ItemStack is, EntityLivingBase ent) { return true; }
    public boolean shouldItemBeHeldLikeBow(ItemStack is, EntityLivingBase ent) { return true; }
}
