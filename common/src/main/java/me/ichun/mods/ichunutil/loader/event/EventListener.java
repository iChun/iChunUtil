package me.ichun.mods.ichunutil.loader.event;

import java.util.ArrayList;
import java.util.function.Consumer;

public class EventListener<T>
{
    private final ArrayList<Consumer<T>> listeners = new ArrayList<>();

    public EventListener(Consumer<EventListener<T>> registration)
    {
        registration.accept(this);
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
