package me.ichun.mods.ichunutil.loader.forge;

import me.ichun.mods.ichunutil.common.entity.EntityPersistentDataHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class EntityPersistentDataHandlerForge
    implements EntityPersistentDataHandler
{
    @Override
    public @NotNull CompoundTag getPersistentData(@NotNull Entity ent)
    {
        return ent.getPersistentData();
    }

    @Override
    public void savePersistentData(@NotNull Entity ent, @NotNull CompoundTag tag){} //NOP - Forge already does persistent data.

    @Override
    public void loadPersistentData(@NotNull Entity ent, @NotNull CompoundTag tag){} //NOP - Forge already does persistent data.
}
