package me.ichun.mods.ichunutil.mixin;

import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.file.Path;

@Mixin(LevelStorageSource.LevelStorageAccess.class)
public interface LevelStorageAccessAccessorMixin
{
    @Accessor
    Path getLevelPath();
}
