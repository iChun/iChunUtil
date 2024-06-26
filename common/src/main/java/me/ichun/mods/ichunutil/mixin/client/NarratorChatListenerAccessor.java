package me.ichun.mods.ichunutil.mixin.client;

import com.mojang.text2speech.Narrator;
import net.minecraft.client.gui.chat.NarratorChatListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NarratorChatListener.class)
public interface NarratorChatListenerAccessor
{
    @Accessor
    Narrator getNarrator();
}
