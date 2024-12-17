package me.ichun.mods.ichunutil.loader.neoforge.client;

import me.ichun.mods.ichunutil.client.core.EventHandlerClient;
import me.ichun.mods.ichunutil.client.key.KeyBind;
import me.ichun.mods.ichunutil.loader.event.EventListener;
import me.ichun.mods.ichunutil.loader.neoforge.event.client.ClientSystemChatEvent;
import me.ichun.mods.ichunutil.loader.neoforge.event.client.OverlayChangeEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class EventHandlerClientNeoForge extends EventHandlerClient
{
    @Override
    @SuppressWarnings("all")
    public void registerKeyMapping(KeyMapping key, String... conflictContext)
    {
        if(!KeyBind.areKeyConflictContextsRegistered())
        {
            KeyBind.registerKeyConflictContext("in_game_modifier_sensitive", new IKeyConflictContext() {
                @Override
                public boolean isActive()
                {
                    return !KeyConflictContext.GUI.isActive();
                }

                @Override
                public boolean conflicts(IKeyConflictContext other)
                {
                    return this == other;
                }
            });
        }

        if(conflictContext.length > 0)
        {
            Object keyConflictContext = KeyBind.getKeyConflictContext(conflictContext[0]);
            if(keyConflictContext != null)
            {
                key.setKeyConflictContext((IKeyConflictContext)keyConflictContext);
            }
        }

        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, RegisterKeyMappingsEvent.class, event -> event.register(key));
    }

    @Override
    protected void registerAsClientTickStartListener(EventListener<Minecraft> eventListener)
    {
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TickEvent.ClientTickEvent.class, event -> {
            if(event.phase == TickEvent.Phase.START) eventListener.trigger(Minecraft.getInstance());
        });
    }

    @Override
    protected void registerAsClientTickEndListener(EventListener<Minecraft> eventListener)
    {
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, TickEvent.ClientTickEvent.class, event -> {
            if(event.phase == TickEvent.Phase.END) eventListener.trigger(Minecraft.getInstance());
        });
    }

    @Override
    protected void registerAsOnClientConnectListener(EventListener<Minecraft> eventListener)
    {
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ClientPlayerNetworkEvent.LoggingIn.class, event -> eventListener.trigger(Minecraft.getInstance()));
    }

    @Override
    protected void registerAsOnClientDisconnectListener(EventListener<Minecraft> eventListener)
    {
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ClientPlayerNetworkEvent.LoggingOut.class, event -> eventListener.trigger(Minecraft.getInstance()));
    }

    @Override
    public boolean fireClientHandleSystemMessage(Component message, boolean isOverlay)
    {
        return NeoForge.EVENT_BUS.post(new ClientSystemChatEvent(message, isOverlay)).isCanceled();
    }

    @Override
    public void fireOverlayChange(@Nullable Overlay currentOverlay, @Nullable Overlay newOverlay)
    {
        NeoForge.EVENT_BUS.post(new OverlayChangeEvent(currentOverlay, newOverlay));
    }
}
