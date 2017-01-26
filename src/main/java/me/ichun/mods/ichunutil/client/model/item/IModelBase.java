package me.ichun.mods.ichunutil.client.model.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IModelBase
{
    @Nonnull ResourceLocation getTexture(); //ResourceLocation to the model texture
    void renderModel(float renderTick); //To actually transform/scale and render the model.
    void postRender();
    @Nonnull ModelBase getModel();
    ItemCameraTransforms getCameraTransforms();
    void handleBlockState(@Nullable IBlockState state, @Nullable EnumFacing side, long rand);
    void handleItemState(ItemStack stack, World world, EntityLivingBase entity);
}
