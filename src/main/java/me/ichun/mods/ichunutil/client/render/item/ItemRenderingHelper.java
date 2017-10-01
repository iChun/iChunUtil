package me.ichun.mods.ichunutil.client.render.item;

import me.ichun.mods.ichunutil.common.item.ItemHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.RenderSpecificHandEvent;

import javax.annotation.Nullable;
import java.util.HashSet;

public class ItemRenderingHelper
{
    public static int lastThirdPersonView;

    public static HashSet<SwingProofHandler> swingProofItems = new HashSet<>();

    private static int prevCurItem;
    private static boolean currentItemIsSwingProof;
    private static boolean hasShownItemName;

    public static int dualHandedAnimationRight;
    public static int dualHandedAnimationLeft;
    public static int prevDualHandedAnimationRight;
    public static int prevDualHandedAnimationLeft;

    public static int dualHandedAnimationTime = 5;

    public static class SwingProofHandler
    {
        public final Class<? extends Item> clz;
        public final
        @Nullable
        IItemEquippedHandler hnd;

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

    public static void handlePreRender(Minecraft mc)
    {
        if(mc.player != null)
        {
            ItemStack currentInv = mc.player.getHeldItemMainhand();
            if(!currentInv.isEmpty())
            {
                if(isItemSwingProof(currentInv.getItem()))
                {
                    mc.playerController.resetBlockRemoving();
                    if(prevCurItem == mc.player.inventory.currentItem)
                    {
                        if(!currentItemIsSwingProof)
                        {
                            handleSwingProofItemEquip(mc.player, currentInv);
                        }

                        if(currentInv.getItem().shouldCauseReequipAnimation(currentInv, mc.getItemRenderer().itemStackMainHand, false))
                        {
                            mc.getItemRenderer().itemStackMainHand = currentInv;
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
                    if(mc.player.ticksSinceLastSwing < 2)
                    {
                        mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1F;
                    }
                    mc.player.ticksSinceLastSwing = 10000;
                    mc.player.isSwingInProgress = false;
                    mc.player.swingProgressInt = 0;
                    mc.player.swingProgress = 0;
                }
            }
            currentItemIsSwingProof = !currentInv.isEmpty() && isItemSwingProof(currentInv.getItem());
            if(prevCurItem != mc.player.inventory.currentItem)
            {
                if(mc.player.inventory.currentItem >= 0 && mc.player.inventory.currentItem <= 9 && mc.entityRenderer.itemRenderer.equippedProgressMainHand >= 1.0F) //TODO off hand rendering?
                {
                    prevCurItem = mc.player.inventory.currentItem;
                }
                currentItemIsSwingProof = false;
                hasShownItemName = false;
            }
        }
    }

    public static void onRenderSpecificHand(RenderSpecificHandEvent event)
    {
        if(event.getHand() == EnumHand.MAIN_HAND && event.getItemStack().isEmpty())
        {
            ItemStack is = Minecraft.getMinecraft().player.getHeldItem(EnumHand.OFF_HAND);
            if(!is.isEmpty() && ItemHandler.isItemDualHanded(is))
            {
                event.setCanceled(true);
            }
        }
    }

    public static void handlePlayerTick(Minecraft mc, EntityPlayer player)
    {
        boolean isRenderViewEntity = mc.getRenderViewEntity() == player;
        if(isRenderViewEntity)
        {
            prevDualHandedAnimationLeft = dualHandedAnimationLeft;
            prevDualHandedAnimationRight = dualHandedAnimationRight;

            dualHandedAnimationRight++;
            dualHandedAnimationLeft++;
            dualHandedAnimationTime = 4;
            if(dualHandedAnimationRight > dualHandedAnimationTime)
            {
                dualHandedAnimationRight = dualHandedAnimationTime;
            }
            if(dualHandedAnimationLeft > dualHandedAnimationTime)
            {
                dualHandedAnimationLeft = dualHandedAnimationTime;
            }
        }
        ItemStack is = player.getHeldItem(EnumHand.MAIN_HAND);
        if(!is.isEmpty() && ItemHandler.isItemDualHanded(is) && ItemHandler.canItemBeUsed(player, is))
        {
            if(isRenderViewEntity)
            {
                if(player.getPrimaryHand() == EnumHandSide.RIGHT)
                {
                    dualHandedAnimationRight -= 2;
                    if(dualHandedAnimationRight < 0)
                    {
                        dualHandedAnimationRight = 0;
                    }
                }
                else
                {
                    dualHandedAnimationLeft -= 2;
                    if(dualHandedAnimationLeft < 0)
                    {
                        dualHandedAnimationLeft = 0;
                    }
                }
            }
            if(ItemHandler.getDualHandedItemCallback(is).shouldItemBeHeldLikeBow(is, player) && !(player == mc.getRenderViewEntity() && mc.gameSettings.thirdPersonView == 0))
            {
                if(player.getItemInUseCount() <= 0)
                {
                    player.resetActiveHand();
                    player.setActiveHand(EnumHand.MAIN_HAND);
                }
            }
            else
            {
                player.resetActiveHand();
            }
        }
        is = player.getHeldItem(EnumHand.OFF_HAND);
        if(!is.isEmpty() && ItemHandler.isItemDualHanded(is) && ItemHandler.canItemBeUsed(player, is))
        {
            if(isRenderViewEntity)
            {
                if(player.getPrimaryHand() == EnumHandSide.RIGHT)
                {
                    dualHandedAnimationLeft -= 2;
                    if(dualHandedAnimationLeft < 0)
                    {
                        dualHandedAnimationLeft = 0;
                    }
                }
                else
                {
                    dualHandedAnimationRight -= 2;
                    if(dualHandedAnimationRight < 0)
                    {
                        dualHandedAnimationRight = 0;
                    }
                }
            }
            if(ItemHandler.getDualHandedItemCallback(is).shouldItemBeHeldLikeBow(is, player) && !(player == mc.getRenderViewEntity() && mc.gameSettings.thirdPersonView == 0))
            {
                if(player.getItemInUseCount() <= 0)
                {
                    player.resetActiveHand();
                    player.setActiveHand(EnumHand.OFF_HAND);
                }
            }
            else
            {
                player.resetActiveHand();
            }
        }

        if(player == mc.getRenderViewEntity() && mc.gameSettings.thirdPersonView == 0 && !(!is.isEmpty() && ItemHandler.getDualHandedItemCallback(is).shouldItemBeHeldLikeBow(is, player)) && lastThirdPersonView != 0)
        {
            player.resetActiveHand();
        }

        if(player == mc.getRenderViewEntity())
        {
            lastThirdPersonView = mc.gameSettings.thirdPersonView;
        }
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
