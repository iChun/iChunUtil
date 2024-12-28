package me.ichun.mods.ichunutil.loader.event.listener;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

public class EventListenerBoolean<T>
{
    private final ArrayList<Function<T, Boolean>> listeners = new ArrayList<>();

    public EventListenerBoolean(@Nullable Consumer<EventListenerBoolean<T>> registration)
    {
        if(registration != null) registration.accept(this);
    }

    public void register(Consumer<T> listener)
    {
        listeners.add(t -> {
            listener.accept(t);
            return false;
        });
    }

    public void register(Function<T, Boolean> listener)
    {
        listeners.add(listener);
    }

    public boolean trigger(T t) //if any of the listeners return true, this returns true
    {
        for(Function<T, Boolean> listener : listeners)
        {
            if(listener.apply(t)) return true;
        }
        return false;
    }
}
