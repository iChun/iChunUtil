package me.ichun.mods.ichunutil.api.fabric.event.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public final class FabricClientEvents
{
    private FabricClientEvents(){}//no init!
    public static final Event<ClientLevelLoad> CLIENT_LEVEL_LOAD = EventFactory.createArrayBacked(ClientLevelLoad.class, callbacks -> level -> {
        for(ClientLevelLoad callback : callbacks)
        {
            callback.onClientLevelLoad(level);
        }
    });

    public static final Event<LivingRenderPre> LIVING_RENDER_PRE = EventFactory.createArrayBacked(LivingRenderPre.class, callbacks -> (living, renderer, partialTick) -> {
        for(LivingRenderPre callback : callbacks)
        {
            callback.onLivingRenderPre(living, renderer, partialTick);
        }
    });

    @FunctionalInterface
    public interface ClientLevelLoad
    {
        void onClientLevelLoad(ClientLevel level);
    }

    @FunctionalInterface
    public interface LivingRenderPre
    {
        void onLivingRenderPre(LivingEntity living, LivingEntityRenderer renderer, float partialTick);
    }
}
