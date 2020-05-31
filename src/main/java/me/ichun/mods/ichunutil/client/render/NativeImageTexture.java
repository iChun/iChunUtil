package me.ichun.mods.ichunutil.client.render;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.io.IOException;

public class NativeImageTexture extends Texture
{
    @Nonnull
    public final NativeImage image;
    private final ResourceLocation resourceLocation;

    public NativeImageTexture(@Nonnull NativeImage image)
    {
        this.image = image;
        this.resourceLocation = new ResourceLocation("ichunutil", "native_image_" + Math.abs(image.hashCode()));
    }

    @Override
    public void loadTexture(IResourceManager manager) throws IOException
    {
        if(this.glTextureId == -1)
        {
            TextureUtil.prepareImage(getGlTextureId(), image.getWidth(), image.getHeight());
            image.uploadTextureSub(0, 0, 0, true);
        }
    }

    public ResourceLocation getResourceLocation()
    {
        return resourceLocation;
    }
}
