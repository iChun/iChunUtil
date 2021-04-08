package me.ichun.mods.ichunutil.client.toast;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import me.ichun.mods.ichunutil.common.util.ObfHelper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

@OnlyIn(Dist.CLIENT)
public class ToastGui extends net.minecraft.client.gui.toasts.ToastGui
{
    public ToastGui(Minecraft mc)
    {
        super(mc);
        ObfuscationReflectionHelper.setPrivateValue(net.minecraft.client.gui.toasts.ToastGui.class, this, mc.getToastGui().visible, ObfHelper.visible);
        ObfuscationReflectionHelper.setPrivateValue(net.minecraft.client.gui.toasts.ToastGui.class, this, mc.getToastGui().toastsQueue, ObfHelper.toastsQueue);
    }

    @Override
    public void func_238541_a_(MatrixStack stack) {
        if (!this.mc.gameSettings.hideGUI) {
            RenderSystem.pushMatrix();
            for(int i = 0; i < this.visible.length; ++i) {
                net.minecraft.client.gui.toasts.ToastGui.ToastInstance<?> toastinstance = this.visible[i];
                if (toastinstance != null) {
                    if(toastinstance.render(this.mc.getMainWindow().getScaledWidth(), 0, stack))
                    {
                        this.visible[i] = null;
                    }
                    RenderSystem.translatef(0F, toastinstance.getToast().func_238540_d_(), 1);
                }

                if (this.visible[i] == null && !this.toastsQueue.isEmpty()) {
                    this.visible[i] = new net.minecraft.client.gui.toasts.ToastGui.ToastInstance(this.toastsQueue.removeFirst());
                }
            }
            RenderSystem.popMatrix();
        }
    }
}
