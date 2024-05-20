package me.ichun.mods.ichunutil.client.render;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class NativeImageTexture extends AbstractTexture
{
    @NotNull
    public final NativeImage image;
    private final ResourceLocation resourceLocation;

    public NativeImageTexture(@NotNull NativeImage image)
    {
        this.image = image;
        this.resourceLocation = new ResourceLocation("ichunutil", "native_image_" + Math.abs(image.hashCode()));
    }

    @Override
    public void load(ResourceManager manager) throws IOException
    {
        if(this.id == -1)
        {
            TextureUtil.prepareImage(getId(), image.getWidth(), image.getHeight());
            image.upload(0, 0, 0, true);
        }
    }

    public ResourceLocation getResourceLocation()
    {
        return resourceLocation;
    }
}
