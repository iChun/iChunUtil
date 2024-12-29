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

    public NativeImageTexture(@NotNull NativeImage image)
    {
        super(image);
        this.resourceLocation = ResourceLocation.fromNamespaceAndPath("ichunutil", "native_image_" + Math.abs(image.hashCode()));
    }

    public ResourceLocation getResourceLocation()
    {
        return resourceLocation;
    }
}
