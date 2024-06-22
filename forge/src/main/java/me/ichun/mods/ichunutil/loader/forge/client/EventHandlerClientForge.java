package me.ichun.mods.ichunutil.loader.forge.client;

import me.ichun.mods.ichunutil.client.core.EventHandlerClient;
import me.ichun.mods.ichunutil.client.key.KeyBind;
import me.ichun.mods.ichunutil.loader.forge.event.client.ClientSystemChatEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.ArrayUtils;

public class EventHandlerClientForge extends EventHandlerClient
{
    @Override
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
    protected void registerAsClientTickStartListener()
    {
        MinecraftForge.EVENT_BUS.addListener(event -> this.onClientTickEventStart());
    }

    @Override
    protected void registerAsClientTickEndListener()
    {
        MinecraftForge.EVENT_BUS.addListener(event -> this.onClientTickEventEnd());
    }

    @Override
    public boolean fireClientHandleSystemMessage(Component message, boolean isOverlay)
    {
        return MinecraftForge.EVENT_BUS.post(new ClientSystemChatEvent(message, isOverlay));
    }
}