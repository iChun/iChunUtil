package me.ichun.mods.ichunutil.client.key;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
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

    public boolean holdable = false;
    public int holdTime = 0;

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

        MinecraftForge.EVENT_BUS.addListener(this::onClientTick);
    }

    public KeyBind setTickConsumer(Consumer<KeyBind> tickConsumer)
    {
        this.tickConsumer = tickConsumer;
        return this;
    }

    public KeyBind setHoldable()
    {
        this.holdable = true;
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
                    holdTime = 0;
                    if(releaseConsumer != null)
                    {
                        releaseConsumer.accept(this);
                    }
                }
                else
                {
                    if(tickConsumer != null)
                    {
                        tickConsumer.accept(this);
                    }
                    if(holdTime > 0)
                    {
                        holdTime--;
                        if(holdTime == 0)
                        {
                            holdTime = 5;
                            if(pressConsumer != null)
                            {
                                pressConsumer.accept(this);
                            }
                        }
                    }
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
                    if(holdable)
                    {
                        holdTime = 20;
                    }
                }
            }
        }
    }

    public enum ConflictContext implements IKeyConflictContext
    {
        //Allows in-game modifiers (or lack thereof) to conflict
        IN_GAME_MODIFIER_SENSITIVE {
            @Override
            public boolean isActive()
            {
                return !KeyConflictContext.GUI.isActive();
            }

            @Override
            public boolean conflicts(IKeyConflictContext other)
            {
                return this == other;
            }
        }
    }
}
