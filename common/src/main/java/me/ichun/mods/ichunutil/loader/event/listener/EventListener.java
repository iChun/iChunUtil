package me.ichun.mods.ichunutil.loader.event.listener;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Consumer;

public class EventListener<T>
{
    private final ArrayList<Consumer<T>> listeners = new ArrayList<>();

    public EventListener(@Nullable Consumer<EventListener<T>> registration)
    {
        if(registration != null) registration.accept(this);
    }

    public void register(Consumer<T> listener)
    {
        listeners.add(listener);
    }

    public void trigger(T t)
    {
        for(Consumer<T> listener : listeners)
        {
            listener.accept(t);
        }
    }
}
