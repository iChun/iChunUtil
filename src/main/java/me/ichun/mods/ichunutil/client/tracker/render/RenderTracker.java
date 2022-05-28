package me.ichun.mods.ichunutil.client.tracker.render;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.client.tracker.entity.EntityTracker;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class RenderTracker extends EntityRenderer<EntityTracker>
{
    protected RenderTracker(EntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    public void render(EntityTracker tracker, float entityYaw, float partialTick, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn)
    {
        matrixStackIn.popPose(); // pop our current translate

        //we need the parent's rendering position, not the tracker's
        //we need to get the camera position. Get the current active render info
        Vec3 vec3d = this.entityRenderDispatcher.camera.getPosition();
        double camX = vec3d.x();
        double camY = vec3d.y();
        double camZ = vec3d.z();

        //taken from EntityRendererManager
        double d0 = Mth.lerp((double)partialTick, tracker.parent.xOld, tracker.parent.getX());
        double d1 = Mth.lerp((double)partialTick, tracker.parent.yOld, tracker.parent.getY());
        double d2 = Mth.lerp((double)partialTick, tracker.parent.zOld, tracker.parent.getZ());

        int parentPackedLight = this.entityRenderDispatcher.getPackedLightCoords(tracker.parent, partialTick);
        Vec3 renderOffset = this.getRenderOffset(tracker, partialTick);
        double pX = d0 - camX + renderOffset.x();
        double pY = d1 - camY + renderOffset.y();
        double pZ = d2 - camZ + renderOffset.z();

        //we can render
        matrixStackIn.pushPose(); // push to get to our entity's position
        matrixStackIn.translate(pX, pY, pZ); //translate to our parent.

        tracker.tags.forEach(tag -> tag.render(tracker, entityYaw, partialTick, matrixStackIn, bufferIn, parentPackedLight));
    }

    @Override
    public ResourceLocation getTextureLocation(EntityTracker entity)
    {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    public static class RenderFactory implements EntityRendererProvider<EntityTracker>
    {
        @Override
        public EntityRenderer<EntityTracker> create(EntityRendererProvider.Context context)
        {
            return new RenderTracker(context);
        }
    }
}
