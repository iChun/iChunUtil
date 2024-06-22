package me.ichun.mods.ichunutil.mixin.client;

import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatListener.class)
public abstract class ChatListenerMixin
{
    @Inject(method = "handleSystemMessage", at = @At("HEAD"), cancellable = true)
    private void ichunutil$handleSystemMessage(Component message, boolean isOverlay, CallbackInfo ci)
    {
        if(iChunUtil.eventHandlerClient.fireClientHandleSystemMessage(message, isOverlay))
        {
            ci.cancel();
        }
    }
}
