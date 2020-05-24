package me.ichun.mods.ichunutil.client.model.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface IModel //has to extend ItemStackTileEntityRenderer!
{
    default boolean isDualHanded() { return true; }

    default void setToOrigin(MatrixStack stack)
    {
        stack.translate(0.5D, 0.5D, 0.5D); //reset the translation in ItemRenderer
        stack.translate(0.0D, 1.5D, 0.0D); //translate down to the base of models
        stack.scale(-1F, -1F, 1F); //flip the models so it renders upright
    }

    ItemCameraTransforms getCameraTransforms();

    void handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat);

    void handleItemState(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity);
}
