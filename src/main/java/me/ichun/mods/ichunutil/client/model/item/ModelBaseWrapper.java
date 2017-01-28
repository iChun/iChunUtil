package me.ichun.mods.ichunutil.client.model.item;

import com.google.common.collect.ImmutableList;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class ModelBaseWrapper implements IBakedModel
{
    private static final List<BakedQuad> DUMMY_LIST = Collections.emptyList();

    private final @Nonnull IModelBase modelBase; //an (outdated) example of IModelBase can be found here https://gist.github.com/iChun/b6f3696a119365bbd7e4
    private @Nonnull VertexFormat defaultVertexFormat = DefaultVertexFormats.ITEM;

    private boolean disableRender = false;

    public ModelBaseWrapper(@Nonnull IModelBase renderer)
    {
        modelBase = renderer;
    }

    public ModelBaseWrapper(@Nonnull IModelBase renderer, @Nonnull VertexFormat defVertexFormat)
    {
        this(renderer);
        defaultVertexFormat = defVertexFormat;
    }

    public void setDisableRender(boolean disable)
    {
        disableRender = disable;
    }

    public void handleBlockState(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
    {
        modelBase.handleBlockState(state, side, rand);
    }

    public void bindTexture(@Nonnull ResourceLocation rs)
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(rs);
    }

    protected void rebindTexture()
    {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
    {
        if(side != null) return DUMMY_LIST;

        handleBlockState(state, side, rand);

        if(!disableRender)
        {
            //Render the model
            Tessellator tessellator = Tessellator.getInstance();
            tessellator.draw();

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.5D, 0.5D, 0.5D);
            GlStateManager.scale(-1.0F, -1.0F, 1.0F);

            bindTexture(modelBase.getTexture());
            modelBase.renderModel(iChunUtil.eventHandlerClient.renderTick);
            modelBase.postRender();
            rebindTexture();

            GlStateManager.popMatrix();

            VertexBuffer vertexBuffer = tessellator.getBuffer();
            vertexBuffer.begin(7, defaultVertexFormat);
        }

        return DUMMY_LIST;
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
    public TextureAtlasSprite getParticleTexture()
    {
        return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite(); //TODO do I have to generate a particle texture sprite for Block models?
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return modelBase.getCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return ItemOverrideListHandler.INSTANCE.setModelBase(modelBase);
    }

    private static final class ItemOverrideListHandler extends ItemOverrideList
    {
        private static final ItemOverrideListHandler INSTANCE = new ItemOverrideListHandler();

        private ItemOverrideListHandler()
        {
            super(ImmutableList.of());
        }

        private IModelBase modelBase;

        private ItemOverrideListHandler setModelBase(IModelBase model)
        {
            modelBase = model;
            return this;
        }

        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity)
        {
            modelBase.handleItemState(stack, world, entity);
            return originalModel;
        }
    }
}
