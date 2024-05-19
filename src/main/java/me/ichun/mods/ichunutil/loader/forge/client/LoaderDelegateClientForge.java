package me.ichun.mods.ichunutil.loader.forge.client;

import me.ichun.mods.ichunutil.client.key.KeyBind;
import me.ichun.mods.ichunutil.loader.client.LoaderDelegateClient;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.function.Consumer;

public class LoaderDelegateClientForge implements LoaderDelegateClient
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
    public void registerClientTickStartListener(Consumer<Minecraft> consumer)
    {
        if(clientTickStartListeners.isEmpty() && clientTickEndListeners.isEmpty())
        {
            MinecraftForge.EVENT_BUS.addListener(this::onClientTickEvent);
        }
        clientTickStartListeners.add(consumer);
    }

    @Override
    public void registerClientTickEndListener(Consumer<Minecraft> consumer)
    {
        if(clientTickStartListeners.isEmpty() && clientTickEndListeners.isEmpty())
        {
            MinecraftForge.EVENT_BUS.addListener(this::onClientTickEvent);
        }
        clientTickEndListeners.add(consumer);
    }

    private final ArrayList<Consumer<Minecraft>> clientTickStartListeners = new ArrayList<>();
    private final ArrayList<Consumer<Minecraft>> clientTickEndListeners = new ArrayList<>();

    private void onClientTickEvent(ClientTickEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        if(event.phase == TickEvent.Phase.START)
        {
            for(Consumer<Minecraft> listener : clientTickStartListeners)
            {
                listener.accept(mc);
            }
        }
        else
        {
            for(Consumer<Minecraft> listener : clientTickEndListeners)
            {
                listener.accept(mc);
            }
        }
    }
}