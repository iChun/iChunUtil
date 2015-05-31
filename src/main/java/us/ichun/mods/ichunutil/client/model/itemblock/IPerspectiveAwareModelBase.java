package us.ichun.mods.ichunutil.client.model.itemblock;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms;

public interface IPerspectiveAwareModelBase extends IModelBase
{
    public void handlePerspective(ItemCameraTransforms.TransformType cameraTransformType);
}
