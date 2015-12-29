package me.ichun.mods.ichunutil.client.core;

import me.ichun.mods.ichunutil.client.core.event.EventHandlerClient;
import me.ichun.mods.ichunutil.common.core.ProxyCommon;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class ProxyClient extends ProxyCommon
{
    @Override
    public void preInit()
    {
        super.preInit();

        iChunUtil.eventHandlerClient = new EventHandlerClient();
        MinecraftForge.EVENT_BUS.register(iChunUtil.eventHandlerClient);
    }

    @Override
    public void nudgeHand(float mag)
    {
        Minecraft.getMinecraft().thePlayer.renderArmPitch += mag;
    }
}
