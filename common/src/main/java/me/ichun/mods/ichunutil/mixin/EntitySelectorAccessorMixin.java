package me.ichun.mods.ichunutil.mixin;

import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@Mixin(EntitySelector.class)
public interface EntitySelectorAccessorMixin
{
    @Accessor
    Function<Vec3, Vec3> getPosition();

    @Accessor
    AABB getAabb();

    @Accessor
    boolean getCurrentEntity();

    @Accessor
    EntityTypeTest<Entity, ?> getType();

    @Invoker
    Predicate<Entity> invokeGetPredicate(Vec3 pos, @Nullable AABB box, @Nullable FeatureFlagSet enabledFeatures);

    @Invoker
    <T extends Entity> List<T> invokeSortAndLimit(Vec3 pos, List<T> entities);
}
