package me.ichun.mods.ichunutil.client.model.tabula;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelRotationPoint extends Model
{
    public ModelRenderer cube1;
    public ModelRenderer cube2;
    public ModelRenderer cube3;

    public ModelRotationPoint()
    {
        super(rl -> ModelTabula.RENDER_MODEL_NO_TEXTURE);
        float size = 1;
        cube1 = new ModelRenderer(this, 0, 0);
        cube1.addBox(-(size / 2), -(size / 2), -(size / 2), (int)size, (int)size, (int)size);
        cube1.rotateAngleX = (float)Math.toRadians(45F);
        cube2 = new ModelRenderer(this, 0, 0);
        cube2.addBox(-(size / 2), -(size / 2), -(size / 2), (int)size, (int)size, (int)size);
        cube2.rotateAngleY = (float)Math.toRadians(45F);
        cube3 = new ModelRenderer(this, 0, 0);
        cube3.addBox(-(size / 2), -(size / 2), -(size / 2), (int)size, (int)size, (int)size);
        cube3.rotateAngleZ = (float)Math.toRadians(45F);
    }

    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder iVertexBuilder, int i, int i1, float v, float v1, float v2, float v3)
    {
        cube1.render(matrixStack, iVertexBuilder, i, i1, v, v1, v2, v3);
        cube2.render(matrixStack, iVertexBuilder, i, i1, v, v1, v2, v3);
        cube3.render(matrixStack, iVertexBuilder, i, i1, v, v1, v2, v3);
    }
}
