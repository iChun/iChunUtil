package me.ichun.mods.ichunutil.client.model.item;

import com.mojang.blaze3d.vertex.PoseStack;
import me.ichun.mods.ichunutil.client.item.ItemEffectHandler;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import com.mojang.math.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static net.minecraft.client.renderer.block.model.ItemTransforms.TransformType.*;

@SuppressWarnings("deprecation")
public class ItemModelRenderer implements BakedModel
{
    private static final List<BakedQuad> EMPTY_LIST = Collections.emptyList();

    @Nonnull
    private final IModel model;

    public <T extends BlockEntityWithoutLevelRenderer & IModel> ItemModelRenderer(@Nonnull T renderer)
    {
        model = renderer;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand)
    {
        return EMPTY_LIST;
    }

    @Override
    public boolean useAmbientOcclusion()
    {
        return true;
    }

    @Override
    public boolean isGui3d()
    {
        return true;
    }

    @Override
    public boolean usesBlockLight()
    {
        return true;
    }

    @Override
    public boolean isCustomRenderer()
    {
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleIcon()
    {
        return Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).getSprite(MissingTextureAtlasSprite.getLocation());
    }

    @Override
    public ItemTransforms getTransforms()
    {
        return model.getCameraTransforms();
    }

    //TODO this function is Forge only? How to handle?
//    @Override
//    public BakedModel handlePerspective(ItemTransforms.TransformType cameraTransformType, PoseStack stack)
//    {
//        model.handlePerspective(cameraTransformType, stack);
//
//        //item can't be used animation
//        if(model.isDualHanded())
//        {
//            boolean isLeft = isLeftHand(cameraTransformType);
//            if(isFirstPerson(cameraTransformType) && ItemEffectHandler.dualHandedAnimationRight > 0)
//            {
//                float prog = (float)Math.sin(Mth.clamp((isLeft ? Mth.lerp(iChunUtil.eventHandlerClient.partialTick, ItemEffectHandler.prevDualHandedAnimationLeft, ItemEffectHandler.dualHandedAnimationLeft) : Mth.lerp(iChunUtil.eventHandlerClient.partialTick, ItemEffectHandler.prevDualHandedAnimationRight, ItemEffectHandler.dualHandedAnimationRight)) / (float)ItemEffectHandler.dualHandedAnimationTime, 0F, 1F) * Math.PI / 4F);
//                stack.mulPose(Vector3f.XN.rotationDegrees(30F * prog));
//                stack.translate(0F, -0.1F * prog, 0.3F * prog);
//                stack.mulPose(Vector3f.YP.rotationDegrees(35F * prog));
//            }
//        }
//        ForgeHooksClient.handlePerspective(this, cameraTransformType, stack);
//        return this;
//    }

    @Override
    public ItemOverrides getOverrides()
    {
        return ItemOverrideListHandler.INSTANCE.setItemModel(this);
    }

    private static final class ItemOverrideListHandler extends ItemOverrides
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
        public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) //getModelWithOverrides
        {
            itemModel.model.handleItemState(stack, world, entity);
            return originalModel;
        }
    }

    public static boolean isFirstPerson(ItemTransforms.TransformType type)
    {
        return type == FIRST_PERSON_LEFT_HAND || type == FIRST_PERSON_RIGHT_HAND;
    }

    public static boolean isThirdPerson(ItemTransforms.TransformType type)
    {
        return type == THIRD_PERSON_LEFT_HAND || type == THIRD_PERSON_RIGHT_HAND;
    }

    public static boolean isEntityRender(ItemTransforms.TransformType type)
    {
        return isFirstPerson(type) || isThirdPerson(type);
    }

    public static boolean isLeftHand(ItemTransforms.TransformType type)
    {
        return type == FIRST_PERSON_LEFT_HAND || type == THIRD_PERSON_LEFT_HAND;
    }

    public static boolean isRightHand(ItemTransforms.TransformType type)
    {
        return type == FIRST_PERSON_RIGHT_HAND || type == THIRD_PERSON_RIGHT_HAND;
    }

    public static boolean isItemRender(ItemTransforms.TransformType type) //default render type
    {
        return type == null || type == HEAD || type == GUI || type == GROUND || type == NONE;
    }
}
