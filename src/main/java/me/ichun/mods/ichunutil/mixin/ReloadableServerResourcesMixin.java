package me.ichun.mods.ichunutil.mixin;

import me.ichun.mods.ichunutil.loader.LoaderHandler;
import me.ichun.mods.ichunutil.loader.fabric.event.FabricEvents;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ReloadableServerResources.class)
public abstract class ReloadableServerResourcesMixin
{
    private boolean capturing = false;

    @Inject(method = "listeners", at = @At("HEAD"), cancellable = true)
    public void listeners(CallbackInfoReturnable<List<PreparableReloadListener>> cir)
    {
        if(LoaderHandler.isFabricEnv())
        {
            if(!capturing)
            {
                capturing = true;

                List<PreparableReloadListener> listeners = ((ReloadableServerResources)(Object)this).listeners();

                ArrayList<PreparableReloadListener> newListeners = new ArrayList<>(listeners);

                FabricEvents.ADD_RELOAD_LISTENER.invoker().onAddReloadListener(newListeners);

                capturing = false;

                cir.setReturnValue(newListeners);
            }
        }
    }
}
