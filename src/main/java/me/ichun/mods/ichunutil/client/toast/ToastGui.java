package me.ichun.mods.ichunutil.client.toast;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ToastGui extends net.minecraft.client.gui.toasts.ToastGui
{
    public ToastGui(Minecraft mc)
    {
        super(mc);
        this.visible = mc.getToastGui().visible;
        this.toastsQueue = mc.getToastGui().toastsQueue;
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
