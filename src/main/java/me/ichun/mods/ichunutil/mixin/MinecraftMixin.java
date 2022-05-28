package me.ichun.mods.ichunutil.mixin;

import com.mojang.blaze3d.pipeline.MainTarget;
import me.ichun.mods.ichunutil.client.render.RenderHelper;
import me.ichun.mods.ichunutil.loader.LoaderHandler;
import me.ichun.mods.ichunutil.loader.fabric.event.FabricClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    @Inject(method = "resizeDisplay", at = @At("TAIL"))
    private void resizeDisplay(CallbackInfo ci)
    {
        for(MainTarget buffer : RenderHelper.frameBuffers)
        {
            buffer.resize(((Minecraft)(Object)this).getWindow().getWidth(), ((Minecraft)(Object)this).getWindow().getHeight(), Minecraft.ON_OSX);
        }
    }

    //For the Fabric level unload event
    @Inject(method = "setLevel", at = @At("HEAD"))
    private void setLevel(ClientLevel level, CallbackInfo ci)
    {
        if(LoaderHandler.isFabricEnv() && level != null)
        {
            FabricClientEvents.CLIENT_LEVEL_UNLOAD.invoker().onClientLevelUnload(level);
        }
    }

    //Inject after the level != null check in clearLevel
    @Inject(method = "clearLevel(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/Minecraft;level:Lnet/minecraft/client/multiplayer/ClientLevel;",
            opcode = Opcodes.GETFIELD,
            shift = At.Shift.AFTER
    ))
    private void clearLevel(Screen screen, CallbackInfo ci)
    {
        if(LoaderHandler.isFabricEnv()) //TODO test this! breakpoint it
        {
            FabricClientEvents.CLIENT_LEVEL_UNLOAD.invoker().onClientLevelUnload(((Minecraft)(Object)this).level);
        }
    }
    //End Fabric level unload
}
