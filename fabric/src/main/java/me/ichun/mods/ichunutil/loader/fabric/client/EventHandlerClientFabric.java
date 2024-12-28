package me.ichun.mods.ichunutil.loader.fabric.client;

import me.ichun.mods.ichunutil.client.core.EventHandlerClient;
import me.ichun.mods.ichunutil.loader.event.listener.EventListener;
import me.ichun.mods.ichunutil.loader.event.listener.EventListenerBi;
import me.ichun.mods.ichunutil.loader.fabric.event.client.FabricClientEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class EventHandlerClientFabric extends EventHandlerClient
{
    @Override
    public void registerKeyMapping(KeyMapping key, Object eventBus, String...conflictContext)
    {
        KeyBindingRegistryImpl.registerKeyBinding(key);
    }

    @Override
    protected void registerAsClientTickStartListener(EventListener<Minecraft> eventListener)
    {
        ClientTickEvents.START_CLIENT_TICK.register(eventListener::trigger);
    }

    @Override
    protected void registerAsClientTickEndListener(EventListener<Minecraft> eventListener)
    {
        ClientTickEvents.END_CLIENT_TICK.register(eventListener::trigger);
    }

    @Override
    protected void registerAsOnClientConnectListener(EventListener<Minecraft> eventListener)
    {
        ClientLoginConnectionEvents.INIT.register((handler, client) -> eventListener.trigger(client));
    }

    @Override
    protected void registerAsOnClientDisconnectListener(EventListener<Minecraft> eventListener)
    {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> eventListener.trigger(client));
    }

    @Override
    protected void registerClientLevelLoadListener(EventListener<LevelAccessor> eventListener)
    {
        FabricClientEvents.CLIENT_LEVEL_LOAD.register(eventListener::trigger);
    }

    @Override
    protected void registerClientEntityJoinLevelListener(EventListenerBi<Level, Entity> eventListener)
    {
        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> eventListener.trigger(world, entity));
    }

    @Override
    public void fireClientLevelLoad(ClientLevel level)
    {
        FabricClientEvents.CLIENT_LEVEL_LOAD.invoker().onClientLevelLoad(level);
    }

    @Override
    public void fireOverlayChange(@Nullable Overlay currentOverlay, @Nullable Overlay newOverlay)
    {
        FabricClientEvents.OVERLAY_CHANGE.invoker().onOverlayChange(currentOverlay, newOverlay);
    }

    @Override
    public boolean fireMouseScroll(double scrollDeltaX, double scrollDeltaY)
    {
        return FabricClientEvents.MOUSE_SCROLL.invoker().onMouseScroll(scrollDeltaX, scrollDeltaY);
    }
}
