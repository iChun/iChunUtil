package me.ichun.mods.ichunutil.client.render;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * See Forge/NeoForge ModListScreen#updateCache in terms of use
 */
public class NativeImageTexture extends DynamicTexture
{
    private final ResourceLocation resourceLocation;

    private NativeImageTexture(@NotNull ResourceLocation resourceLocation, @NotNull NativeImage image)
    {
        super(resourceLocation::toString, image);
        this.resourceLocation = resourceLocation;
    }

    public ResourceLocation getResourceLocation()
    {
        return resourceLocation;
    }

    public static NativeImageTexture create(@NotNull NativeImage image)
    {
        //consider taking a "label" param for the Dynamic Texture
        return new NativeImageTexture(ResourceLocation.fromNamespaceAndPath("ichunutil", "native_image_" + Math.abs(image.hashCode())), image);
    }
}
