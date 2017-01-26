package me.ichun.mods.ichunutil.client.model.item;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;

public interface IPerspectiveAwareModelBase extends IModelBase
{
    Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, Pair<? extends IBakedModel, Matrix4f> pair);
    boolean useVanillaCameraTransform();
}
