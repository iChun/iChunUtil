package me.ichun.mods.ichunutil.loader.fabric.client;

import me.ichun.mods.ichunutil.api.fabric.event.client.FabricClientEvents;
import me.ichun.mods.ichunutil.loader.client.LoaderDelegateClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

public class LoaderDelegateClientFabric implements LoaderDelegateClient
{
    @Override
    public void registerKeyMapping(KeyMapping key, String...conflictContext)
    {
        KeyBindingRegistryImpl.registerKeyBinding(key);
    }

    @Override
    public void registerClientTickStartListener(Consumer<Minecraft> consumer)
    {
        ClientTickEvents.START_CLIENT_TICK.register(consumer::accept);
    }

    @Override
    public void registerClientTickEndListener(Consumer<Minecraft> consumer)
    {
        ClientTickEvents.END_CLIENT_TICK.register(consumer::accept);
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
