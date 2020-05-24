package me.ichun.mods.ichunutil.client.item;

import me.ichun.mods.ichunutil.common.item.DualHandedItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

@OnlyIn(Dist.CLIENT)
public class ItemEffectHandler
{
    private static boolean hasInit = false;

    public static void init()
    {
        if(!hasInit)
        {
            hasInit = true;
            MinecraftForge.EVENT_BUS.addListener(ItemEffectHandler::onClientTick);
            MinecraftForge.EVENT_BUS.addListener(ItemEffectHandler::onPlayerTick);
            MinecraftForge.EVENT_BUS.addListener(ItemEffectHandler::onRenderSpecificHand);
        }
    }

    public static int lastThirdPersonView;

    public static int dualHandedAnimationRight;
    public static int dualHandedAnimationLeft;
    public static int prevDualHandedAnimationRight;
    public static int prevDualHandedAnimationLeft;

    public static int dualHandedAnimationTime = 5;

    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        if(event.phase == TickEvent.Phase.START && mc.player != null)
        {
            //disable using the hand so that we can actually use this in third person
            ItemStack is = mc.player.getHeldItem(Hand.MAIN_HAND);
            ItemStack is1 = mc.player.getHeldItem(Hand.OFF_HAND);
            boolean bowLike = (is.getItem() instanceof DualHandedItem && ((DualHandedItem)is.getItem()).isHeldLikeBow(is, mc.player) || is1.getItem() instanceof DualHandedItem && ((DualHandedItem)is1.getItem()).isHeldLikeBow(is1, mc.player)) && mc.gameSettings.thirdPersonView != 0;
            if(bowLike)
            {
                mc.player.resetActiveHand();
            }
        }

    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if(event.side.isClient() && event.phase == TickEvent.Phase.END)
        {
            Minecraft mc = Minecraft.getInstance();
            PlayerEntity player = event.player;

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
            ItemStack is = player.getHeldItem(Hand.MAIN_HAND);
            if(!is.isEmpty() && DualHandedItem.isItemDualHanded(is) && DualHandedItem.canItemBeUsed(player, is))
            {
                if(isRenderViewEntity)
                {
                    if(player.getPrimaryHand() == HandSide.RIGHT)
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
                if(is.getItem() instanceof DualHandedItem && ((DualHandedItem)is.getItem()).isHeldLikeBow(is, player) && !(player == mc.getRenderViewEntity() && mc.gameSettings.thirdPersonView == 0))
                {
                    if(player.getItemInUseCount() <= 0)
                    {
                        player.resetActiveHand();
                        player.setActiveHand(Hand.MAIN_HAND);
                    }
                }
                else
                {
                    player.resetActiveHand();
                }
            }
            is = player.getHeldItem(Hand.OFF_HAND);
            if(!is.isEmpty() && DualHandedItem.isItemDualHanded(is) && DualHandedItem.canItemBeUsed(player, is))
            {
                if(isRenderViewEntity)
                {
                    if(player.getPrimaryHand() == HandSide.RIGHT)
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
                if(is.getItem() instanceof DualHandedItem && ((DualHandedItem)is.getItem()).isHeldLikeBow(is, player) && !(player == mc.getRenderViewEntity() && mc.gameSettings.thirdPersonView == 0))
                {
                    if(player.getItemInUseCount() <= 0)
                    {
                        player.resetActiveHand();
                        player.setActiveHand(Hand.OFF_HAND);
                    }
                }
                else
                {
                    player.resetActiveHand();
                }
            }

            if(player == mc.getRenderViewEntity() && mc.gameSettings.thirdPersonView == 0 && !(!is.isEmpty() && is.getItem() instanceof DualHandedItem && ((DualHandedItem)is.getItem()).isHeldLikeBow(is, player)) && lastThirdPersonView != 0)
            {
                player.resetActiveHand();
            }

            if(player == mc.getRenderViewEntity())
            {
                lastThirdPersonView = mc.gameSettings.thirdPersonView;
            }
        }
    }

    public static void onRenderSpecificHand(RenderHandEvent event)
    {
        if(event.getHand() == Hand.MAIN_HAND && event.getItemStack().isEmpty())
        {
            ItemStack is = Minecraft.getInstance().player.getHeldItem(Hand.OFF_HAND);
            if(!is.isEmpty() && DualHandedItem.isItemDualHanded(is))
            {
                event.setCanceled(true);
            }
        }
    }
}
