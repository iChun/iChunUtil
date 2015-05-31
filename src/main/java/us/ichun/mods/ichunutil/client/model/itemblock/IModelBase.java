package us.ichun.mods.ichunutil.client.model.itemblock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public interface IModelBase
{
    public ResourceLocation getTexture(); //Return resourcelocation to the texture
    public void renderModel(); //To actually transform/scale and render the model
    public void postRender();
    public ModelBase getModel();
    public ItemCameraTransforms getCameraTransforms();
    public void handleBlockState(IBlockState state);
    public void handleItemState(ItemStack stack);
}
