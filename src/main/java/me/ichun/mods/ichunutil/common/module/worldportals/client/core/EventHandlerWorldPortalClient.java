package me.ichun.mods.ichunutil.common.module.worldportals.client.core;

import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.module.worldportals.client.render.WorldPortalRenderer;
import me.ichun.mods.ichunutil.common.module.worldportals.client.render.world.RenderGlobalProxy;
import me.ichun.mods.ichunutil.common.module.worldportals.common.WorldPortals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashSet;

public class EventHandlerWorldPortalClient
{
    public float prevCameraRoll;
    public float cameraRoll;

    public WorldClient instance;
    public RenderGlobalProxy renderGlobalProxy;

    //temp
    public HashSet<AxisAlignedBB> aabbToRender = new HashSet<>();

    @SubscribeEvent
    public void onCameraSetupEvent(EntityViewRenderEvent.CameraSetup event)
    {
        if(cameraRoll != 0F && WorldPortalRenderer.renderLevel <= 0)
        {
            event.setRoll(EntityHelper.interpolateValues(prevCameraRoll, cameraRoll, (float)event.getRenderPartialTicks()));
        }
    }

    @SubscribeEvent
    public void onRenderBlockOverlay(RenderBlockOverlayEvent event)
    {
        if(WorldPortals.eventHandler.isInPortal(event.getPlayer()))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPushPlayerSPOutOfBlock(PlayerSPPushOutOfBlocksEvent event)
    {
        if(WorldPortals.eventHandler.isInPortal(event.getEntityPlayer()))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        if(event.phase == TickEvent.Phase.START)
        {
            if(instance != mc.world)
            {
                instance = mc.world;

                if(renderGlobalProxy == null)
                {
                    renderGlobalProxy = new RenderGlobalProxy(mc);
                    renderGlobalProxy.updateDestroyBlockIcons();
                }
                renderGlobalProxy.setWorldAndLoadRenderers(instance);
            }
            WorldPortalRenderer.renderLevel = 0;
            WorldPortalRenderer.renderCount = 0;
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            prevCameraRoll = cameraRoll;
            cameraRoll *= 0.85F;
            if(Math.abs(cameraRoll) < 0.05F)
            {
                cameraRoll = 0F;
            }
        }
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        Minecraft.getMinecraft().addScheduledTask(this::disconnectFromServer);
    }

    public void disconnectFromServer()
    {
        WorldPortals.eventHandler.monitoredEntities.get(Side.CLIENT).clear();
        WorldPortalRenderer.renderLevel = 0;
        WorldPortalRenderer.rollFactor.clear();
    }

//    @SubscribeEvent
//    public void onRenderWorldLastEvent(RenderWorldLastEvent event)
//    {
//        if(aabbToRender.isEmpty())
//        {
//            return;
//        }
//        EntityPlayer lastPlayer = Minecraft.getMinecraft().player;
//
//        GlStateManager.disableAlpha();
//        GlStateManager.enableBlend();
//        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
//        float r, g, b, a;
//        if(true)
//        {
//            r = 0.8F;
//            b = 0F;
//            g = 0F;
//            a = 0.4F;
//        }
//        else
//        {
//            r = 120F / 255F;
//            g = 188F / 255F;
//            b = 215F / 255F;
//            a = 0.4F;
//        }
//        GlStateManager.color(r, g, b, a);
//        GlStateManager.glLineWidth(4.0F);
//        GlStateManager.disableTexture2D();
//        GlStateManager.depthMask(false);
//        float f1 = 0.002F;
//
//        double d0 = lastPlayer.lastTickPosX + (lastPlayer.posX - lastPlayer.lastTickPosX) * (double)event.getPartialTicks();
//        double d1 = lastPlayer.lastTickPosY + (lastPlayer.posY - lastPlayer.lastTickPosY) * (double)event.getPartialTicks();
//        double d2 = lastPlayer.lastTickPosZ + (lastPlayer.posZ - lastPlayer.lastTickPosZ) * (double)event.getPartialTicks();
//        for(AxisAlignedBB aabb : aabbToRender)
//        {
//            RenderGlobal.drawSelectionBoundingBox(aabb.grow(0.002D).offset(-d0, -d1, -d2), r, g, b, a);
//        }
//
//        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//        GlStateManager.depthMask(true);
//        GlStateManager.enableTexture2D();
//        GlStateManager.disableBlend();
//        GlStateManager.enableAlpha();
//
//        if(Keyboard.isKeyDown(Keyboard.KEY_TAB))
//        {
//            aabbToRender.clear();
//        }
//    }
}
