package me.ichun.mods.ichunutil.loader.neoforge.event.client;

import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.ApiStatus;

public class ClientSystemChatEvent extends Event implements ICancellableEvent
{
    private final Component component;
    private final boolean isOverlay;

    @ApiStatus.Internal
    public ClientSystemChatEvent(Component component, boolean isOverlay) {
        this.component = component;
        this.isOverlay = isOverlay;
    }

    public Component getComponent()
    {
        return component;
    }

    public boolean isOverlay()
    {
        return isOverlay;
    }
}
