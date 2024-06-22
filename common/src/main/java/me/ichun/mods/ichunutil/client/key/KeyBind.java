package me.ichun.mods.ichunutil.client.key;

import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Consumer;

public class KeyBind
{
    private static final HashMap<String, Object> KEY_CONFLICT_CONTEXT = new HashMap<>();

    public static void registerKeyConflictContext(String key, Object o)
    {
        KEY_CONFLICT_CONTEXT.put(key, o);
    }

    public static Object getKeyConflictContext(String key)
    {
        return KEY_CONFLICT_CONTEXT.get(key);
    }

    public static boolean areKeyConflictContextsRegistered()
    {
        return !KEY_CONFLICT_CONTEXT.isEmpty();
    }

    @NotNull
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
    public KeyBind(KeyMapping keyBinding, @Nullable Consumer<KeyBind> pressConsumer, @Nullable Consumer<KeyBind> releaseConsumer, String...conflictContext)
    {
        this.keyBinding = keyBinding;
        this.pressConsumer = pressConsumer;
        this.releaseConsumer = releaseConsumer;

        iChunUtil.eC().registerKeyMapping(this.keyBinding, conflictContext);

        iChunUtil.eC().registerClientTickEndListener(this::onClientTick);
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
}
