package me.ichun.mods.ichunutil.mixin;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityInvokerMixin
{
    @Invoker
    SoundEvent callGetHurtSound(DamageSource source);

    @Invoker
    SoundEvent callGetDeathSound();

    @Invoker
    float callGetSoundVolume();

    @Invoker
    float callGetVoicePitch();

    @Invoker
    void callOnEffectUpdated(MobEffectInstance mobEffectInstance, boolean reapply, @Nullable Entity entity);
}
