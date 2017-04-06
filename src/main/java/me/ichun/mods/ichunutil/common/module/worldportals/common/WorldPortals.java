package me.ichun.mods.ichunutil.common.module.worldportals.common;

import me.ichun.mods.ichunutil.api.worldportals.WorldPortalsApi;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import me.ichun.mods.ichunutil.common.core.network.PacketChannel;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.worldportals.client.core.EventHandlerWorldPortalClient;
import me.ichun.mods.ichunutil.common.module.worldportals.common.core.ApiImpl;
import me.ichun.mods.ichunutil.common.module.worldportals.common.core.EventHandlerWorldPortal;
import me.ichun.mods.ichunutil.common.module.worldportals.common.packet.PacketEntityLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldPortals
{
    private static boolean init = false;

    public static EventHandlerWorldPortalClient eventHandlerClient;
    public static EventHandlerWorldPortal eventHandler;
    public static PacketChannel channel;

    public static void init()
    {
        if(init)
        {
            return;
        }
        init = true;

        iChunUtil.config.reveal("maxRecursion", "stencilValue", "renderDistanceChunks", "maxRendersPerTick");

        eventHandler = new EventHandlerWorldPortal();
        MinecraftForge.EVENT_BUS.register(eventHandler);

        WorldPortalsApi.setApiImpl(new ApiImpl());

        channel = new PacketChannel("iChunUtil_WorldPortals", PacketEntityLocation.class);

        if(FMLCommonHandler.instance().getSide().isClient())
        {
            initClient();
        }
    }

    @SideOnly(Side.CLIENT)
    private static void initClient()
    {
        eventHandlerClient = new EventHandlerWorldPortalClient();
        MinecraftForge.EVENT_BUS.register(eventHandlerClient);

        if(!RendererHelper.canUseStencils())
        {
            iChunUtil.LOGGER.error("[WorldPortals] Stencils aren't enabled. We won't be able to render a world portal!");
        }
    }

    public static void onServerStopping()
    {
        if(init)
        {
            eventHandler.monitoredEntities.get(Side.SERVER).clear();
        }
    }
}
