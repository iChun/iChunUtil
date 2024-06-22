package me.ichun.mods.ichunutil.loader.event;

import org.apache.commons.lang3.function.TriConsumer;

import java.util.ArrayList;
import java.util.function.Consumer;

public class EventListenerTri<T, U, V>
{
    private final ArrayList<TriConsumer<T, U, V>> listeners = new ArrayList<>();

    public EventListenerTri(Consumer<EventListenerTri<T, U, V>> registration)
    {
        registration.accept(this);
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
