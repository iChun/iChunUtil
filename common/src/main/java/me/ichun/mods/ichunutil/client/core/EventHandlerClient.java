package me.ichun.mods.ichunutil.client.core;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.loader.event.client.LivingRenderPreEvent;
import me.ichun.mods.ichunutil.loader.event.listener.EventListener;
import me.ichun.mods.ichunutil.loader.event.listener.EventListenerBi;
import me.ichun.mods.ichunutil.loader.event.listener.EventListenerBoolean;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public abstract class EventHandlerClient
{
    public abstract void registerKeyMapping(KeyMapping key, Object eventBus, String...conflictContext);

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

    private EventListener<LevelAccessor> clientLevelLoadListener;
    protected abstract void registerClientLevelLoadListener(EventListener<LevelAccessor> eventListener);
    public final void registerClientLevelLoadListener(Consumer<LevelAccessor> consumer)
    {
        if(clientLevelLoadListener == null)
        {
            clientLevelLoadListener = new EventListener<>(this::registerClientLevelLoadListener);
        }
        clientLevelLoadListener.register(consumer);
    }

    private EventListenerBi<Level, Entity> clientEntityJoinLevelListener;
    protected abstract void registerClientEntityJoinLevelListener(EventListenerBi<Level, Entity> eventListener);
    public final void registerClientEntityJoinLevelListener(BiConsumer<Level, Entity> consumer)
    {
        if(clientEntityJoinLevelListener == null)
        {
            clientEntityJoinLevelListener = new EventListenerBi<>(this::registerClientEntityJoinLevelListener);
        }
        clientEntityJoinLevelListener.register(consumer);
    }

    //Custom Events
    private EventListenerBoolean<LivingRenderPreEvent> livingRenderPreListener;
    private void createLivingRenderPreListener() { if(livingRenderPreListener == null) livingRenderPreListener = new EventListenerBoolean<>(null); }
    public final void registerLivingRenderPreListener(Consumer<LivingRenderPreEvent> consumer)
    {
        createLivingRenderPreListener();
        livingRenderPreListener.register(consumer);
    }
    public final void registerCancelableLivingRenderPreListener(Function<LivingRenderPreEvent, Boolean> function)
    {
        createLivingRenderPreListener();
        livingRenderPreListener.register(function);
    }
    public <E extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> boolean fireLivingRenderPreEvent(E livingEntity, LivingEntityRenderer<E, S ,M> renderer, S renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick)
    {
        return livingRenderPreListener != null && livingRenderPreListener.trigger(new LivingRenderPreEvent(livingEntity, renderer, renderState, poseStack, bufferSource, packedLight, partialTick));
    }

    //Methods to fire events that don't already have a modloader equivalent

    /**
     * Fires an event for when the client receives a system message
     * @param message Message received
     * @param isOverlay is message for overlay
     * @return Return true to cancel the event - Fabric defaults false as Fabric has own handling
     */
    public boolean fireClientHandleSystemMessage(Component message, boolean isOverlay) { return false; }

    public void fireClientLevelLoad(ClientLevel level){}

    public abstract void fireOverlayChange(@Nullable Overlay currentOverlay, @Nullable Overlay newOverlay);

    /**
     * @return Return true to cancel the event
     */
    public boolean fireMouseScroll(double scrollDeltaX, double scrollDeltaY)
    {
        return false;
    }

    //Methods below are just Minecraft calls to avoid references to client classes

    public String getPlayerName()
    {
        return Minecraft.getInstance().getUser().getName();
    }

    @Nullable
    public LocalPlayer getPlayer()
    {
        return Minecraft.getInstance().player;
    }

    @Nullable
    public ClientLevel getWorld()
    {
        return Minecraft.getInstance().level;
    }

    public String getLocalisedString(String s, Object...params)
    {
        return I18n.get(s, params);
    }
}
