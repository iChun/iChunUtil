package me.ichun.mods.ichunutil.loader.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Consumer;

public interface LoaderDelegateClient
{
    void registerKeyMapping(KeyMapping key, String...conflictContext);

    void registerClientTickStartListener(Consumer<Minecraft> consumer);

    void registerClientTickEndListener(Consumer<Minecraft> consumer);

    default void fireLivingRenderPreEvent(LivingEntity entity, LivingEntityRenderer livingEntityRenderer, float partialTicks){}

    default void fireClientLevelLoad(ClientLevel level){};
}
