package me.ichun.mods.ichunutil.client.model.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public interface IModel //has to extend ItemStackTileEntityRenderer!
{
    default boolean isDualHanded() { return true; }

    default void setToOrigin(PoseStack stack)
    {
        stack.translate(0.5D, 0.5D, 0.5D); //reset the translation in ItemRenderer
        stack.scale(-1F, -1F, 1F); //flip the models so it renders upright
    }

    ItemTransforms getCameraTransforms();

    void handlePerspective(ItemTransforms.TransformType cameraTransformType, PoseStack mat);

    void handleItemState(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity);
}
