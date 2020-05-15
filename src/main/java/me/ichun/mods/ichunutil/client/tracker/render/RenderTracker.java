package me.ichun.mods.ichunutil.client.tracker.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.tracker.entity.EntityTracker;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderTracker extends EntityRenderer<EntityTracker>
{
    protected RenderTracker(EntityRendererManager renderManager)
    {
        super(renderManager);
    }

    @Override
    public void render(EntityTracker tracker, float entityYaw, float partialTick, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
    {
        matrixStackIn.pop(); // pop our current translate

        //we need the parent's rendering position, not the tracker's
        //we need to get the camera position. Get the current active render info
        Vec3d vec3d = this.renderManager.info.getProjectedView();
        double camX = vec3d.getX();
        double camY = vec3d.getY();
        double camZ = vec3d.getZ();

        //taken from EntityRendererManager
        double d0 = MathHelper.lerp((double)partialTick, tracker.parent.lastTickPosX, tracker.parent.getPosX());
        double d1 = MathHelper.lerp((double)partialTick, tracker.parent.lastTickPosY, tracker.parent.getPosY());
        double d2 = MathHelper.lerp((double)partialTick, tracker.parent.lastTickPosZ, tracker.parent.getPosZ());

        int parentPackedLight = this.renderManager.getPackedLight(tracker.parent, partialTick);
        Vec3d renderOffset = this.getRenderOffset(tracker, partialTick);
        double pX = d0 - camX + renderOffset.getX();
        double pY = d1 - camY + renderOffset.getY();
        double pZ = d2 - camZ + renderOffset.getZ();

        //we can render
        matrixStackIn.push(); // push to get to our entity's position
        matrixStackIn.translate(pX, pY, pZ); //translate to our parent.

        tracker.tags.forEach(tag -> tag.render(tracker, entityYaw, partialTick, matrixStackIn, bufferIn, parentPackedLight));
    }

    @Override
    public ResourceLocation getEntityTexture(EntityTracker entity)
    {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }

    //This class is for entities that are just listeners and do not render anything
    public static class RenderFactory implements IRenderFactory<EntityTracker>
    {
        @Override
        public EntityRenderer<EntityTracker> createRenderFor(EntityRendererManager manager)
        {
            return new RenderTracker(manager);
        }
    }
}
