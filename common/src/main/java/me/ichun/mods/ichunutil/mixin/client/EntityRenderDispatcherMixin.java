package me.ichun.mods.ichunutil.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin
{
    @Inject(
        method = "render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;getRenderOffset(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;)Lnet/minecraft/world/phys/Vec3;"),
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true)
    private <E extends Entity, S extends EntityRenderState> void ichunutil$render(E entity, double xOffset, double yOffset, double zOffset, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, EntityRenderer<? super E, S> renderer, CallbackInfo ci, EntityRenderState entityRenderState)
    {
        if(entity instanceof LivingEntity living && renderer instanceof LivingEntityRenderer livingRenderer && entityRenderState instanceof LivingEntityRenderState livingEntityRenderState)
        {
            if(iChunUtil.eC().fireLivingRenderPreEvent(living, livingRenderer, livingEntityRenderState, poseStack, bufferSource, packedLight, partialTick))
            {
                ci.cancel();
            }
        }
    }
}
