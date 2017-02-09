package me.ichun.mods.ichunutil.client.render.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

import javax.annotation.Nullable;
import java.util.HashSet;

public class ItemRenderingHelper
{
    public static HashSet<Class<? extends Item>> bowAnimationLockedItems = new HashSet<>();
    public static int lastThirdPersonView;

    public static HashSet<SwingProofHandler> swingProofItems = new HashSet<>();

    private static int prevCurItem;
    private static boolean currentItemIsSwingProof;
    private static boolean hasShownItemName;

    public static class SwingProofHandler
    {
        public final Class<? extends Item> clz;
        public final @Nullable IItemEquippedHandler hnd;
        public SwingProofHandler(Class<? extends Item> clz, IItemEquippedHandler hnd)
        {
            this.clz = clz;
            this.hnd = hnd;
        }

        public interface IItemEquippedHandler
        {
            void handleEquip(EntityPlayerSP player, ItemStack stack);
            boolean hideName();
        }

        @Override
        public boolean equals(Object o)
        {
            if(o instanceof SwingProofHandler)
            {
                SwingProofHandler swing = (SwingProofHandler)o;
                return swing.clz == clz && swing.hnd.getClass() == hnd.getClass();
            }
            return false;
        }
    }

    //TODO this only takes note of the main hand, not the off hand as well... WHAT DO
    public static void handlePreRender(Minecraft mc)
    {
        if(mc.thePlayer != null)
        {
            ItemStack currentInv = mc.thePlayer.getHeldItemMainhand();
            if(currentInv != null)
            {
                if(isItemSwingProof(currentInv.getItem()))
                {
                    mc.playerController.resetBlockRemoving();
                    if(prevCurItem == mc.thePlayer.inventory.currentItem)
                    {
                        mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0F;
                        mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand = 1.0F;
                        mc.entityRenderer.itemRenderer.itemStackMainHand = mc.thePlayer.getHeldItemMainhand();
                        if(!currentItemIsSwingProof)
                        {
                            handleSwingProofItemEquip(mc.thePlayer, currentInv);
                        }

                        if(mc.ingameGUI.remainingHighlightTicks == 0)
                        {
                            hasShownItemName = true;
                        }
                        if(hasShownItemName)
                        {
                            mc.ingameGUI.remainingHighlightTicks = 0;
                        }
                    }
                    mc.thePlayer.isSwingInProgress = false;
                    mc.thePlayer.swingProgressInt = 0;
                    mc.thePlayer.swingProgress = 0;
                }
            }
            currentItemIsSwingProof = currentInv != null && isItemSwingProof(currentInv.getItem());
            if(prevCurItem != mc.thePlayer.inventory.currentItem)
            {
                if(mc.thePlayer.inventory.currentItem >= 0 && mc.thePlayer.inventory.currentItem <= 9 && mc.entityRenderer.itemRenderer.equippedProgressMainHand >= 1.0F) //TODO off hand rendering?
                {
                    prevCurItem = mc.thePlayer.inventory.currentItem;
                }
                currentItemIsSwingProof = false;
                hasShownItemName = false;
            }
        }
    }

    public static void handlePlayerTick(Minecraft mc, EntityPlayer player)
    {
        ItemStack is = player.getHeldItemMainhand();
        if(is != null && !(player == mc.getRenderViewEntity() && mc.gameSettings.thirdPersonView == 0) && ItemRenderingHelper.isItemBowAnimationLocked(is.getItem()))
        {
            if(player.getItemInUseCount() <= 0)
            {
                player.resetActiveHand();
                player.setActiveHand(EnumHand.MAIN_HAND);
            }
        }
        if(player == mc.getRenderViewEntity() && mc.gameSettings.thirdPersonView == 0 && lastThirdPersonView != 0)
        {
            player.resetActiveHand();
        }
        if(player == mc.getRenderViewEntity())
        {
            lastThirdPersonView = mc.gameSettings.thirdPersonView;
        }
    }

    //Items registered here can never be allowed to use MC's default "use timer". Their use timer (getMaxItemUseDuration) must be set to Integer.MAX_VALUE.
    public static void registerBowAnimationLockedItem(Class<? extends Item>clz)
    {
        bowAnimationLockedItems.add(clz);
    }

    public static boolean isItemBowAnimationLocked(Item item)
    {
        for(Class<? extends Item> clz : bowAnimationLockedItems)
        {
            if(clz.isInstance(item))
            {
                return true;
            }
        }
        return false;
    }

    public static void registerSwingProofItem(SwingProofHandler handler)
    {
        for(SwingProofHandler handler1 : swingProofItems)
        {
            if(handler1.clz.equals(handler.clz))
            {
                return;
            }
        }
        swingProofItems.add(handler);
    }

    public static boolean isItemSwingProof(Item item)
    {
        for(SwingProofHandler handler1 : swingProofItems)
        {
            if(handler1.clz.isInstance(item))
            {
                return true;
            }
        }
        return false;
    }

    public static void handleSwingProofItemEquip(EntityPlayerSP player, ItemStack stack)
    {
        for(SwingProofHandler handler1 : swingProofItems)
        {
            if(handler1.clz.isInstance(stack.getItem()))
            {
                if(handler1.hnd != null)
                {
                    handler1.hnd.handleEquip(player, stack);
                }
                break;
            }
        }
    }
}
