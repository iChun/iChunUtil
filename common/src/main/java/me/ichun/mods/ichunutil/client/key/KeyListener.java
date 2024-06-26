package me.ichun.mods.ichunutil.client.key;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class KeyListener
{
    @NotNull
    public final KeyMapping keyBinding;
    @Nullable
    public final Consumer<KeyListener> pressConsumer;
    @Nullable
    public final Consumer<KeyListener> releaseConsumer;
    @Nullable
    public Consumer<KeyListener> tickConsumer;

    public boolean pressed = false;
    public int pressTime = 0;

    public boolean holdable = false;
    public int holdTime = 0;

    public KeyListener(@NotNull KeyMapping keyBinding, @Nullable Consumer<KeyListener> pressConsumer, @Nullable Consumer<KeyListener> releaseConsumer)
    {
        this.keyBinding = keyBinding;
        this.pressConsumer = pressConsumer;
        this.releaseConsumer = releaseConsumer;
    }

    public KeyListener setTickConsumer(Consumer<KeyListener> tickConsumer)
    {
        this.tickConsumer = tickConsumer;
        return this;
    }

    public KeyListener setHoldable()
    {
        this.holdable = true;
        return this;
    }


    public void tick(Minecraft mc)
    {
        if(pressed)
        {
            pressTime++;
            if(!keyBinding.isDown())
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
            if(keyBinding.isDown())
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
