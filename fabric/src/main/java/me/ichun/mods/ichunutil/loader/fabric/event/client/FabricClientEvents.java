package me.ichun.mods.ichunutil.loader.fabric.event.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

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

    public static final Event<OverlayChange> OVERLAY_CHANGE = EventFactory.createArrayBacked(OverlayChange.class, callbacks -> (currentOverlay, newOverlay) -> {
        for(OverlayChange callback : callbacks)
        {
            callback.onOverlayChange(currentOverlay, newOverlay);
        }
    });

    public static final Event<MouseScroll> MOUSE_SCROLL = EventFactory.createArrayBacked(MouseScroll.class, listeners -> (scrollDeltaX, scrollDeltaY) -> {
        for (MouseScroll listener : listeners) {
            if (listener.onMouseScroll(scrollDeltaX, scrollDeltaY)) {
                return true;
            }
        }

        return false;
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

    @FunctionalInterface
    public interface OverlayChange
    {
        void onOverlayChange(@Nullable Overlay currentOverlay, @Nullable Overlay newOverlay);
    }

    @FunctionalInterface
    public interface MouseScroll
    {
        boolean onMouseScroll(double scrollDeltaX, double scrollDeltaY);
    }
}
