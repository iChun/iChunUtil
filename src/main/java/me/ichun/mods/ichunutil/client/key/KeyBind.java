package me.ichun.mods.ichunutil.client.key;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class KeyBind
{
    @Nonnull
    public final KeyBinding keyBinding;
    @Nullable
    public final Consumer<KeyBind> pressConsumer;
    @Nullable
    public final Consumer<KeyBind> releaseConsumer;
    @Nullable
    public Consumer<KeyBind> tickConsumer;

    public boolean pressed = false;
    public int pressTime = 0;

    /**
     * Construct during Client Setup Event
     * @param keyBinding key binding!
     * @param pressConsumer press consumer
     * @param releaseConsumer release consumer
     */
    public KeyBind(KeyBinding keyBinding, @Nullable Consumer<KeyBind> pressConsumer, @Nullable Consumer<KeyBind> releaseConsumer)
    {
        this.keyBinding = keyBinding;
        this.pressConsumer = pressConsumer;
        this.releaseConsumer = releaseConsumer;

        ClientRegistry.registerKeyBinding(this.keyBinding);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public KeyBind setTickConsumer(Consumer<KeyBind> tickConsumer)
    {
        this.tickConsumer = tickConsumer;
        return this;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            if(pressed)
            {
                pressTime++;
                if(!keyBinding.isKeyDown())
                {
                    pressed = false;
                    if(releaseConsumer != null)
                    {
                        releaseConsumer.accept(this);
                    }
                }
                else if(tickConsumer != null)
                {
                    tickConsumer.accept(this);
                }
            }
            else
            {
                pressTime = 0;
                if(keyBinding.isKeyDown())
                {
                    pressed = true;
                    if(pressConsumer != null)
                    {
                        pressConsumer.accept(this);
                    }
                }
            }
        }
    }
}
