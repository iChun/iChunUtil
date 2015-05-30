package us.ichun.mods.ichunutil.client.model.itemblock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelCreeper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;

import java.util.Collections;
import java.util.List;

public class ModelRendererWrapper implements IFlexibleBakedModel, ISmartBlockModel, ISmartItemModel
{
    private static List<BakedQuad> dummyList = Collections.emptyList();

    //Cannot be null
    private final IModelRenderer modelRenderer;

    public boolean disableRender = false;

    public ModelRendererWrapper(IModelRenderer renderer)
    {
        modelRenderer = renderer;
    }

    @Override
    public List<BakedQuad> getFaceQuads(EnumFacing side)
    {
        return dummyList;
    }

    @Override
    public List<BakedQuad> getGeneralQuads()
    {
        if(disableRender)
        {
            Tessellator tessellator = Tessellator.getInstance();
            tessellator.draw();

            modelRenderer.bindTexture();
            modelRenderer.renderModel();

            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            worldrenderer.startDrawingQuads();
            worldrenderer.setVertexFormat(DefaultVertexFormats.ITEM);
        }
        return dummyList;
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return true;
    }

    @Override
    public boolean isGui3d()
    {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return false;
    }

    @Override
    public TextureAtlasSprite getTexture()
    {
        return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return modelRenderer.getCameraTransforms();
    }

    @Override
    public VertexFormat getFormat()
    {
        return DefaultVertexFormats.ITEM;
    }

    @Override
    public IBakedModel handleBlockState(IBlockState state)
    {
        modelRenderer.handleBlockState(state);
        return this;
    }

    @Override
    public IBakedModel handleItemState(ItemStack stack)
    {
        modelRenderer.handleItemState(stack);
        return this;
    }
}
