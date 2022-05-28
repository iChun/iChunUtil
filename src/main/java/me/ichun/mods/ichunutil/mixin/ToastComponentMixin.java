package me.ichun.mods.ichunutil.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ToastComponent.class)
public abstract class ToastComponentMixin
{
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(PoseStack stack, CallbackInfo ci)
    {
        if(iChunUtil.configClient.overrideToastGui)
        {
            Minecraft mc = Minecraft.getInstance();
            if(!mc.options.hideGui)
            {
                stack.pushPose();
                stack.translate(0F, 0F, 800F); //the toasts render 800F in front

                ToastComponent toast = ((ToastComponent)(Object)this);

                for(int i = 0; i < toast.visible.length; ++i) {
                    net.minecraft.client.gui.components.toasts.ToastComponent.ToastInstance<?> toastinstance = toast.visible[i];
                    if (toastinstance != null) {
                        if(toastinstance.render(mc.getWindow().getGuiScaledWidth(), 0, stack)) //we pass in an index of 0
                        {
                            toast.visible[i] = null;
                        }
                        stack.translate(0F, toastinstance.getToast().height(), 1F);
                    }

                    if (toast.visible[i] == null && !toast.queued.isEmpty()) {
                        toast.visible[i] = toast.new ToastInstance(toast.queued.removeFirst());
                    }
                }

                stack.popPose();
            }

            ci.cancel();
        }
    }
}
