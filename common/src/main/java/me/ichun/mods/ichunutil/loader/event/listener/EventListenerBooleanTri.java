package me.ichun.mods.ichunutil.loader.event.listener;

import org.apache.commons.lang3.function.TriConsumer;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Consumer;

public class EventListenerBooleanTri<T, U, V>
{
    private final ArrayList<TriFunction<T, U, V, Boolean>> listeners = new ArrayList<>();

    public EventListenerBooleanTri(@Nullable Consumer<EventListenerBooleanTri<T, U, V>> registration)
    {
        if(registration != null) registration.accept(this);
    }

    public void register(TriConsumer<T, U, V> listener)
    {
        listeners.add((t, u, v) -> {
            listener.accept(t, u, v);
            return false;
        });
    }

    public void register(TriFunction<T, U, V, Boolean> listener)
    {
        listeners.add(listener);
    }

    public boolean trigger(T t, U u, V v) //if any of the listeners return true, this returns true
    {
        for(TriFunction<T, U, V, Boolean> listener : listeners)
        {
            if(listener.apply(t, u, v)) return true;
        }
        return false;
    }
}
