package me.ichun.mods.ichunutil.client.core;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Consumer;

public abstract class EventHandlerClient
{
    public abstract void registerKeyMapping(KeyMapping key, String...conflictContext);

    /**
     * Convenience methods to listen to client tick - regularly used
     */
    protected final ArrayList<Consumer<Minecraft>> clientTickStartListeners = new ArrayList<>();
    protected final ArrayList<Consumer<Minecraft>> clientTickEndListeners = new ArrayList<>();
    public final void registerClientTickStartListener(Consumer<Minecraft> consumer)
    {
        if(clientTickStartListeners.isEmpty())
        {
            registerAsClientTickStartListener();
        }
        clientTickStartListeners.add(consumer);
    }

    public final void registerClientTickEndListener(Consumer<Minecraft> consumer)
    {
        if(clientTickEndListeners.isEmpty())
        {
            registerAsClientTickEndListener();
        }
        clientTickEndListeners.add(consumer);
    }

    protected abstract void registerAsClientTickStartListener();

    protected abstract void registerAsClientTickEndListener();

    protected void onClientTickEventStart()
    {
        for(Consumer<Minecraft> listener : clientTickStartListeners)
        {
            listener.accept(Minecraft.getInstance());
        }
    }

    protected void onClientTickEventEnd()
    {
        for(Consumer<Minecraft> listener : clientTickEndListeners)
        {
            listener.accept(Minecraft.getInstance());
        }
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
