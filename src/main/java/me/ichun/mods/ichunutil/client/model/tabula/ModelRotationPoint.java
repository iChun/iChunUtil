package me.ichun.mods.ichunutil.client.model.tabula;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

public class ModelRotationPoint extends Model
{
    public ModelPart cube1;
    public ModelPart cube2;
    public ModelPart cube3;

    public ModelRotationPoint()
    {
//        super(rl -> ModelTabula.RENDER_MODEL_NO_TEXTURE);
        super(RenderType::entityTranslucentCull);

        ModelPart root = createLayer().bakeRoot();

        cube1 = root.getChild("cube1");
        cube2 = root.getChild("cube2");
        cube3 = root.getChild("cube3");
    }

    public static LayerDefinition createLayer()
    {

        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        float size = 1;
        root.addOrReplaceChild("cube1", CubeListBuilder.create().texOffs(0, 0).addBox(-(size / 2), -(size / 2), -(size / 2), (int)size, (int)size, (int)size), PartPose.rotation((float)Math.toRadians(45F), 0F, 0F));
        root.addOrReplaceChild("cube2", CubeListBuilder.create().texOffs(0, 0).addBox(-(size / 2), -(size / 2), -(size / 2), (int)size, (int)size, (int)size), PartPose.rotation(0F, (float)Math.toRadians(45F), 0F));
        root.addOrReplaceChild("cube3", CubeListBuilder.create().texOffs(0, 0).addBox(-(size / 2), -(size / 2), -(size / 2), (int)size, (int)size, (int)size), PartPose.rotation(0F, 0F, (float)Math.toRadians(45F)));

        return LayerDefinition.create(mesh, 2, 2);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStack, VertexConsumer iVertexBuilder, int i, int i1, float v, float v1, float v2, float v3)
    {
        cube1.render(matrixStack, iVertexBuilder, i, i1, v, v1, v2, v3);
        cube2.render(matrixStack, iVertexBuilder, i, i1, v, v1, v2, v3);
        cube3.render(matrixStack, iVertexBuilder, i, i1, v, v1, v2, v3);
    }
}
