package me.ichun.mods.ichunutil.client.item;

import me.ichun.mods.ichunutil.common.item.DualHandedItem;
import me.ichun.mods.ichunutil.loader.LoaderHandler;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class ItemEffectHandler
{
    private static boolean hasInit = false;

    public static synchronized void init()
    {
        if(!hasInit)
        {
            hasInit = true;
            LoaderHandler.d().registerClientTickStartListener(ItemEffectHandler::onClientTickStart);
            LoaderHandler.d().registerPlayerTickEndListener(ItemEffectHandler::onPlayerTickEnd);
            //TODO this
            //            MinecraftForge.EVENT_BUS.addListener(ItemEffectHandler::onRenderSpecificHand);
        }
    }

    public static CameraType lastPoV;

    public static int dualHandedAnimationRight;
    public static int dualHandedAnimationLeft;
    public static int prevDualHandedAnimationRight;
    public static int prevDualHandedAnimationLeft;

    public static int dualHandedAnimationTime = 5;

    public static void onClientTickStart(Minecraft mc)
    {
        if(mc.player != null)
        {
            //disable using the hand so that we can actually use this in third person
            ItemStack is = mc.player.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStack is1 = mc.player.getItemInHand(InteractionHand.OFF_HAND);
            boolean bowLike = (is.getItem() instanceof DualHandedItem && ((DualHandedItem)is.getItem()).isHeldLikeBow(is, mc.player) || is1.getItem() instanceof DualHandedItem && ((DualHandedItem)is1.getItem()).isHeldLikeBow(is1, mc.player)) && mc.options.getCameraType() != CameraType.FIRST_PERSON;
            if(bowLike)
            {
                mc.player.stopUsingItem();
            }
        }

    }

    public static void onPlayerTickEnd(Player player)
    {
        if(player.getLevel().isClientSide)
        {
            Minecraft mc = Minecraft.getInstance();

            boolean isRenderViewEntity = mc.getCameraEntity() == player;

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
            ItemStack is = player.getItemInHand(InteractionHand.MAIN_HAND);
            if(!is.isEmpty() && DualHandedItem.isItemDualHanded(is) && DualHandedItem.canItemBeUsed(player, is))
            {
                if(isRenderViewEntity)
                {
                    if(player.getMainArm() == HumanoidArm.RIGHT)
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
                if(is.getItem() instanceof DualHandedItem && ((DualHandedItem)is.getItem()).isHeldLikeBow(is, player) && !(player == mc.getCameraEntity() && mc.options.getCameraType() == CameraType.FIRST_PERSON))
                {
                    if(player.getUseItemRemainingTicks() <= 0)
                    {
                        player.stopUsingItem();
                        player.startUsingItem(InteractionHand.MAIN_HAND);
                    }
                }
                else
                {
                    player.stopUsingItem();
                }
            }
            is = player.getItemInHand(InteractionHand.OFF_HAND);
            if(!is.isEmpty() && DualHandedItem.isItemDualHanded(is) && DualHandedItem.canItemBeUsed(player, is))
            {
                if(isRenderViewEntity)
                {
                    if(player.getMainArm() == HumanoidArm.RIGHT)
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
                if(is.getItem() instanceof DualHandedItem && ((DualHandedItem)is.getItem()).isHeldLikeBow(is, player) && !(player == mc.getCameraEntity() && mc.options.getCameraType() == CameraType.FIRST_PERSON))
                {
                    if(player.getUseItemRemainingTicks() <= 0)
                    {
                        player.stopUsingItem();
                        player.startUsingItem(InteractionHand.OFF_HAND);
                    }
                }
                else
                {
                    player.stopUsingItem();
                }
            }

            if(player == mc.getCameraEntity() && mc.options.getCameraType() == CameraType.FIRST_PERSON && !(!is.isEmpty() && is.getItem() instanceof DualHandedItem && ((DualHandedItem)is.getItem()).isHeldLikeBow(is, player)) && lastPoV != CameraType.FIRST_PERSON)
            {
                player.stopUsingItem();
            }

            if(player == mc.getCameraEntity())
            {
                lastPoV = mc.options.getCameraType();
            }
        }
    }

    //TODO this
    //    public static void onRenderSpecificHand(RenderHandEvent event)
    //    {
    //        if(event.getHand() == InteractionHand.MAIN_HAND && event.getItemStack().isEmpty())
    //        {
    //            ItemStack is = Minecraft.getInstance().player.getItemInHand(InteractionHand.OFF_HAND);
    //            if(!is.isEmpty() && DualHandedItem.isItemDualHanded(is))
    //            {
    //                event.setCanceled(true);
    //            }
    //        }
    //    }
}
