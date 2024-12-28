package me.ichun.mods.ichunutil.loader.event.listener;

import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Consumer;

public class EventListenerTri<T, U, V>
{
    private final ArrayList<TriConsumer<T, U, V>> listeners = new ArrayList<>();

    public EventListenerTri(@Nullable Consumer<EventListenerTri<T, U, V>> registration)
    {
        if(registration != null) registration.accept(this);
    }

    public void register(TriConsumer<T, U, V> listener)
    {
        listeners.add(listener);
    }

    public void trigger(T t, U u, V v)
    {
        for(TriConsumer<T, U, V> listener : listeners)
        {
            listener.accept(t, u, v);
        }
    }
}
