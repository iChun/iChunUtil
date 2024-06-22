package me.ichun.mods.ichunutil.loader.neoforge.event.client;

import net.minecraft.client.gui.screens.Overlay;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.Nullable;

public class OverlayChangeEvent extends Event
{
    @Nullable
    private final Overlay currentOverlay;
    @Nullable
    private final Overlay newOverlay;

    public OverlayChangeEvent(@Nullable Overlay currentOverlay, @Nullable Overlay newOverlay) {
        this.currentOverlay = currentOverlay;
        this.newOverlay = newOverlay;
    }

    @Nullable
    public Overlay getCurrentOverlay()
    {
        return currentOverlay;
    }

    @Nullable
    public Overlay getNewOverlay()
    {
        return newOverlay;
    }
}
