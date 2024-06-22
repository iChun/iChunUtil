package me.ichun.mods.ichunutil.loader.neoforge;

import me.ichun.mods.ichunutil.common.core.EventHandlerServer;
import me.ichun.mods.ichunutil.common.entity.EntityPersistentDataHandler;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public class EventHandlerServerNeoForge extends EventHandlerServer
{
    @Override
    public EntityPersistentDataHandler getEntityPersistedDataHandler()
    {
        if(iChunUtil.entityPersistentDataHandler == null)
        {
            iChunUtil.entityPersistentDataHandler = new EntityPersistentDataHandlerNeoForge();
        }
        return iChunUtil.entityPersistentDataHandler;
    }

    @Override
    public void registerAsPlayerTickEndListener()
    {
        NeoForge.EVENT_BUS.addListener(this::onPlayerTickEnd);
    }

    private void onPlayerTickEnd(PlayerTickEvent.Post event)
    {
        onPlayerTickEventEnd(event.getEntity());
    }
}
