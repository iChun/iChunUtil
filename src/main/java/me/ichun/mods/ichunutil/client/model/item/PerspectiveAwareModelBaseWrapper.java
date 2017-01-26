package me.ichun.mods.ichunutil.client.model.item;

import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;

import static net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.*;

public class PerspectiveAwareModelBaseWrapper extends ModelBaseWrapper
    implements IPerspectiveAwareModel
{
    private final IPerspectiveAwareModelBase perspectiveAwareModelBase;
    private final Pair<IBakedModel, Matrix4f> selfPair;
    public PerspectiveAwareModelBaseWrapper(IPerspectiveAwareModelBase renderer)
    {
        super(renderer);
        perspectiveAwareModelBase = renderer;
        selfPair = Pair.of(this, null);
    }

    @SuppressWarnings("deprecation")
    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
    {
        Pair<? extends IBakedModel, Matrix4f> pair = perspectiveAwareModelBase.handlePerspective(cameraTransformType, selfPair);

        if(perspectiveAwareModelBase.useVanillaCameraTransform())
        {
            ItemCameraTransforms.applyTransformSide(this.getItemCameraTransforms().getTransform(cameraTransformType), cameraTransformType == FIRST_PERSON_LEFT_HAND || cameraTransformType == THIRD_PERSON_LEFT_HAND);
        }
        return pair;
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
        return type == null || type == GROUND || type == NONE;
    }
}
