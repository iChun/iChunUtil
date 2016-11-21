package me.ichun.mods.ichunutil.client.render.entity;

import me.ichun.mods.ichunutil.client.entity.EntityLatchedRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.common.eventhandler.Event;

public class RenderLatchedRenderer extends Render<EntityLatchedRenderer>
{
    public RenderLatchedRenderer(RenderManager renderManager)
    {
        super(renderManager);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityLatchedRenderer entity)
    {
        return DefaultPlayerSkin.getDefaultSkinLegacy();
    }

    @Override
    public void doRender(EntityLatchedRenderer entity, double d, double d1, double d2, float f, float f1)
    {
        MinecraftForge.EVENT_BUS.post(new RenderLatchedRendererEvent(entity, d, d1, d2, f, f1));
    }

    public class RenderLatchedRendererEvent extends Event
    {
        public final EntityLatchedRenderer ent;
        public final double x;
        public final double y;
        public final double z;
        public final float yaw;
        public final float partialTick;

        public RenderLatchedRendererEvent(EntityLatchedRenderer ent, double x, double y, double z, float yaw, float partialTick)
        {
            this.ent = ent;
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.partialTick = partialTick;
        }
    }

    public static class RenderFactory implements IRenderFactory<EntityLatchedRenderer>
    {
        @Override
        public Render<EntityLatchedRenderer> createRenderFor(RenderManager manager)
        {
            return new RenderLatchedRenderer(manager);
        }
    }
}
