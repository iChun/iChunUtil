package me.ichun.mods.ichunutil.client.model.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("deprecation")
public class ModelEmpty implements IBakedModel
{
    public static final ModelEmpty INSTANCE = new ModelEmpty();

    private static final List<BakedQuad> DUMMY_LIST = Collections.emptyList();

    private TextureAtlasSprite particleTexture;

    private ModelEmpty()
    {
        this.particleTexture = null;
    }

    public ModelEmpty(TextureAtlasSprite particleTexture)
    {
        this.particleTexture = particleTexture;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
    {
        return DUMMY_LIST;
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return false;
    }

    @Override
    public boolean isGui3d()
    {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return particleTexture;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return ItemOverrideList.NONE;
    }
}
