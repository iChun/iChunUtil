package me.ichun.mods.ichunutil.mixin;

import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin
{
    @Inject(method = "tick", at = @At("TAIL"))
    private void ichunutil$tick(CallbackInfo ci)
    {
        iChunUtil.eS().firePlayerTickEndEvent(((Player)(Object)this));
    }
}
