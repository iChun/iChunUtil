package me.ichun.mods.ichunutil.client.render;

import me.ichun.mods.ichunutil.common.entity.LatchedEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import javax.annotation.Nullable;

public class LatchedEntityRenderer<T extends LatchedEntity> extends EntityRenderer<T>
{
    protected LatchedEntityRenderer(EntityRendererManager p_i46179_1_)
    {
        super(p_i46179_1_);
    }

    @Override
    public void doRender(T p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
    {
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(T t)
    {
        return null;
    }

    //This class is for entities that are just listeners and do not render anything
    public static class RenderFactory implements IRenderFactory<LatchedEntity>
    {
        @Override
        public EntityRenderer<LatchedEntity> createRenderFor(EntityRendererManager manager)
        {
            return new LatchedEntityRenderer(manager);
        }
    }
}
