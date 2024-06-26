package me.ichun.mods.ichunutil.mixin.client;

import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.gui.chat.StandardChatListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(StandardChatListener.class)
public abstract class ChatListenerMixin
{
    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private void ichunutil$handleSystemMessage(ChatType chatType, Component component, UUID senderId, CallbackInfo ci)
    {
        if(chatType == ChatType.SYSTEM && iChunUtil.eventHandlerClient.fireClientHandleSystemMessage(component, false))
        {
            ci.cancel();
        }
    }
}
