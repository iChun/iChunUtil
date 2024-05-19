package me.ichun.mods.ichunutil.mixin.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(LivingEntityRenderer.class)
public interface LivingEntityRendererAccessorMixin<T extends LivingEntity, M extends EntityModel<T>>
{
    @Accessor
    List<RenderLayer<T, M>> getLayers();

    @Accessor
    void setModel(M newModel);

    @Invoker
    boolean invokeAddLayer(RenderLayer<T, M> layer);
}
