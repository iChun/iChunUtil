package me.ichun.mods.ichunutil.mixin.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(LivingEntityRenderer.class)
public interface LivingEntityRendererAccessorMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>
{
    @Accessor
    List<RenderLayer<S, M>> getLayers();

    @Accessor
    void setModel(M newModel);

    @Invoker
    boolean invokeAddLayer(RenderLayer<S, M> layer);
}
