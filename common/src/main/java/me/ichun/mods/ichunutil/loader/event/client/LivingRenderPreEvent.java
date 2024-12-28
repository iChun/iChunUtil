package me.ichun.mods.ichunutil.loader.event.client;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.function.Predicate;

public record LivingRenderPreEvent(LivingEntity livingEntity, LivingEntityRenderer<?, ?, ?> renderer, LivingEntityRenderState renderState, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick)
{
    /**
     * Tracks a specific LivingEntity class rendered by predicate and returns the entity object on #get()
     * @param <E> LivingEntity subclass to provide
     */
    public static class LastRenderedEntitySupplier<E extends LivingEntity>
    {
        public final Predicate<LivingRenderPreEvent> predicate;

        @Nullable
        private WeakReference<E> entity;

        public LastRenderedEntitySupplier(Predicate<LivingRenderPreEvent> predicate)
        {
            this.predicate = predicate;

            iChunUtil.eC().registerClientLevelLoadListener(level -> clean());
            iChunUtil.eC().registerOnClientDisconnectListener(client -> clean());

            iChunUtil.eC().registerLivingRenderPreListener(this::onLivingRenderPreEvent);
        }

        private void clean()
        {
            entity = null;
        }

        @SuppressWarnings("unchecked")
        private void onLivingRenderPreEvent(LivingRenderPreEvent event)
        {
            if(predicate.test(event)) //please check for your entity type
            {
                entity = new WeakReference<>((E)event.livingEntity);
            }
        }

        @Nullable
        public E get()
        {
            if(entity == null || entity.get() == null)
            {
                return null;
            }
            return entity.get();
        }
    }
}
