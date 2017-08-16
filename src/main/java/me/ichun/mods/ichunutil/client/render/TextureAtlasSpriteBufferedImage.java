package me.ichun.mods.ichunutil.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;

public class TextureAtlasSpriteBufferedImage extends TextureAtlasSprite
{
    public BufferedImage image;

    public TextureAtlasSpriteBufferedImage(ResourceLocation rl, BufferedImage image)
    {
        super(rl.toString());
        this.image = image;
    }

    public boolean hasCustomLoader(net.minecraft.client.resources.IResourceManager manager, net.minecraft.util.ResourceLocation location)
    {
        return true;
    }

    public boolean load(net.minecraft.client.resources.IResourceManager manager, net.minecraft.util.ResourceLocation location)
    {
        this.width = image.getWidth();
        this.height = image.getHeight();

        int[][] aint = new int[Minecraft.getMinecraft().getTextureMapBlocks().getMipmapLevels() + 1][];
        aint[0] = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), aint[0], 0, image.getWidth());

        this.framesTextureData.add(aint);

        this.initSprite(width, height, 0, 0, false);

        return false;
    }
}
