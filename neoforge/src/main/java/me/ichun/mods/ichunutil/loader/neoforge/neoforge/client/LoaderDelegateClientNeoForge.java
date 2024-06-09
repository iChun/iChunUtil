package me.ichun.mods.ichunutil.loader.neoforge.neoforge.client;

import me.ichun.mods.ichunutil.client.key.KeyBind;
import me.ichun.mods.ichunutil.loader.client.LoaderDelegateClient;
import me.ichun.mods.ichunutil.loader.neoforge.neoforge.ReflectionReferenceNeoForge;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.function.Consumer;

public class LoaderDelegateClientNeoForge implements LoaderDelegateClient
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
                ReflectionReferenceNeoForge.setKeyConflictContext(key, (IKeyConflictContext)keyConflictContext);
            }
        }

        Minecraft.getInstance().options.keyMappings = ArrayUtils.add(Minecraft.getInstance().options.keyMappings, key); //Originally from Forge: ClientRegistry.registerKeyBinding(this.keyBinding);
    }

    @Override
    public void registerClientTickStartListener(Consumer<Minecraft> consumer)
    {
        if(clientTickStartListeners.isEmpty() && clientTickEndListeners.isEmpty())
        {
            NeoForge.EVENT_BUS.addListener(this::onClientTickEventStart);
        }
        clientTickStartListeners.add(consumer);
    }

    @Override
    public void registerClientTickEndListener(Consumer<Minecraft> consumer)
    {
        if(clientTickStartListeners.isEmpty() && clientTickEndListeners.isEmpty())
        {
            NeoForge.EVENT_BUS.addListener(this::onClientTickEventEnd);
        }
        clientTickEndListeners.add(consumer);
    }

    private final ArrayList<Consumer<Minecraft>> clientTickStartListeners = new ArrayList<>();
    private final ArrayList<Consumer<Minecraft>> clientTickEndListeners = new ArrayList<>();

    private void onClientTickEventStart(ClientTickEvent.Pre event)
    {
        for(Consumer<Minecraft> listener : clientTickStartListeners)
        {
            listener.accept(Minecraft.getInstance());
        }
    }

    private void onClientTickEventEnd(ClientTickEvent.Post event)
    {
        for(Consumer<Minecraft> listener : clientTickEndListeners)
        {
            listener.accept(Minecraft.getInstance());
        }
    }
}
