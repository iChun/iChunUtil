package me.ichun.mods.ichunutil.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public interface EntityPersistentDataHandler
{
    @NotNull
    CompoundTag getPersistentData(@NotNull Entity ent);
    void savePersistentData(@NotNull Entity ent, @NotNull CompoundTag tag);
    void loadPersistentData(@NotNull Entity ent, @NotNull CompoundTag tag);
}

