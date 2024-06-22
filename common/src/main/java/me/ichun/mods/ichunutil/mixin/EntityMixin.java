package me.ichun.mods.ichunutil.mixin;

import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin
{
    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
    private void ichunutil$load(CompoundTag tag, CallbackInfo ci) //load our data before loading additional save data
    {
        if(iChunUtil.d().env().isFabric())
        {
            iChunUtil.eS().getEntityPersistedDataHandler().loadPersistentData((Entity)(Object)this, tag);
        }
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
    private void ichunutil$saveWithoutId(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir)
    {
        if(iChunUtil.d().env().isFabric())
        {
            iChunUtil.eS().getEntityPersistedDataHandler().savePersistentData((Entity)(Object)this, tag);
        }
    }
}
