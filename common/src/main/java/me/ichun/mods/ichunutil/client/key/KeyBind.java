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

    public final KeyListener keyListener;

    /**
     * Construct during Client Setup Event
     * @param keyBinding key binding!
     * @param pressConsumer press consumer
     * @param releaseConsumer release consumer
     */
    public KeyBind(@NotNull KeyMapping keyBinding, @Nullable Consumer<KeyListener> pressConsumer, @Nullable Consumer<KeyListener> releaseConsumer, String...conflictContext)
    {
        this.keyListener = new KeyListener(keyBinding, pressConsumer, releaseConsumer);

        iChunUtil.eC().registerKeyMapping(this.keyListener.keyBinding, conflictContext);

        iChunUtil.eC().registerClientTickEndListener(this::onClientTick);
    }

    public KeyBind setTickConsumer(Consumer<KeyListener> tickConsumer)
    {
        this.keyListener.setTickConsumer(tickConsumer);
        return this;
    }

    public KeyBind setHoldable()
    {
        this.keyListener.setHoldable();
        return this;
    }

    public void onClientTick(Minecraft mc)
    {
        this.keyListener.tick(mc);
    }
}
