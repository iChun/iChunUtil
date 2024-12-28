package me.ichun.mods.ichunutil.loader.event.listener;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EventListenerBi<T, U>
{
    private final ArrayList<BiConsumer<T, U>> listeners = new ArrayList<>();

    public EventListenerBi(@Nullable Consumer<EventListenerBi<T, U>> registration)
    {
        if(registration != null) registration.accept(this);
    }

    public void register(BiConsumer<T, U> listener)
    {
        listeners.add(listener);
    }

    public void trigger(T t, U u)
    {
        for(BiConsumer<T, U> listener : listeners)
        {
            listener.accept(t, u);
        }
    }
}
