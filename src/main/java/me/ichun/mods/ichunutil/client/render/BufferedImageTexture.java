package me.ichun.mods.ichunutil.client.render;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BufferedImageTexture extends Texture //TODO get rid of this! We have to use NativeImage
{
    @Nonnull
    public final BufferedImage image;
    private final ResourceLocation resourceLocation;

    public BufferedImageTexture(@Nonnull BufferedImage image)
    {
        this.image = image;
        this.resourceLocation = new ResourceLocation("ichunutil", "buffered_image_" + Math.abs(image.hashCode()));
    }

    @Override
    public void loadTexture(IResourceManager manager) throws IOException
    {
        if(this.glTextureId == -1)
        {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "png", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());

            NativeImage nativeImage = NativeImage.read(is);

            TextureUtil.prepareImage(getGlTextureId(), nativeImage.getWidth(), nativeImage.getHeight());
            nativeImage.uploadTextureSub(0, 0, 0, true);
        }
    }

    public ResourceLocation getResourceLocation()
    {
        return resourceLocation;
    }
}
