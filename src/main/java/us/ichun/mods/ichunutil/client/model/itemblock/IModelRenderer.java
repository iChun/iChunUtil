package us.ichun.mods.ichunutil.client.model.itemblock;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;

public interface IModelRenderer extends ISmartBlockModel, ISmartItemModel
{
    public void bindTexture();
    public void renderModel();
    public ModelRenderer getModel();
    public ItemCameraTransforms getCameraTransforms();
}
