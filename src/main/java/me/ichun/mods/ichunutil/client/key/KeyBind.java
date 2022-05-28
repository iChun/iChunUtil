package me.ichun.mods.ichunutil.client.key;

import me.ichun.mods.ichunutil.loader.LoaderHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class KeyBind
{
    @Nonnull
    public final KeyMapping keyBinding;
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
    public KeyBind(KeyMapping keyBinding, @Nullable Consumer<KeyBind> pressConsumer, @Nullable Consumer<KeyBind> releaseConsumer)
    {
        this.keyBinding = keyBinding;
        this.pressConsumer = pressConsumer;
        this.releaseConsumer = releaseConsumer;

        Minecraft.getInstance().options.keyMappings = ArrayUtils.add(Minecraft.getInstance().options.keyMappings, this.keyBinding); //Originally from Forge: ClientRegistry.registerKeyBinding(this.keyBinding);

        LoaderHandler.d().registerClientTickEndListener(this::onClientTick);
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

    public void onClientTick(Minecraft mc)
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

    //TODO Forge conflict context
//    public enum ConflictContext implements IKeyConflictContext
//    {
//        //Allows in-game modifiers (or lack thereof) to conflict
//        IN_GAME_MODIFIER_SENSITIVE {
//            @Override
//            public boolean isActive()
//            {
//                return !KeyConflictContext.GUI.isActive();
//            }
//
//            @Override
//            public boolean conflicts(IKeyConflictContext other)
//            {
//                return this == other;
//            }
//        }
//    }
}
