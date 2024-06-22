package me.ichun.mods.ichunutil.loader.neoforge.client;

import me.ichun.mods.ichunutil.client.core.EventHandlerClient;
import me.ichun.mods.ichunutil.client.key.KeyBind;
import me.ichun.mods.ichunutil.loader.event.EventListener;
import me.ichun.mods.ichunutil.loader.neoforge.event.client.ClientSystemChatEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.ArrayUtils;

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

        Minecraft.getInstance().options.keyMappings = ArrayUtils.add(Minecraft.getInstance().options.keyMappings, key); //Originally from Forge: ClientRegistry.registerKeyBinding(this.keyBinding);
    }

    @Override
    protected void registerAsClientTickStartListener(EventListener<Minecraft> eventListener)
    {
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ClientTickEvent.Pre.class, event -> eventListener.trigger(Minecraft.getInstance()));
    }

    @Override
    protected void registerAsClientTickEndListener(EventListener<Minecraft> eventListener)
    {
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ClientTickEvent.Pre.class, event -> eventListener.trigger(Minecraft.getInstance()));
    }

    @Override
    public boolean fireClientHandleSystemMessage(Component message, boolean isOverlay)
    {
        return NeoForge.EVENT_BUS.post(new ClientSystemChatEvent(message, isOverlay)).isCanceled();
    }
}
