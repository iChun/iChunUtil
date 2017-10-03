package me.ichun.mods.ichunutil.client.model.item;

import com.google.common.collect.ImmutableList;
import me.ichun.mods.ichunutil.client.render.item.ItemRenderingHelper;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.Collections;
import java.util.List;

import static net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.*;

@SuppressWarnings("deprecation")
public class ModelBaseWrapper implements IBakedModel
{
    private static final List<BakedQuad> DUMMY_LIST = Collections.emptyList();

    private final
    @Nonnull
    IModelBase modelBase; //an (outdated) example of IModelBase can be found here https://gist.github.com/iChun/b6f3696a119365bbd7e4
    private final Pair<IBakedModel, Matrix4f> selfPair; //All models should be perspective aware. If you have no need for perspective, just ignore those capabilities.
    private
    @Nonnull
    VertexFormat defaultVertexFormat = DefaultVertexFormats.ITEM;
    private boolean isItemDualHanded = false;

    private boolean disableRender = false;
    private ItemCameraTransforms.TransformType currentPerspective;
    private ItemStack lastStack;
    private EntityLivingBase lastEntity;

    public ModelBaseWrapper(@Nonnull IModelBase renderer)
    {
        modelBase = renderer;
        selfPair = Pair.of(this, null);
    }

    public ModelBaseWrapper(@Nonnull IModelBase renderer, @Nonnull VertexFormat defVertexFormat)
    {
        this(renderer);
        defaultVertexFormat = defVertexFormat;
    }

    public ModelBaseWrapper setItemDualHanded()
    {
        isItemDualHanded = true;
        return this;
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
        if(side != null || !Tessellator.getInstance().getBuffer().isDrawing) //we're not drawing right now... don't do anything.
        {
            return DUMMY_LIST;
        }

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

            //cleanup
            currentPerspective = null;
            lastStack = null;
            lastEntity = null;

            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, defaultVertexFormat);
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
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
    {
        currentPerspective = cameraTransformType;

        Pair<? extends IBakedModel, Matrix4f> pair = modelBase.handlePerspective(cameraTransformType, selfPair);

        if(modelBase.useVanillaCameraTransform())
        {
            boolean isLeft = isLeftHand(cameraTransformType);
            if(isItemDualHanded && isFirstPerson(currentPerspective) && lastEntity instanceof EntityPlayer && ItemRenderingHelper.dualHandedAnimationRight > 0)
            {
                float prog = (float)Math.sin(MathHelper.clamp((isLeft ? EntityHelper.interpolateValues(ItemRenderingHelper.prevDualHandedAnimationLeft, ItemRenderingHelper.dualHandedAnimationLeft, iChunUtil.eventHandlerClient.renderTick) : EntityHelper.interpolateValues(ItemRenderingHelper.prevDualHandedAnimationRight, ItemRenderingHelper.dualHandedAnimationRight, iChunUtil.eventHandlerClient.renderTick)) / (float)ItemRenderingHelper.dualHandedAnimationTime, 0F, 1F) * Math.PI / 4F);
                GlStateManager.rotate(30F * prog, -1F, 0F, 0F);
                GlStateManager.translate(0F, -0.1F * prog, 0.3F * prog);
                GlStateManager.rotate((isLeft ? -35F : 35F) * prog, 0F, 1F, 0F);
            }

            ItemCameraTransforms.applyTransformSide(this.getItemCameraTransforms().getTransform(cameraTransformType), isLeft);
        }
        return pair;
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return ItemOverrideListHandler.INSTANCE.setModelBaseWrapper(this);
    }

    private static final class ItemOverrideListHandler extends ItemOverrideList
    {
        private static final ItemOverrideListHandler INSTANCE = new ItemOverrideListHandler();

        private ItemOverrideListHandler()
        {
            super(ImmutableList.of());
        }

        private ModelBaseWrapper modelBaseWrapper;

        private ItemOverrideListHandler setModelBaseWrapper(ModelBaseWrapper model)
        {
            modelBaseWrapper = model;
            return this;
        }

        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity)
        {
            modelBaseWrapper.lastStack = stack;
            modelBaseWrapper.lastEntity = entity;
            modelBaseWrapper.modelBase.handleItemState(stack, world, entity);
            return originalModel;
        }
    }

    public static boolean isFirstPerson(ItemCameraTransforms.TransformType type)
    {
        return type == FIRST_PERSON_LEFT_HAND || type == FIRST_PERSON_RIGHT_HAND;
    }

    public static boolean isThirdPerson(ItemCameraTransforms.TransformType type)
    {
        return type == THIRD_PERSON_LEFT_HAND || type == THIRD_PERSON_RIGHT_HAND;
    }

    public static boolean isEntityRender(ItemCameraTransforms.TransformType type)
    {
        return isFirstPerson(type) || isThirdPerson(type);
    }

    public static boolean isLeftHand(ItemCameraTransforms.TransformType type)
    {
        return type == FIRST_PERSON_LEFT_HAND || type == THIRD_PERSON_LEFT_HAND;
    }

    public static boolean isRightHand(ItemCameraTransforms.TransformType type)
    {
        return type == FIRST_PERSON_RIGHT_HAND || type == THIRD_PERSON_RIGHT_HAND;
    }

    public static boolean isItemRender(ItemCameraTransforms.TransformType type) //default render type
    {
        return type == null || type == GUI || type == GROUND || type == NONE;
    }
}
