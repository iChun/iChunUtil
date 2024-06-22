package me.ichun.mods.ichunutil.loader.fabric.client;

import me.ichun.mods.ichunutil.client.core.EventHandlerClient;
import me.ichun.mods.ichunutil.loader.fabric.event.client.FabricClientEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

public class EventHandlerClientFabric extends EventHandlerClient
{
    @Override
    public void registerKeyMapping(KeyMapping key, String...conflictContext)
    {
        KeyBindingRegistryImpl.registerKeyBinding(key);
    }

    @Override
    protected void registerAsClientTickStartListener()
    {
        ClientTickEvents.START_CLIENT_TICK.register(client -> this.onClientTickEventStart());
    }

    @Override
    protected void registerAsClientTickEndListener()
    {
        ClientTickEvents.END_CLIENT_TICK.register(client -> this.onClientTickEventEnd());
    }

    @Override
    public void fireLivingRenderPreEvent(LivingEntity living, LivingEntityRenderer renderer, float partialTick)
    {
        FabricClientEvents.LIVING_RENDER_PRE.invoker().onLivingRenderPre(living, renderer, partialTick);
    }

    @Override
    public void fireClientLevelLoad(ClientLevel level)
    {
        FabricClientEvents.CLIENT_LEVEL_LOAD.invoker().onClientLevelLoad(level);
    }
}
