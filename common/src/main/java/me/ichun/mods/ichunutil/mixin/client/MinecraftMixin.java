package me.ichun.mods.ichunutil.mixin.client;

import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Overlay;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    @Inject(method = "setOverlay", at = @At("HEAD"))
    private void ichunutil$setOverlay(@Nullable Overlay overlay, CallbackInfo ci)
    {
        iChunUtil.eC().fireOverlayChange(((Minecraft)(Object)this).getOverlay(), overlay);
    }
}
