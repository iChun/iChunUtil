package me.ichun.mods.ichunutil.loader.fabric;

import me.ichun.mods.ichunutil.common.core.EventHandlerServer;
import me.ichun.mods.ichunutil.common.entity.EntityPersistentDataHandler;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.loader.fabric.event.FabricEvents;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class EventHandlerServerFabric extends EventHandlerServer
{
    @Override
    public EntityPersistentDataHandler getEntityPersistedDataHandler()
    {
        if(iChunUtil.entityPersistentDataHandler == null)
        {
            iChunUtil.entityPersistentDataHandler = new EntityPersistentDataHandlerFabric();
        }
        return iChunUtil.entityPersistentDataHandler;
    }

    @Override
    public void registerAsPlayerTickEndListener()
    {
        FabricEvents.PLAYER_TICK_END.register(this::onPlayerTickEventEnd);
    }

    @Override
    public void firePlayerTickEndEvent(Player player)
    {
        FabricEvents.PLAYER_TICK_END.invoker().onPlayerTickEnd(player);
    }

    @Override
    public boolean isFakePlayer(ServerPlayer player)
    {
        return player instanceof FakePlayer || super.isFakePlayer(player);
    }
}
