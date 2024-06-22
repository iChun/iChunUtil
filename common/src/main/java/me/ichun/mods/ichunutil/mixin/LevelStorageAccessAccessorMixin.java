package me.ichun.mods.ichunutil.mixin;

import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelStorageSource.LevelStorageAccess.class)
public interface LevelStorageAccessAccessorMixin
{
    @Accessor
    LevelStorageSource.LevelDirectory getLevelDirectory();
}
