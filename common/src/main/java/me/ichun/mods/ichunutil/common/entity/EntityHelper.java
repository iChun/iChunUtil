package me.ichun.mods.ichunutil.common.entity;

import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class EntityHelper
{
    public static final String PLAYER_PERSISTED_NBT_TAG = "PlayerPersisted"; //As per Forge.

    public static CompoundTag getPlayerPersistentData(Player player, String key)
    {
        CompoundTag playerPersisted = iChunUtil.eS().getEntityPersistedDataHandler().getPersistentData(player).getCompound(PLAYER_PERSISTED_NBT_TAG);
        iChunUtil.eS().getEntityPersistedDataHandler().getPersistentData(player).put(PLAYER_PERSISTED_NBT_TAG, playerPersisted);
        CompoundTag persistentTag = playerPersisted.getCompound(key);
        playerPersisted.put(key, persistentTag);
        return persistentTag;
    }

    public static boolean isFakePlayer(ServerPlayer player)
    {
        return iChunUtil.eS().isFakePlayer(player);
    }

    public static int wrap(int input, int min, int max)
    {
        while(input > max)
        {
            input = min + (input - 1 - max);
        }
        while(input < min)
        {
            input = max - (input + 1 - min);
        }
        return input;
    }
}
