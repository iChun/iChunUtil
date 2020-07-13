package me.ichun.mods.ichunutil.client.model.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import me.ichun.mods.ichunutil.client.item.ItemEffectHandler;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType.*;

@SuppressWarnings("deprecation")
public class ItemModelRenderer implements IBakedModel
{
    private static final List<BakedQuad> EMPTY_LIST = Collections.emptyList();

    @Nonnull
    private final IModel model;

    public <T extends ItemStackTileEntityRenderer & IModel> ItemModelRenderer(@Nonnull T renderer)
    {
        model = renderer;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand)
    {
        return EMPTY_LIST;
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
    public boolean func_230044_c_()
    {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return Minecraft.getInstance().getModelManager().getAtlasTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).getSprite(MissingTextureSprite.getLocation()); //TODO do I have to generate a particle texture sprite for Block models?
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return model.getCameraTransforms();
    }

    @Override
    public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack stack)
    {
        model.handlePerspective(cameraTransformType, stack);

        //item can't be used animation
        if(model.isDualHanded())
        {
            boolean isLeft = isLeftHand(cameraTransformType);
            if(isFirstPerson(cameraTransformType) && ItemEffectHandler.dualHandedAnimationRight > 0)
            {
                float prog = (float)Math.sin(MathHelper.clamp((isLeft ? MathHelper.lerp(iChunUtil.eventHandlerClient.partialTick, ItemEffectHandler.prevDualHandedAnimationLeft, ItemEffectHandler.dualHandedAnimationLeft) : MathHelper.lerp(iChunUtil.eventHandlerClient.partialTick, ItemEffectHandler.prevDualHandedAnimationRight, ItemEffectHandler.dualHandedAnimationRight)) / (float)ItemEffectHandler.dualHandedAnimationTime, 0F, 1F) * Math.PI / 4F);
                stack.rotate(Vector3f.XN.rotationDegrees(30F * prog));
                stack.translate(0F, -0.1F * prog, 0.3F * prog);
                stack.rotate(Vector3f.YP.rotationDegrees(35F * prog));
            }
        }
        ForgeHooksClient.handlePerspective(this, cameraTransformType, stack);
        return this;
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return ItemOverrideListHandler.INSTANCE.setItemModel(this);
    }

    private static final class ItemOverrideListHandler extends ItemOverrideList
    {
        private static final ItemOverrideListHandler INSTANCE = new ItemOverrideListHandler();

        private ItemOverrideListHandler()
        {
            super();
        }

        private ItemModelRenderer itemModel;

        private ItemOverrideListHandler setItemModel(ItemModelRenderer itemModel)
        {
            this.itemModel = itemModel;
            return this;
        }

        @Override
        public IBakedModel func_239290_a_(IBakedModel originalModel, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) //getModelWithOverrides
        {
            itemModel.model.handleItemState(stack, world, entity);
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
        return type == null || type == HEAD || type == GUI || type == GROUND || type == NONE;
    }
}
