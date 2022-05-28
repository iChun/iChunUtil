package me.ichun.mods.ichunutil.mixin;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityRenderer.class)
public interface EntityRendererInvokerMixin
{
    @Invoker
    ResourceLocation callGetTextureLocation(Entity ent);
}
