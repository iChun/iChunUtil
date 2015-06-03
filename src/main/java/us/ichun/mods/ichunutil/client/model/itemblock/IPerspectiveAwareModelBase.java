package us.ichun.mods.ichunutil.client.model.itemblock;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.resources.model.IBakedModel;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;

@SuppressWarnings("deprecation")
public interface IPerspectiveAwareModelBase extends IModelBase
{
    public Pair<IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, Pair<IBakedModel, Matrix4f> pair);
    public boolean useVanillaCameraTransform();
}
