package me.ichun.mods.ichunutil.loader.forge;

import me.ichun.mods.ichunutil.common.core.EventHandlerServer;
import me.ichun.mods.ichunutil.common.entity.EntityPersistentDataHandler;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

public class EventHandlerServerForge extends EventHandlerServer
{
    @Override
    public EntityPersistentDataHandler getEntityPersistedDataHandler()
    {
        if(iChunUtil.entityPersistentDataHandler == null)
        {
            iChunUtil.entityPersistentDataHandler = new EntityPersistentDataHandlerForge();
        }
        return iChunUtil.entityPersistentDataHandler;
    }

    @Override
    public void registerAsPlayerTickEndListener()
    {
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerTickEnd);
    }

    private void onPlayerTickEnd(TickEvent.PlayerTickEvent.Post event)
    {
        onPlayerTickEventEnd(event.player);
    }
}
