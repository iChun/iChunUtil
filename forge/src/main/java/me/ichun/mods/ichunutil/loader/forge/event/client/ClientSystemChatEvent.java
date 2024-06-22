package me.ichun.mods.ichunutil.loader.forge.event.client;

import net.minecraft.network.chat.Component;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

@Cancelable
public class ClientSystemChatEvent extends Event
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
