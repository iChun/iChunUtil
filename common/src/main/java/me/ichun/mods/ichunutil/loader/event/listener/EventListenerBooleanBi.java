package me.ichun.mods.ichunutil.loader.event.listener;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class EventListenerBooleanBi<T, U>
{
    private final ArrayList<BiFunction<T, U, Boolean>> listeners = new ArrayList<>();

    public EventListenerBooleanBi(@Nullable Consumer<EventListenerBooleanBi<T, U>> registration)
    {
        if(registration != null) registration.accept(this);
    }

    public void register(BiConsumer<T, U> listener)
    {
        listeners.add((t, u) -> {
            listener.accept(t, u);
            return false;
        });
    }

    public void register(BiFunction<T, U, Boolean> listener)
    {
        listeners.add(listener);
    }

    public boolean trigger(T t, U u) //if any of the listeners return true, this returns true
    {
        for(BiFunction<T, U, Boolean> listener : listeners)
        {
            if(listener.apply(t, u)) return true;
        }
        return false;
    }
}
