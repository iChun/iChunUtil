package me.ichun.mods.ichunutil.mixin.client;

import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin
{
    @Inject(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"), cancellable = true)
    private void ichunutil$onScroll(long windowPointer, double xOffset, double yOffset, CallbackInfo ci)
    {
        if(iChunUtil.d().env().isFabric())
        {
            boolean bl = Minecraft.getInstance().options.discreteMouseScroll().get();
            double d = Minecraft.getInstance().options.mouseWheelSensitivity().get();
            double scrollDeltaX = (bl ? Math.signum(xOffset) : xOffset) * d;
            double scrollDeltaY = (bl ? Math.signum(yOffset) : yOffset) * d;

            if(iChunUtil.eC().fireMouseScroll(scrollDeltaX, scrollDeltaY))
            {
                ci.cancel();
            }
        }
    }
}
