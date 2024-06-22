package me.ichun.mods.ichunutil.client.core;

import me.ichun.mods.ichunutil.loader.event.EventListener;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class EventHandlerClient
{
    public abstract void registerKeyMapping(KeyMapping key, String...conflictContext);

    /**
     * Convenience methods to listen to specific regularly used events
     */
    private EventListener<Minecraft> clientTickStartListener;
    protected abstract void registerAsClientTickStartListener(EventListener<Minecraft> eventListener);
    public final void registerClientTickStartListener(Consumer<Minecraft> consumer)
    {
        if(clientTickStartListener == null)
        {
            clientTickStartListener = new EventListener<>(this::registerAsClientTickStartListener);
        }
        clientTickStartListener.register(consumer);
    }

    private EventListener<Minecraft> clientTickEndListener;
    protected abstract void registerAsClientTickEndListener(EventListener<Minecraft> eventListener);
    public final void registerClientTickEndListener(Consumer<Minecraft> consumer)
    {
        if(clientTickEndListener == null)
        {
            clientTickEndListener = new EventListener<>(this::registerAsClientTickEndListener);
        }
        clientTickEndListener.register(consumer);
    }

    private EventListener<Minecraft> clientConnectListener;
    protected abstract void registerAsOnClientConnectListener(EventListener<Minecraft> eventListener);
    public final void registerOnClientConnectListener(Consumer<Minecraft> consumer)
    {
        if(clientConnectListener == null)
        {
            clientConnectListener = new EventListener<>(this::registerAsOnClientConnectListener);
        }
        clientConnectListener.register(consumer);
    }

    private EventListener<Minecraft> clientDisconnectListener;
    protected abstract void registerAsOnClientDisconnectListener(EventListener<Minecraft> eventListener);
    public final void registerOnClientDisconnectListener(Consumer<Minecraft> consumer)
    {
        if(clientDisconnectListener == null)
        {
            clientDisconnectListener = new EventListener<>(this::registerAsOnClientDisconnectListener);
        }
        clientDisconnectListener.register(consumer);
    }

    /**
     * Fires an event for when the client receives a system message
     * @param message Message received
     * @param isOverlay is message for overlay
     * @return Return true to cancel the event - Fabric defaults false as Fabric has own handling
     */
    public boolean fireClientHandleSystemMessage(Component message, boolean isOverlay) { return false; }

    public void fireLivingRenderPreEvent(LivingEntity entity, LivingEntityRenderer livingEntityRenderer, float partialTicks){}

    public void fireClientLevelLoad(ClientLevel level){}


    //Methods below are just Minecraft calls to avoid references to client classes

    public String getPlayerName()
    {
        return Minecraft.getInstance().getUser().getName();
    }

    @Nullable
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public LocalPlayer getPlayer()
    {
        return Minecraft.getInstance().player;
    }

    @Nullable
    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public ClientLevel getWorld()
    {
        return Minecraft.getInstance().level;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    public String getLocalisedString(String s, Object...params)
    {
        return I18n.get(s, params);
    }
}
