package us.ichun.mods.ichunutil.client.model.itemblock;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;

public class PerspectiveAwareModelBaseWrapper extends ModelBaseWrapper implements IPerspectiveAwareModel
{
    private final IPerspectiveAwareModelBase perspectiveAwareModelBase;
    private final Pair<IBakedModel, Matrix4f> selfPair;
    public PerspectiveAwareModelBaseWrapper(IPerspectiveAwareModelBase renderer)
    {
        super(renderer);
        perspectiveAwareModelBase = renderer;
        selfPair = Pair.of((IBakedModel)this, null);
    }

    @Override
    public Pair<IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
    {
        perspectiveAwareModelBase.handlePerspective(cameraTransformType);

        switch(cameraTransformType)//this is here since this model is Perspective aware, vanilla transforms aren't used.
        {
            case FIRST_PERSON:
                RenderItem.applyVanillaTransform(this.getItemCameraTransforms().firstPerson);
                break;
            case GUI:
                RenderItem.applyVanillaTransform(this.getItemCameraTransforms().gui);
                break;
            case HEAD:
                RenderItem.applyVanillaTransform(this.getItemCameraTransforms().head);
                break;
            case THIRD_PERSON:
                RenderItem.applyVanillaTransform(this.getItemCameraTransforms().thirdPerson);
                break;
            default:
                break;
        }
        return selfPair;
    }
}
