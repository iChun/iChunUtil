package me.ichun.mods.ichunutil.client.tracker.tag;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.tracker.entity.EntityTracker;
import net.minecraft.client.renderer.IRenderTypeBuffer;

public interface Tag
{
    default int maxTracks(){ return 0; }
    default int maxDeathPersist() { return 0; }
    default float width(EntityTracker tracker) { return 0F; }
    default float height(EntityTracker tracker) { return 0F; }
    default void init(EntityTracker tracker){}
    default void tick(EntityTracker tracker){}
    default void addInfo(EntityTracker tracker, EntityTracker.EntityInfo info){}
    default void removeInfo(EntityTracker tracker, EntityTracker.EntityInfo info){}
    default boolean ignoreFrustumCheck() { return false; }
    default void render(EntityTracker entity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {};
}
