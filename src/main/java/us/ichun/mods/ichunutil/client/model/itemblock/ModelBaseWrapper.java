package us.ichun.mods.ichunutil.client.model.itemblock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;

import java.util.Collections;
import java.util.List;

public class ModelBaseWrapper implements IFlexibleBakedModel, ISmartBlockModel, ISmartItemModel
{
    private static List<BakedQuad> dummyList = Collections.emptyList();

    //Cannot be null
    private final IModelBase modelBase; //an example of IModelBase can be found here https://gist.github.com/iChun/b6f3696a119365bbd7e4

    public boolean disableRender = false;

    public ModelBaseWrapper(IModelBase renderer)
    {
        modelBase = renderer;
    }

    @Override
    public List<BakedQuad> getFaceQuads(EnumFacing side)
    {
        return dummyList;
    }

    @Override
    public List<BakedQuad> getGeneralQuads()
    {
        if(!disableRender)
        {
            Tessellator tessellator = Tessellator.getInstance();
            if (tessellator.getWorldRenderer().isDrawing)
                tessellator.draw();

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.5D, 0.5D, 0.5D);
            GlStateManager.scale(-1.0F, -1.0F, 1.0F);

            bindTexture(modelBase.getTexture());
            modelBase.renderModel();
            modelBase.postRender();
            rebindTexture();

            GlStateManager.popMatrix();

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
        return modelBase.getCameraTransforms();
    }

    @Override
    public VertexFormat getFormat()
    {
        return DefaultVertexFormats.ITEM;
    }

    @Override
    public IBakedModel handleBlockState(IBlockState state)
    {
        modelBase.handleBlockState(state);
        return this;
    }

    @Override
    public IBakedModel handleItemState(ItemStack stack)
    {
        modelBase.handleItemState(stack);
        return this;
    }

    public void bindTexture(ResourceLocation loc)
    {
        if(loc != null)
        {
            Minecraft.getMinecraft().getTextureManager().bindTexture(loc);
        }
    }

    protected void rebindTexture()
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
    }
}
//This class was made by iChun. Remember to credit if you "steal" this :(


//Heh.